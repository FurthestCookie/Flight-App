package se325.flights.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se325.flights.domain.*;
import se325.flights.domain.mappers.BookingMapper;
import se325.flights.domain.mappers.FlightMapper;
import se325.flights.dto.BookingRequestDTO;
import se325.flights.dto.FlightBookingDTO;
import se325.flights.dto.FlightDTO;
import se325.flights.util.SecurityUtils;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.*;

/**
 * A JAX-RS Resource class intended to contain methods with making and cancelling flight bookings, in
 * addition to retrieving information about existing flight bookings.
 */
@Path("/bookings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookingsResource {

    private static final Logger logger = LoggerFactory.getLogger(BookingsResource.class);

    /**
     * Endpoint allows an authenticated user to make a flight booking on a specific flight for one
     * or more seats.
     *
     * The header of the response provides the URL of the newly created Flight booking resource if
     * successful.
     * @param cookie
     * @param bookingRequestDTO
     * @return
     */
    @POST
    public Response makeBooking(@CookieParam("authToken") Cookie cookie, BookingRequestDTO bookingRequestDTO){

        EntityManager em = PersistenceManager.instance().createEntityManager();
        User user;
        try {
            // authorize user
            em.getTransaction().begin();
            user = SecurityUtils.getUserWithAuthToken(em,cookie);
            em.getTransaction().commit();


            // search for the flight using its id given by the input dto
            // third parameter for concurrency control -> explain in report
            em.getTransaction().begin();
            Flight flight = em.find(Flight.class, bookingRequestDTO.getFlightId(), LockModeType.PESSIMISTIC_WRITE);
            em.getTransaction().commit();

            if (flight == null) {
                // 404 status code returned and no payload
                throw new NotFoundException();
            }

            // if flight is found then make the booking
            FlightBooking flightBooking = flight.makeBooking(user,bookingRequestDTO.getRequestedSeats());

            // persist in db
            em.getTransaction().begin();
            em.persist(flightBooking);
            em.getTransaction().commit();

            // set location header to that of the new flightbooking - status code 201
            return Response.created(URI.create("/bookings/" + flightBooking.getId())).build();

        } catch (BookingException e) {
            // if seat codes are already booked then return 409 status code
            throw new WebApplicationException(Response.Status.CONFLICT);
        } catch(NotAuthorizedException e){
            // status code 401: unauthorized
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }finally {
            em.close();
        }

    }

    /**
     * Endpoint returns the details of all the flight bookings made by an authenticated user, the
     * details of the user required for the query are obtained using the authentication cookie supplied in
     * the header.
     * @param cookie
     * @return
     */
    @GET
    public List<FlightBookingDTO> getUserBookings(@CookieParam("authToken") Cookie cookie){
        EntityManager em = PersistenceManager.instance().createEntityManager();
        User user;
        try {
            // authorize user
            em.getTransaction().begin();
            user = SecurityUtils.getUserWithAuthToken(em,cookie);
            em.getTransaction().commit();

            // retrieve all the FlightBookings of the user
            Set<FlightBooking> userFlightBookings = user.getBookings();

            // convert the set to a list of FlightBookingDtos
            List<FlightBookingDTO> unsortedUserFlightBookingsDto = new ArrayList<>();
            for (FlightBooking fb : userFlightBookings){
                unsortedUserFlightBookingsDto.add(BookingMapper.toDTO(fb));
            }

            //sort the list by departure time in ascending order
            List<FlightBookingDTO> sortedFlightBookingsDto = sortCollectionByDepartureTIme(unsortedUserFlightBookingsDto);

            return sortedFlightBookingsDto;

        } finally {
            em.close();
        }
    }

    /**
     * Retrieve single flight booking details for a booking made by the authenticated user, using the flight booking id
     * specified as a query parameter.
     * @param id
     * @param cookie
     * @return
     */
    @GET
    @Path("/{id}")
    public FlightBookingDTO getSingleUserBooking(@PathParam("id") long id, @CookieParam("authToken") Cookie cookie){
        EntityManager em = PersistenceManager.instance().createEntityManager();
        User user;
        try {
            // authorize user
            em.getTransaction().begin();
            user = SecurityUtils.getUserWithAuthToken(em,cookie);
            em.getTransaction().commit();

            // jqpl query -> join on user and flightbookings for user id field
            em.getTransaction().begin();
            TypedQuery<FlightBooking> matchFlightQuery = em.createQuery(
                            "select f from FlightBooking f where f.id = :fid and f.user.id = :uid", FlightBooking.class)
                    .setParameter("fid", id)
                    .setParameter("uid", user.getId());


            List<FlightBooking> matchFlightBooking = matchFlightQuery.getResultList();

            // if it is not found then return 404 status code
            if (matchFlightBooking.isEmpty()){
                // 404
                em.getTransaction().commit();
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            em.getTransaction().commit();

            // else convert to dto and return
            // implicit 200 status code returned by jax-rs
            return BookingMapper.toDTO(matchFlightBooking.get(0));

        } finally {
            em.close();
        }
    }

    /**
     * Remove a flight booking, provided that the flight booking was initially made by the user
     * now removing it, user can only delete their bookings and the endpoint can only be accessed
     * by an authenticated user.
     * @param id
     * @param cookie
     * @return
     */
    @DELETE
    @Path("/{id}")
    public Response deleteBooking(@PathParam("id") long id, @CookieParam("authToken") Cookie cookie){

        // authorize user
        EntityManager em = PersistenceManager.instance().createEntityManager();
        User user;
        try {
            // authorize user
            em.getTransaction().begin();
            user = SecurityUtils.getUserWithAuthToken(em,cookie);
            em.getTransaction().commit();

            // check that the flight booking exists
            em.getTransaction().begin();
            FlightBooking fb = em.find(FlightBooking.class, id);
            em.getTransaction().commit();

            // if FlightBooking doesnt exist then throw not found 404
            if (fb == null){
                throw new NotFoundException();
            }

            // check that the flightbooking belongs to the user if not 404
            if (!fb.getUser().getId().equals(user.getId())){
                throw new NotFoundException();
            }

            // remove the flightBooking
            em.getTransaction().begin();
            TypedQuery<Flight> flightQuery = em.createQuery(
                            "select f from Flight f where :fid member of f.bookings", Flight.class)
                    .setParameter("fid", fb);

            List<Flight> flight = flightQuery.getResultList();

            flight.get(0).removeBooking(fb);
            em.getTransaction().commit();

            // check whether seats become available for a booking so that a response can be sent
            SubscriptionManager.instance().processSubscriptions(flight.get(0).getId());

            // return 204 status code
            return Response.noContent().build();

        } finally {
            em.close();
        }
    }


    /**
     * Helper method to sort by departure time in ascending order by default
     * @param flights
     * @return
     */
    private List<FlightBookingDTO> sortCollectionByDepartureTIme(List<FlightBookingDTO> flights){
        Collections.sort(flights, (f1, f2) -> {
            if(f1.getFlight().getDepartureTime().equals(f2.getFlight().getDepartureTime())) {
                return 0;
            }
            return f1.getFlight().getDepartureTime().isBefore(f2.getFlight().getDepartureTime()) ? -1 : 1;
        });
        return flights;
    }

}

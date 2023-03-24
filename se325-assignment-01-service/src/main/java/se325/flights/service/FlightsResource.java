package se325.flights.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se325.flights.domain.Airport;
import se325.flights.domain.Flight;
import se325.flights.domain.Seat;
import se325.flights.domain.User;
import se325.flights.domain.mappers.AircraftMapper;
import se325.flights.domain.mappers.FlightMapper;
import se325.flights.dto.AircraftTypeDTO;
import se325.flights.dto.AvailableSeatsSubscriptionDTO;
import se325.flights.dto.BookingInfoDTO;
import se325.flights.dto.FlightDTO;
import se325.flights.util.SecurityUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * A JAX-RS Resource class for retrieving information about particular flights.
 */
@Path("/flights")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FlightsResource {

    private static final Logger logger = LoggerFactory.getLogger(FlightsResource.class);

    /**
     * Endpoint which allows the client to search for flights based on origin, destination airport (partial names and codes work too)
     * as well as optionally departure date with a day buffer on either side.
     * Parameters supplied as query parameters appended to the end of the URL
     *
     */
    @GET
    public List<FlightDTO> getFlights(@QueryParam("origin") String origin,@QueryParam("destination") String destination,@QueryParam("departureDate") String departureDate,
                                      @DefaultValue("0") @QueryParam("dayRange") int dayRange) {

        // checking if destination and source airport parameters are set
        if (origin == null || destination == null){
            // status code:400 thrown
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            if (departureDate == null){

                em.getTransaction().begin();
                List<Flight> matchingFlights = em.createQuery(
                                "select f from Flight f where (lower(f.origin.name) like CONCAT('%', :origin, '%') "
                                        + "and lower(f.destination.name) like CONCAT('%', :destination, '%') or lower(f.origin.code) = :origin"
                                        + " and lower(f.destination.code) = :destination"
                                        + " or lower(f.origin.name) like CONCAT('%', :origin, '%') and lower(f.destination.code) = :destination"
                                        + " or lower(f.destination.name) like CONCAT('%', :destination, '%') and lower(f.origin.code) = :origin) order by f.departureTime asc", Flight.class)
                        .setParameter("origin", origin.toLowerCase())
                        .setParameter("destination", destination.toLowerCase())
                        .getResultList();
                em.getTransaction().commit();

                return convertToFlightDtoList(matchingFlights);
            }

            String originTimezone = getTimezoneOriginAirport(em, origin);

            // parse the departure date query to zonedDateTime based on the time zone
            ZonedDateTime[] zonedDateTimes;
            zonedDateTimes = parseDepartureDateQuery(departureDate, dayRange, originTimezone);

            // iterate through the Flights and find those within the specified departure time range using JPQL
            List<Flight> departureTimeFilter = searchFiltersWithDepartureTime(em, zonedDateTimes[0], zonedDateTimes[1], origin, destination);

            return convertToFlightDtoList(departureTimeFilter);

        } catch(DateTimeException e){
            // status code:400 thrown
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        } catch(IndexOutOfBoundsException e){
            // status code:400 thrown
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        } finally {
            em.close();
        }
    }

    /**
     * Helper method for getFlights endpoint which returns string containing the time zone of the
     * origin airport for that flight.
     * @param em
     * @param origin
     * @return
     */
    private String getTimezoneOriginAirport(EntityManager em, String origin){
        em.getTransaction().begin();
        List<Airport> originAirportTimezone = em.createQuery(
                        "select a from Airport a where lower(a.name) like CONCAT('%', :origin, '%') or lower(a.code) = :origin", Airport.class)
                .setParameter("origin", origin.toLowerCase())
                .getResultList();
        em.getTransaction().commit();

        String originTimezone = originAirportTimezone.get(0).getTimeZone();
        return originTimezone;
    }

    /**
     * Helper method for getFlights endpoint, performs jpql query on database to search for flights based
     * on source,destination airports and within the departure date range.
     * @param em
     * @param departureMin
     * @param departureMax
     * @param origin
     * @param destination
     * @return
     */
    private List<Flight> searchFiltersWithDepartureTime(EntityManager em, ZonedDateTime departureMin, ZonedDateTime departureMax,String origin, String destination){

        em.getTransaction().begin();
        TypedQuery<Flight> flightQuery = em
                .createQuery("select f from Flight f where (lower(f.origin.name) like CONCAT('%', :origin, '%') "
                        + "and lower(f.destination.name) like CONCAT('%', :destination, '%') or lower(f.origin.code) = :origin"
                        + " and lower(f.destination.code) = :destination"
                        + " or lower(f.origin.name) like CONCAT('%', :origin, '%') and lower(f.destination.code) = :destination"
                        + " or lower(f.destination.name) like CONCAT('%', :destination, '%') and lower(f.origin.code) = :origin) "
                        + "and (f.departureTime >= :startDeparture and f.departureTime <= :endDeparture) order by f.departureTime asc", Flight.class)
                .setParameter("origin", origin.toLowerCase())
                .setParameter("destination", destination.toLowerCase())
                .setParameter("startDeparture", departureMin)
                .setParameter("endDeparture", departureMax);

        List<Flight> results = flightQuery.getResultList();

        em.getTransaction().commit();

        return results;
    }

    /**
     * Endpoint to retrieve information about the bookings made for a particular flight.
     * The flight is distinguished using flight id, given as a path parameter as part
     * of URL.
     */
    @GET
    @Path("/{id}/booking-info")
    public BookingInfoDTO retrieveBookingInfo(@PathParam("id") long flightId){

        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {

            // find the corresponding flight, if not found throw 404 not found
            em.getTransaction().begin();
            Flight flight = em.find(Flight.class, flightId);
            em.getTransaction().commit();

            if (flight == null) {
                // 401 - unauthorized status code returned
                throw new NotFoundException();
            }

            Collection<String> bookedSeats = new ArrayList<>();

            // collection of seat codes parameter
            for (Seat bookedSeat : flight.getBookedSeats()){
                bookedSeats.add(bookedSeat.getSeatCode());
            }

            // parse aircraft type into dto
            AircraftTypeDTO aircraftTypeDTO = AircraftMapper.toDTO(flight.getAircraftType());

            return new BookingInfoDTO(aircraftTypeDTO,bookedSeats,flight.getSeatPricings());

        } finally {
            em.close();
        }
    }

    private List<FlightDTO> convertToFlightDtoList(List<Flight> flights){
        List<FlightDTO> flightsDto = new ArrayList<>();

        // create FlightDTO list
        for (Flight flight : flights){
            flightsDto.add(FlightMapper.toDTO(flight));
        }

        return flightsDto;
    }

    /**
     * Parses the given departure date query. If the query matches the format "YYYY-MM-DD" (e.g. "2021-08-16"), an array
     * of two {@link ZonedDateTime} instances corresponding to 00:00:00 and 23:59:59 on the given date in the given
     * timezone is returned. If dayRange is > 0, the range is expanded by (24 * dayRange) hours on either side.
     *
     * @param departureDateQuery the date / time query to parse
     * @param dayRange           the range, in days. Adds (24 * dayRange) hours on each side of the range to search.
     * @param timezone           the timezone to parse. Should come from {@link Airport#getTimeZone()}
     * @return an array of two {@link ZonedDateTime} instances, representing the beginning and end of the given date
     * in the given timezone
     * @throws DateTimeException if departureDateQuery or timezone are invalid
     */
    private ZonedDateTime[] parseDepartureDateQuery(String departureDateQuery, int dayRange, String timezone) throws DateTimeException, IndexOutOfBoundsException {

        // check that the day range value is 0 or positive
        if (dayRange < 0){
            throw new IndexOutOfBoundsException("dayRange value is invalid");
        }

        //if the dayRange variable is 0 then both the min and max date return should be the same
        LocalDate departureDate = LocalDate.parse(departureDateQuery, DateTimeFormatter.ISO_DATE);

        return new ZonedDateTime[]{
                ZonedDateTime.of(departureDate.minusDays(dayRange), LocalTime.MIN, ZoneId.of(timezone)),
                ZonedDateTime.of(departureDate.plusDays(dayRange), LocalTime.MAX, ZoneId.of(timezone))
        };
    }

    /**
     * Endpoint enables the user to be able to subscribe to be notified when seats on their specified flight become
     * available. Method implemented in an asynchronous manner, without blocking.
     * @param cookie
     * @param availableSeatsDTO
     * @param sub
     */
    @POST
    @Path("/subscribe")
    public void subscribeToFlight(@CookieParam("authToken") Cookie cookie, AvailableSeatsSubscriptionDTO availableSeatsDTO,@Suspended AsyncResponse sub){
        EntityManager em = PersistenceManager.instance().createEntityManager();
        User user;
        try {
            // authorize user
            em.getTransaction().begin();
            user = SecurityUtils.getUserWithAuthToken(em, cookie);
            em.getTransaction().commit();

            // add the user id to the dto
            availableSeatsDTO.setUserId(user.getId());

            // add subscription
            SubscriptionManager.instance().addSubscription(availableSeatsDTO,sub);

            // check if the seats are already available, use the single subscription process method
            // not the one for processing all subscriptions
            SubscriptionManager.instance().processSingleSubscription(availableSeatsDTO,sub,em);
        } finally{
            em.close();
        }
    }

}

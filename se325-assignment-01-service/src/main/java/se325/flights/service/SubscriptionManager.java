package se325.flights.service;

import se325.flights.CabinClass;
import se325.flights.domain.Flight;
import se325.flights.domain.FlightBooking;
import se325.flights.domain.Seat;
import se325.flights.domain.SeatingZone;
import se325.flights.dto.AvailableSeatsSubscriptionDTO;

import javax.persistence.EntityManager;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * A central place to hold all the subscriptions and subscription management / processing methods. Singleton so that
 * any resource instance can access the same subscription service.
 */
public class SubscriptionManager {

    private static SubscriptionManager instance;

    public static SubscriptionManager instance() {
        if (instance == null) {
            instance = new SubscriptionManager();
        }
        return instance;
    }

    private SubscriptionManager() {
    }

    /**
     * In charge of executing subscription processing
     */
    private final ExecutorService threadPool = Executors.newFixedThreadPool(5);

    /**
     * Holds all subscriptions
     */
    private final Map<AvailableSeatsSubscriptionDTO, AsyncResponse> subs = new HashMap<>();

    /**
     * Adds the given subscription to the collection of ongoing subs.
     *
     * @param subInfo info about the flight for which seats are required, the number of required seats, the required
     *                cabin class of those seats (if any), and the user who made the request
     * @param sub     the {@link AsyncResponse} object that will eventually be used to send the response back to the
     *                client
     */
    public void addSubscription(AvailableSeatsSubscriptionDTO subInfo, AsyncResponse sub) {
        this.subs.put(subInfo, sub);
    }

    /**
     * Runs through all subscriptions pertaining to the flight with the given id, and processes them. This is done on a
     * different Thread.
     *
     * @param flightId the id of the flight whose subs should be processed
     */
    public void processSubscriptions(final long flightId) {
        threadPool.submit(() -> {
            final EntityManager em = PersistenceManager.instance().createEntityManager();
            try {

                // Find all subscriptions for the given flight
                List<Map.Entry<AvailableSeatsSubscriptionDTO, AsyncResponse>> subsToProcess = subs.entrySet().stream()
                        .filter(entry -> flightId == entry.getKey().getFlightId())
                        .collect(Collectors.toList());

                // For each of those subscriptions, process them. If they were successfully processed, remove them
                // from the collection, so they won't be processed again.
                subsToProcess.forEach(entry -> {
                    if (processSingleSubscription(entry.getKey(), entry.getValue(), em)) {
                        subs.remove(entry.getKey());
                    }
                });
            } finally {
                em.close();
            }
        });
    }

    /**
     * Processes a single subscription. Search for that subscription's flight and check how many seats it has of the
     * given cabin class.
     * <p>
     * If the flight doesn't exist, resumes the AsyncResponse with a 404 response, and returns true
     * <p>
     * If the flight contains at least the required number of unbooked seats of the required cabin class (or any cabin
     * class if the provided class is null), resumes the AsyncResponse with a 204 No Content response, and returns true
     * <p>
     * Otherwise, returns false.
     *
     * @param subInfo the subscription to process
     * @param sub     the {@link AsyncResponse} to use to send a response back to the client
     * @param em      the {@link EntityManager} to use to access the database
     * @return true if the sub was successfully processed (i.e. {@link AsyncResponse#resume} was called),
     * false otherwise.
     */
    public boolean processSingleSubscription(AvailableSeatsSubscriptionDTO subInfo, AsyncResponse sub, EntityManager em) {

        // check that the flight exists otherwise return 404 status code
        em.getTransaction().begin();
        Flight flight = em.find(Flight.class,subInfo.getFlightId());
        em.getTransaction().commit();


        if (flight == null){
            // status code 404 in async response for this subscription
            sub.resume(Response.status(Response.Status.NOT_FOUND).build());
            return true;
        }

        if (subInfo.getCabinClass() == null){
            // check if the seats are available -> if cabin class is not specified
            if (checkSeatAvailabilityEntireFlight(subInfo.getNumSeats(),flight)){
                // return 204 status code in the async response
                sub.resume(Response.status(Response.Status.NO_CONTENT).build());
                return true;
            }

            return false;
        }

        // check if the seats are available -> if cabin class is specified
        if (checkSeatAvailabilityCabinClass(subInfo.getNumSeats(),flight,subInfo.getCabinClass())){
            // return 204 status code in the async response
            sub.resume(Response.status(Response.Status.NO_CONTENT).build());
            return true;
        }

        return false;
    }

    /**
     * Helper method for processSingleSubscription, takes an integer i and flight object and determines
     * if that flight has i amount of available/unbooked seats left or not.
     * @param numSeatsReq
     * @param flight
     * @return
     */
    private boolean checkSeatAvailabilityEntireFlight(int numSeatsReq, Flight flight){
        // check the number of unbooked seats on flight is equal to or greater than num of seats needed
        if (flight.getNumSeatsRemaining() >= numSeatsReq){
            return true;
        }
        return false;
    }

    /**
     * Helper method for processSingleSubscription, takes an integer i and flight object and cabin class
     * enum and determines if that specified cabin class in the flight has i amount of available/unbooked
     * seats left or not.
     * @param numSeatsReq
     * @param flight
     * @param cabinClass
     * @return
     */
    private boolean checkSeatAvailabilityCabinClass(int numSeatsReq, Flight flight, CabinClass cabinClass){
        List<String> bookedSeatCodes = seatCodesBookedSeats(flight);

        Set<SeatingZone> flightSeatingZones = flight.getAircraftType().getSeatingZones();
        List<SeatingZone> cabinClassSeatingZones = new ArrayList<>();

        // get the seating zones for the desired cabin class
        for (SeatingZone sz : flightSeatingZones){
            if (sz.getCabinClass() == cabinClass){
                // add to the seating zones of the cabin class
                cabinClassSeatingZones.add(sz);
            }
        }

        // get the total number of seats in the cabin class
        int totalSeatsCabinClass = 0;
        for (SeatingZone sz : cabinClassSeatingZones){
            totalSeatsCabinClass = totalSeatsCabinClass + sz.getNumSeatsInZone();
        }

        // get number of seats already booked in cabin class
        int bookedSeatsCabinClass = 0;
        for (SeatingZone sz : cabinClassSeatingZones){
            for (String bookedSeatCode : bookedSeatCodes){
                if (sz.isValidSeatCode(bookedSeatCode)){
                    bookedSeatsCabinClass = bookedSeatsCabinClass + 1;
                }
            }
        }

        int seatsAvailableCabinClass = totalSeatsCabinClass - bookedSeatsCabinClass;

        if (seatsAvailableCabinClass >= numSeatsReq){
            return true;
        }

        return false;

    }

    /**
     * Returns a list of seat codes of all the booked seats on a flight
     * @param flight
     * @return
     */
    private List<String> seatCodesBookedSeats(Flight flight){
        List<String> bookedSeatCodes = new ArrayList<>();

        Set<FlightBooking> flightBookings = flight.getBookings();

        for (FlightBooking fb : flightBookings){
            Set<Seat> bookedSeats = fb.getSeats();

            for (Seat seat : bookedSeats){
                bookedSeatCodes.add(seat.getSeatCode());
            }
        }

        return bookedSeatCodes;
    }
}

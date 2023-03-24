package se325.flights.domain.test;

import org.junit.jupiter.api.*;
import se325.flights.domain.*;
import se325.flights.service.PersistenceManager;
import se325.flights.util.SecurityUtils;

import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the flight booking domain model. These can be run directly from IntelliJ and don't require running any
 * particular Maven goal - but will be run automatically when Maven's test goal (and highter - package, verify,
 * install, ...) is executed.
 */
public class TestDomainModel {

    private static PersistenceManager PM;
    private EntityManager em;

    /**
     * Before we start, create the persistence manager to initialize the DB with test data.
     */
    @BeforeAll
    public static void initPersistenceManager() {
        PM = PersistenceManager.instance();
    }

    /**
     * Before we finish, make sure all connections to the DB are closed.
     */
    @AfterAll
    public static void closePersistenceManager() {
        PM.close();
    }

    /**
     * Before each test, reset the persistence manager to reset the dummy data in the DB.
     */
    @BeforeEach
    public void createEntityManager() throws BookingException {
        PM.reset();
        this.em = PM.createEntityManager();
    }

    /**
     * Close a particular JPA / Hibernate session after each test
     */
    @AfterEach
    public void resetDatabase() {
        em.close();
    }

    /**
     * Tests whether the user data added by the db-init.sql script is loaded and read successfully by our domain model
     */
    @Test
    public void testDummyUsers() {
        em.getTransaction().begin();

        List<User> users = em.createQuery("SELECT u FROM User u", User.class).getResultList();
        assertEquals(2, users.size());
        User user;

        user = users.get(0);
        assertEquals(1L, (long) user.getId());
        assertEquals("Alice", user.getUsername());
        assertTrue(SecurityUtils.matches("pa55word", user.getPassHash()));
        user = users.get(1);
        assertEquals(2L, (long) user.getId());
        assertEquals("Bob", user.getUsername());
        assertTrue(SecurityUtils.matches("12345", user.getPassHash()));

        em.getTransaction().commit();

    }

    /**
     * Tests whether the airport data added by the db-init.sql script is loaded and read successfully by our domain
     * model
     */
    @Test
    public void testDummyAirports() {
        em.getTransaction().begin();

        List<Airport> airports = em.createQuery("SELECT a FROM Airport a", Airport.class).getResultList();
        assertEquals(5, airports.size());
        Airport airport;

        airport = airports.get(0);
        assertEquals(1L, (long) airport.getId());
        assertEquals("Auckland International Airport", airport.getName());
        airport = airports.get(1);
        assertEquals(2L, (long) airport.getId());
        assertEquals("Sydney International Airport", airport.getName());
        airport = airports.get(2);
        assertEquals(3L, (long) airport.getId());
        assertEquals("Tokyo Narita International Airport", airport.getName());
        airport = airports.get(3);
        assertEquals(4L, (long) airport.getId());
        assertEquals("Singapore Changi International Airport", airport.getName());
        airport = airports.get(4);
        assertEquals(5L, (long) airport.getId());
        assertEquals("Los Angeles International Airport", airport.getName());

        em.getTransaction().commit();
    }

    /**
     * Tests whether the aircraft data added by the db-init.sql script is loaded and read successfully by our domain
     * model
     */
    @Test
    public void testDummyAircraft() {
        em.getTransaction().begin();

        List<AircraftType> aircraft = em.createQuery("SELECT a FROM AircraftType a", AircraftType.class).getResultList();
        assertEquals(2, aircraft.size());

        assertEquals(1L, (long) aircraft.get(0).getId());
        assertEquals("787-9 Dreamliner", aircraft.get(0).getName());
        assertEquals(7, aircraft.get(0).getSeatingZones().size());

        assertEquals(2L, (long) aircraft.get(1).getId());
        assertEquals("777-200ER", aircraft.get(1).getName());
        assertEquals(7, aircraft.get(1).getSeatingZones().size());

        em.getTransaction().commit();
    }

    /**
     * Tests whether the flight data added by the db-init.sql script is loaded and read successfully by our domain model
     */
    @Test
    public void testDummyFlights() {

        em.getTransaction().begin();

        List<Flight> flights = em.createQuery("SELECT f FROM Flight f", Flight.class).getResultList();
        assertEquals(72, flights.size());

        Flight flight;
        flight = flights.get(5);
        assertEquals(6L, (long) flight.getId());
        assertEquals(1L, flight.getOrigin().getId());
        assertEquals(3L, flight.getDestination().getId());
        assertEquals("JUF-567", flight.getName());
        assertEquals("777-200ER", flight.getAircraftType().getName());
        flight = flights.get(14);
        assertEquals(15L, (long) flight.getId());
        assertEquals(1L, flight.getOrigin().getId());
        assertEquals(5L, flight.getDestination().getId());
        assertEquals("IWM-268", flight.getName());
        assertEquals("787-9 Dreamliner", flight.getAircraftType().getName());
        flight = flights.get(24);
        assertEquals(25L, (long) flight.getId());
        assertEquals(2L, flight.getOrigin().getId());
        assertEquals(4L, flight.getDestination().getId());
        assertEquals("JCU-134", flight.getName());
        assertEquals("787-9 Dreamliner", flight.getAircraftType().getName());
        flight = flights.get(33);
        assertEquals(34L, (long) flight.getId());
        assertEquals(3L, flight.getOrigin().getId());
        assertEquals(1L, flight.getDestination().getId());
        assertEquals("XAO-988", flight.getName());
        assertEquals("787-9 Dreamliner", flight.getAircraftType().getName());
        flight = flights.get(40);
        assertEquals(41L, (long) flight.getId());
        assertEquals(3L, flight.getOrigin().getId());
        assertEquals(4L, flight.getDestination().getId());
        assertEquals("XSK-412", flight.getName());
        assertEquals("777-200ER", flight.getAircraftType().getName());
        flight = flights.get(46);
        assertEquals(47L, (long) flight.getId());
        assertEquals(4L, flight.getOrigin().getId());
        assertEquals(1L, flight.getDestination().getId());
        assertEquals("PKJ-347", flight.getName());
        assertEquals("777-200ER", flight.getAircraftType().getName());
        flight = flights.get(48);
        assertEquals(49L, (long) flight.getId());
        assertEquals(4L, flight.getOrigin().getId());
        assertEquals(2L, flight.getDestination().getId());
        assertEquals("DIQ-151", flight.getName());
        assertEquals("777-200ER", flight.getAircraftType().getName());
        flight = flights.get(57);
        assertEquals(58L, (long) flight.getId());
        assertEquals(4L, flight.getOrigin().getId());
        assertEquals(5L, flight.getDestination().getId());
        assertEquals("CGO-235", flight.getName());
        assertEquals("777-200ER", flight.getAircraftType().getName());
        flight = flights.get(64);
        assertEquals(65L, (long) flight.getId());
        assertEquals(5L, flight.getOrigin().getId());
        assertEquals(2L, flight.getDestination().getId());
        assertEquals("NWD-275", flight.getName());
        assertEquals("777-200ER", flight.getAircraftType().getName());

        em.getTransaction().commit();
    }

    /**
     * All tests nested here require some bookings in the database, to operate on.
     */
    @Nested
    class WithBookings {

        /**
         * Add some dummy flight booking information. This information is added here in code, to make sure the
         * {@link Flight#makeBooking(User, String...)} method is tested.
         */
        @BeforeEach
        public void makeBookings() throws BookingException {
            EntityManager em = PM.createEntityManager();

            // Book some flights
            Flight flight;
            em.getTransaction().begin();
            User user1 = em.find(User.class, 1L);
            flight = em.find(Flight.class, 13L);
            flight.makeBooking(user1, "23J", "36E", "58C", "48J", "60C");
            em.getTransaction().commit();

            em.getTransaction().begin();
            User user2 = em.find(User.class, 2L);
            flight = em.find(Flight.class, 5L);
            flight.makeBooking(user1, "52H", "51G", "42C", "35B", "40G");
            flight.makeBooking(user2, "40H", "53G", "32H", "45K", "58C");
            em.getTransaction().commit();
        }

        /**
         * Tests whether our test booking data for a flight with a single booking, can be retrieved successfully
         */
        @Test
        public void testDummySingleBooking() {
            em.getTransaction().begin();

            Flight flight = em.find(Flight.class, 13L);
            assertEquals(1, flight.getBookings().size());
            FlightBooking singleBooking = flight.getBookings().stream().findFirst().get();
            assertEquals(1L, singleBooking.getUser().getId());
            assertSame(flight, singleBooking.getFlight());
            assertEquals(5, singleBooking.getSeats().size());

            assertEquals(5, flight.getBookedSeats().size());
            assertEquals(297, flight.getNumSeatsRemaining());

            em.getTransaction().commit();
        }

        /**
         * Tests whether our test booking data for a flight with multiple bookings, can be retrieved successfully
         */
        @Test
        public void testDummyMultiBooking() {
            em.getTransaction().begin();

            Flight flight = em.find(Flight.class, 5L);
            assertEquals(2, flight.getBookings().size());

            // Test first user's booking on this flight
            FlightBooking user1Booking = flight.getBookings().stream()
                    .filter(b -> b.getUser().getId() == 1L)
                    .findFirst().get();
            assertEquals(1L, user1Booking.getUser().getId());
            assertSame(flight, user1Booking.getFlight());
            assertEquals(5, user1Booking.getSeats().size());

            // Test second user's booking on this flight
            FlightBooking user2Booking = flight.getBookings().stream()
                    .filter(b -> b.getUser().getId() == 2L)
                    .findFirst().get();
            assertEquals(2L, user2Booking.getUser().getId());
            assertSame(flight, user2Booking.getFlight());
            assertEquals(5, user2Booking.getSeats().size());

            assertEquals(10, flight.getBookedSeats().size());
            assertEquals(261, flight.getNumSeatsRemaining());

            em.getTransaction().commit();
        }

        /**
         * Tests that we can't make a booking for 0 seats
         */
        @Test
        public void testBookNoSeats() {
            em.getTransaction().begin();
            User user = em.find(User.class, 2L);
            Flight flight = em.find(Flight.class, 13L);
            try {
                flight.makeBooking(user);
                fail("Can't make a booking for 0 seats");

            } catch (BookingException e) {
                // Ensure state was not updated
                assertEquals(1, flight.getBookings().size());
                FlightBooking singleBooking = flight.getBookings().stream().findFirst().get();
                assertEquals(1L, singleBooking.getUser().getId());
                assertSame(flight, singleBooking.getFlight());
                assertEquals(5, singleBooking.getSeats().size());

                assertEquals(5, flight.getBookedSeats().size());
                assertEquals(297, flight.getNumSeatsRemaining());
            } finally {
                em.getTransaction().commit();
            }
        }

        /**
         * Tests that we can't make a booking for an invalid seat (where the seat code is completely invalid and doesn't
         * even look like a real seat code)
         */
        @Test
        public void testBookInvalidSeats() {
            em.getTransaction().begin();
            User user = em.find(User.class, 2L);
            Flight flight = em.find(Flight.class, 13L);
            try {
                flight.makeBooking(user, "FooBar");
                fail("Shouldn't be allowed to make a booking for an invalid seat");

            } catch (BookingException e) {
                // Ensure state was not updated
                assertEquals(1, flight.getBookings().size());
                FlightBooking singleBooking = flight.getBookings().stream().findFirst().get();
                assertEquals(1L, singleBooking.getUser().getId());
                assertSame(flight, singleBooking.getFlight());
                assertEquals(5, singleBooking.getSeats().size());

                assertEquals(5, flight.getBookedSeats().size());
                assertEquals(297, flight.getNumSeatsRemaining());
            } finally {
                em.getTransaction().commit();
            }
        }

        /**
         * Tests that we can't make a booking for an invalid seat (where the seat code is invalid but at least looks
         * legitimate)
         */
        @Test
        public void testBookInvalidButLegitLookingSeats() {
            em.getTransaction().begin();
            User user = em.find(User.class, 2L);
            Flight flight = em.find(Flight.class, 13L);
            try {
                flight.makeBooking(user, "500F");
                fail("Shouldn't be allowed to make a booking for an invalid seat");

            } catch (BookingException e) {
                // Ensure state was not updated
                assertEquals(1, flight.getBookings().size());
                FlightBooking singleBooking = flight.getBookings().stream().findFirst().get();
                assertEquals(1L, singleBooking.getUser().getId());
                assertSame(flight, singleBooking.getFlight());
                assertEquals(5, singleBooking.getSeats().size());

                assertEquals(5, flight.getBookedSeats().size());
                assertEquals(297, flight.getNumSeatsRemaining());
            } finally {
                em.getTransaction().commit();
            }
        }

        /**
         * Tests that we can't make a booking with any invalid seat codes, even when others are valid.
         */
        @Test
        public void testBookValidAndInvalidSeats() {
            em.getTransaction().begin();
            User user = em.find(User.class, 2L);
            Flight flight = em.find(Flight.class, 13L);
            try {
                flight.makeBooking(user, "35D", "24A", "45A", "FooBar");
                fail("Shouldn't be allowed to make a booking for a mix of valid and invalid seats");

            } catch (BookingException e) {
                // Ensure state was not updated
                assertEquals(1, flight.getBookings().size());
                FlightBooking singleBooking = flight.getBookings().stream().findFirst().get();
                assertEquals(1L, singleBooking.getUser().getId());
                assertSame(flight, singleBooking.getFlight());
                assertEquals(5, singleBooking.getSeats().size());

                assertEquals(5, flight.getBookedSeats().size());
                assertEquals(297, flight.getNumSeatsRemaining());
            } finally {
                em.getTransaction().commit();
            }
        }

        /**
         * Tests that we can't make a booking for seats which are already booked.
         */
        @Test
        public void testBookSameSeats() {
            em.getTransaction().begin();
            User user = em.find(User.class, 2L);
            Flight flight = em.find(Flight.class, 13L);
            try {
                flight.makeBooking(user, "36E");
                fail("Booking for the same seat should not be successful.");

            } catch (BookingException e) {
                // Ensure state was not updated
                assertEquals(1, flight.getBookings().size());
                FlightBooking singleBooking = flight.getBookings().stream().findFirst().get();
                assertEquals(1L, singleBooking.getUser().getId());
                assertSame(flight, singleBooking.getFlight());
                assertEquals(5, singleBooking.getSeats().size());

                assertEquals(5, flight.getBookedSeats().size());
                assertEquals(297, flight.getNumSeatsRemaining());
            } finally {
                em.getTransaction().commit();
            }
        }

        /**
         * Tests that we can't make a booking which includes any seat that's already booked, even when the booking
         * request also includes unbooked seats
         */
        @Test
        public void testBookSomeSameAndSomeDifferentSeats() {
            em.getTransaction().begin();
            User user = em.find(User.class, 2L);
            Flight flight = em.find(Flight.class, 13L);
            try {
                flight.makeBooking(user, "35D", "24A", "45A", "42C", "36E");
                fail("Booking for the same seat should not be successful.");

            } catch (BookingException e) {
                // Ensure state was not updated
                assertEquals(1, flight.getBookings().size());
                FlightBooking singleBooking = flight.getBookings().stream().findFirst().get();
                assertEquals(1L, singleBooking.getUser().getId());
                assertSame(flight, singleBooking.getFlight());
                assertEquals(5, singleBooking.getSeats().size());

                assertEquals(5, flight.getBookedSeats().size());
                assertEquals(297, flight.getNumSeatsRemaining());
            } finally {
                em.getTransaction().commit();
            }
        }
    }
}

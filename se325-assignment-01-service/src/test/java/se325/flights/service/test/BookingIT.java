package se325.flights.service.test;

import org.junit.jupiter.api.Test;
import se325.flights.dto.BookingInfoDTO;
import se325.flights.dto.BookingRequestDTO;
import se325.flights.dto.FlightBookingDTO;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests functionality related to flight bookings.
 */
public class BookingIT extends BaseIntegrationTests {

    /**
     * Tests that an authenticated user can make a booking on a flight.
     */
    @Test
    public void testMakeBooking() {
        logInAsAlice();
        makeBooking(13, "23J", "36E", "58C");
    }

    /**
     * Tests that an unauthenticated user can't make a booking. A 401 Unauthorized response should be returned.
     */
    @Test
    public void testMakeBookingFail_NotAuthenticated() {
        BookingRequestDTO request = new BookingRequestDTO(13, "23J", "36E", "58C");
        try (Response response = clientRequest("/bookings").post(Entity.json(request))) {

            assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        }
    }

    /**
     * Tests that an authenticated user can't make a booking for invalid seats. A 404 or 409 should be returned
     * (either is ok).
     */
    @Test
    public void testMakeBookingFail_InvalidSeats() {
        logInAsAlice();
        BookingRequestDTO request = new BookingRequestDTO(13, "999A");
        try (Response response = clientRequest("/bookings").post(Entity.json(request))) {

            assertTrue(Response.Status.NOT_FOUND.getStatusCode() == response.getStatus() ||
                    Response.Status.CONFLICT.getStatusCode() == response.getStatus());
        }
    }

    /**
     * Tests that an authenticated user can't make a booking for seats which have already been booked. A 404 or 409
     * should be returned (either is ok).
     */
    @Test
    public void testMakeBookingFail_AlreadyBooked() {
        logInAsAlice();
        makeBooking(13, "23J", "36E", "58C");

        BookingRequestDTO request = new BookingRequestDTO(13, "36E", "58C", "48J");
        try (Response response = clientRequest("/bookings").post(Entity.json(request))) {

            assertTrue(Response.Status.NOT_FOUND.getStatusCode() == response.getStatus() ||
                    Response.Status.CONFLICT.getStatusCode() == response.getStatus());
        }
    }

    /**
     * Tests that a booking made by an authenticated user can be retrieved. The retrieved booking should contain the
     * booked seat code info, ordered by row then by letter.
     */
    @Test
    public void testRetrieveBooking() {
        logInAsAlice();
        URI bookingLink = makeBooking(13, "23J", "36E", "58C");

        try (Response response = client.target(bookingLink).request().get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            FlightBookingDTO booking = response.readEntity(FlightBookingDTO.class);

            assertEquals(3, booking.getBookedSeats().size());
            assertEquals("23J", booking.getBookedSeats().get(0));
            assertEquals("36E", booking.getBookedSeats().get(1));
            assertEquals("58C", booking.getBookedSeats().get(2));
            assertEquals(FLIGHTS.get("IWO-222"), booking.getFlight());
            assertEquals(3375, booking.getTotalCost());
            assertNotNull(booking.getId());
        }
    }

    /**
     * Tests that a request by an authenticated user for a booking with a nonexistent id will return a 404 response.
     */
    @Test
    public void testRetrieveBookingFail_InvalidBooking() {
        logInAsAlice();

        try (Response response = clientRequest("/bookings/1").get()) {
            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        }
    }

    /**
     * Tests that a request by an unauthenticated user to retrieve a booking will return a 401 response.
     */
    @Test
    public void testRetrieveBookingFail_NotAuthenticated() {
        logInAsAlice();
        URI bookingLink = makeBooking(13, "23J", "36E", "58C");

        // Clear the auth cookie
        client.close();
        client = ClientBuilder.newClient();

        try (Response response = client.target(bookingLink).request().get()) {
            assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        }
    }

    /**
     * Tests that a request by an authenticated user for a booking other than their own booking will return a
     * 404 response.
     */
    @Test
    public void testRetrieveBookingFail_OtherUsersBooking() {
        logInAsAlice();
        URI bookingLink = makeBooking(13, "23J", "36E", "58C");

        logInAsBob();

        try (Response response = client.target(bookingLink).request().get()) {
            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        }
    }

    /**
     * Tests that we can get the bookings for an authenticated user with no current bookings. In this case, a 200 OK
     * response should be returned, with an empty booking list.
     */
    @Test
    public void testRetrieveAllBookings_EmptyList() {
        logInAsAlice();
        try (Response response = clientRequest("/bookings").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            List<FlightBookingDTO> bookings = response.readEntity(new GenericType<>() {
            });
            assertEquals(0, bookings.size());
        }
    }

    /**
     * Tests that an authenticated user correctly gets a list with a single booking if they've only made one booking
     */
    @Test
    public void testRetrieveAllBookings_SingleBooking() {
        logInAsAlice();
        makeBooking(13, "23J", "36E", "58C");

        try (Response response = clientRequest("/bookings").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            List<FlightBookingDTO> bookings = response.readEntity(new GenericType<>() {
            });
            assertEquals(1, bookings.size());

            FlightBookingDTO booking = bookings.get(0);

            assertEquals(3, booking.getBookedSeats().size());
            assertEquals("23J", booking.getBookedSeats().get(0));
            assertEquals("36E", booking.getBookedSeats().get(1));
            assertEquals("58C", booking.getBookedSeats().get(2));
            assertEquals(FLIGHTS.get("IWO-222"), booking.getFlight());
            assertEquals(3375, booking.getTotalCost());
            assertNotNull(booking.getId());
        }
    }

    /**
     * Tests that an authenticated user correctly gets a list with multiple bookings, in the correct order (ordered
     * by departure time, ascending).
     */
    @Test
    public void testRetrieveAllBookings_MultiBooking() {
        logInAsAlice();
        makeBooking(13, "23J", "36E", "58C");
        makeBooking(5, "52H", "51G", "42C");

        try (Response response = clientRequest("/bookings").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            List<FlightBookingDTO> bookings = response.readEntity(new GenericType<>() {
            });
            assertEquals(2, bookings.size());

            FlightBookingDTO booking;
            booking = bookings.get(0);
            assertEquals(3, booking.getBookedSeats().size());
            assertEquals("23J", booking.getBookedSeats().get(0));
            assertEquals("36E", booking.getBookedSeats().get(1));
            assertEquals("58C", booking.getBookedSeats().get(2));
            assertEquals(FLIGHTS.get("IWO-222"), booking.getFlight());
            assertEquals(3375, booking.getTotalCost());
            assertNotNull(booking.getId());

            booking = bookings.get(1);
            assertEquals(3, booking.getBookedSeats().size());
            assertEquals("42C", booking.getBookedSeats().get(0));
            assertEquals("51G", booking.getBookedSeats().get(1));
            assertEquals("52H", booking.getBookedSeats().get(2));
            assertEquals(FLIGHTS.get("ZWU-462"), booking.getFlight());
            assertEquals(1725, booking.getTotalCost());
            assertNotNull(booking.getId());

        }
    }

    /**
     * Tests that only the currently authenticated user's bookings will be returned in the booking list.
     */
    @Test
    public void testRetrieveAllBookings_OnlyOwnBookings() {

        logInAsAlice();
        makeBooking(13, "23J", "36E", "58C");

        logInAsBob();
        makeBooking(5, "52H", "51G", "42C");

        logInAsAlice();

        try (Response response = clientRequest("/bookings").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            List<FlightBookingDTO> bookings = response.readEntity(new GenericType<>() {
            });
            assertEquals(1, bookings.size());
            FlightBookingDTO booking = bookings.get(0);

            assertEquals(3, booking.getBookedSeats().size());
            assertEquals("23J", booking.getBookedSeats().get(0));
            assertEquals("36E", booking.getBookedSeats().get(1));
            assertEquals("58C", booking.getBookedSeats().get(2));
            assertEquals(FLIGHTS.get("IWO-222"), booking.getFlight());
            assertEquals(3375, booking.getTotalCost());
            assertNotNull(booking.getId());
        }
    }

    /**
     * Tests that we can't get bookings when a user is not authenticated. In this case, a 401 Unauthorized response
     * should be returned.
     */
    @Test
    public void testRetrieveAllBookingsFail_NotAuthenticated() {
        try (Response response = clientRequest("/bookings").get()) {
            assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        }
    }

    /**
     * Tests that a booking can be cancelled by the user who booked it. A 204 response should be returned. Also makes
     * sure that the booking is actually deleted from the system.
     */
    @Test
    public void testCancelBooking() {
        logInAsAlice();
        URI bookingLink = makeBooking(13, "23J", "36E", "58C");

        try (Response response = client.target(bookingLink).request().delete()) {
            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        }

        try (Response response = client.target(bookingLink).request().get()) {
            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        }
    }

    /**
     * Tests that getting a flight's booking info after a booking is made, will show that booking's seats as taken.
     */
    @Test
    public void testGetBookingInfoAfterBookingMade() {
        logInAsAlice();
        URI bookingLink = makeBooking(13, "23J", "36E", "58C");

        try (Response response = clientRequest("/flights/13/booking-info").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            BookingInfoDTO info = response.readEntity(BookingInfoDTO.class);

            assertEquals(3, info.getBookedSeats().size());
            assertTrue(info.getBookedSeats().contains("23J"));
            assertTrue(info.getBookedSeats().contains("36E"));
            assertTrue(info.getBookedSeats().contains("58C"));
        }
    }
}
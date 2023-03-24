package se325.flights.service.test;

import org.junit.jupiter.api.Test;
import se325.flights.dto.BookingInfoDTO;
import se325.flights.dto.FlightDTO;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests functionality related to retrieving flight information.
 */
public class FlightIT extends BaseIntegrationTests {

    /**
     * Tests that the correct flight info is returned when we search for flights by airport name. The
     * response should be a 200 OK with a list of valid {@link FlightDTO} objects. The list should
     * contain 4 flights, sorted by departure time ascending.
     */
    @Test
    public void testFlightSearch_AirportsByName() {
        try (Response response = clientRequest("/flights?origin=Auckland&destination=Sydney").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            List<FlightDTO> responseFlights = response.readEntity(new GenericType<>() {
            });
            assertEquals(4, responseFlights.size());

            assertEquals(FLIGHTS.get("EVR-976"), responseFlights.get(0));
            assertEquals(FLIGHTS.get("ABH-259"), responseFlights.get(1));
            assertEquals(FLIGHTS.get("LVT-200"), responseFlights.get(2));
            assertEquals(FLIGHTS.get("ZJS-735"), responseFlights.get(3));
        }
    }

    /**
     * Tests that the correct flight info is returned when we search for flights by airport code. The
     * response should be a 200 OK with a list of valid {@link FlightDTO} objects. The list should
     * contain 4 flights, sorted by departure time ascending.
     */
    @Test
    public void testFlightSearch_AirportsByCode() {
        try (Response response = clientRequest("/flights?origin=AKL&destination=SYD").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            List<FlightDTO> responseFlights = response.readEntity(new GenericType<>() {
            });
            assertEquals(4, responseFlights.size());

            assertEquals(FLIGHTS.get("EVR-976"), responseFlights.get(0));
            assertEquals(FLIGHTS.get("ABH-259"), responseFlights.get(1));
            assertEquals(FLIGHTS.get("LVT-200"), responseFlights.get(2));
            assertEquals(FLIGHTS.get("ZJS-735"), responseFlights.get(3));
        }
    }

    /**
     * Tests that the flight origin and destination search (by name) is case-insensitive.
     */
    @Test
    public void testFlightSearch_AirportsByName_CaseInsensitive() {
        try (Response response = clientRequest("/flights?origin=aucKlAnD&destination=sYDney").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            List<FlightDTO> responseFlights = response.readEntity(new GenericType<>() {
            });
            assertEquals(4, responseFlights.size());

            assertEquals(FLIGHTS.get("EVR-976"), responseFlights.get(0));
            assertEquals(FLIGHTS.get("ABH-259"), responseFlights.get(1));
            assertEquals(FLIGHTS.get("LVT-200"), responseFlights.get(2));
            assertEquals(FLIGHTS.get("ZJS-735"), responseFlights.get(3));
        }
    }

    /**
     * Tests that the flight origin and destination search (by code) is case-insensitive.
     */
    @Test
    public void testFlightSearch_AirportsByCode_CaseInsensitive() {
        try (Response response = clientRequest("/flights?origin=AKl&destination=SYD").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            List<FlightDTO> responseFlights = response.readEntity(new GenericType<>() {
            });
            assertEquals(4, responseFlights.size());

            assertEquals(FLIGHTS.get("EVR-976"), responseFlights.get(0));
            assertEquals(FLIGHTS.get("ABH-259"), responseFlights.get(1));
            assertEquals(FLIGHTS.get("LVT-200"), responseFlights.get(2));
            assertEquals(FLIGHTS.get("ZJS-735"), responseFlights.get(3));
        }
    }

    /**
     * Tests that a flight search can get results from multiple matching origins, in the correct order.
     * "ng" matches los aNGeles and siNGapore. This search should return 6 results.
     */
    @Test
    public void testFlightSearch_MultipleMatchingOrigins() {
        try (Response response = clientRequest("/flights?origin=ng&destination=SYD").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            List<FlightDTO> responseFlights = response.readEntity(new GenericType<>() {
            });
            assertEquals(6, responseFlights.size());

            assertEquals(FLIGHTS.get("PHZ-546"), responseFlights.get(0));
            assertEquals(FLIGHTS.get("AXT-187"), responseFlights.get(1));
            assertEquals(FLIGHTS.get("NWD-275"), responseFlights.get(2));
            assertEquals(FLIGHTS.get("NNI-190"), responseFlights.get(3));
            assertEquals(FLIGHTS.get("DIQ-151"), responseFlights.get(4));
            assertEquals(FLIGHTS.get("GHA-892"), responseFlights.get(5));
        }
    }

    /**
     * Tests that a flight search can get results from multiple matching destinations, in the correct order.
     * "ng" matches los aNGeles and siNGapore. This search should return 7 results.
     */
    @Test
    public void testFlightSearch_MultipleMatchingDestinations() {
        try (Response response = clientRequest("/flights?origin=AKL&destination=ng").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            List<FlightDTO> responseFlights = response.readEntity(new GenericType<>() {
            });
            assertEquals(7, responseFlights.size());

            assertEquals(FLIGHTS.get("IWO-222"), responseFlights.get(0));
            assertEquals(FLIGHTS.get("IWM-268"), responseFlights.get(1));
            assertEquals(FLIGHTS.get("OFM-134"), responseFlights.get(2));
            assertEquals(FLIGHTS.get("AFS-088"), responseFlights.get(3));
            assertEquals(FLIGHTS.get("UAN-776"), responseFlights.get(4));
            assertEquals(FLIGHTS.get("YCT-364"), responseFlights.get(5));
            assertEquals(FLIGHTS.get("KPF-695"), responseFlights.get(6));
        }
    }

    /**
     * Tests that a flight search with no results still returns a 200 OK response, just with an empty list.
     */
    @Test
    public void testFlightSearch_NoResults() {
        try (Response response = clientRequest("/flights?origin=foobar&destination=AKL").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            List<FlightDTO> flights = response.readEntity(new GenericType<>() {
            });
            assertEquals(0, flights.size());
        }
    }

    /**
     * Tests that a flight search with a missing origin will return a 400 response
     */
    @Test
    public void testFlightSearchFail_MissingOrigin() {
        try (Response response = clientRequest("/flights?destination=Sydney").get()) {
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        }
    }

    /**
     * Tests that a flight search with a missing destination will return a 400 response
     */
    @Test
    public void testFlightSearchWithMissingDestination() {
        try (Response response = clientRequest("/flights?origin=Auckland").get()) {
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        }
    }

    /**
     * Tests that a flight search with a departure time query will return results within 10 days of the given
     * date, in the origin's time zone, in departure date order.
     */
    @Test
    public void testFlightSearchWithDepartureTime() {
        try (Response response = clientRequest(
                "/flights?origin=AKL&destination=SYD&departureDate=2022-08-21&dayRange=10").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            List<FlightDTO> responseFlights = response.readEntity(new GenericType<>() {
            });
            assertEquals(3, responseFlights.size());
            assertEquals(FLIGHTS.get("EVR-976"), responseFlights.get(0));
            assertEquals(FLIGHTS.get("ABH-259"), responseFlights.get(1));
            assertEquals(FLIGHTS.get("LVT-200"), responseFlights.get(2));
        }
    }

    /**
     * Tests that a flight search with an invalid departure time query will return a 400 Bad Request error.
     */
    @Test
    public void testFlightSearchWithDepartureTimeFail_InvalidDate() {
        try (Response response = clientRequest(
                "/flights?origin=AKL&destination=SYD&departureDate=invalid").get()) {
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        }
    }

    /**
     * Tests that a flight search with an invalid day range query will return a 400 Bad Request error.
     */
    @Test
    public void testFlightSearchWithDepartureTimeFail_InvalidDayRange() {
        try (Response response = clientRequest(
                "/flights?origin=AKL&destination=SYD&departureDate=2022-08-21&dayRange=-1").get()) {
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        }
    }

    /**
     * Tests that we can get booking info for a flight which exists
     */
    @Test
    public void testRetrieveBookingInfo() {
        try (Response response = clientRequest("/flights/13/booking-info").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            BookingInfoDTO info = response.readEntity(BookingInfoDTO.class);

            assertEquals("787-9 Dreamliner", info.getAircraftType().getName());
            assertEquals(0, info.getBookedSeats().size());
        }
    }

    /**
     * Tests that we get a 404 error for requesting booking info for a nonexistent flight
     */
    @Test
    public void testRetrieveBookingInfoFail_NotFound() {
        try (Response response = clientRequest("/flights/999/booking-info").get()) {
            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        }
    }
}
package se325.flights.service.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import se325.flights.dto.AirportDTO;
import se325.flights.dto.BookingRequestDTO;
import se325.flights.dto.FlightDTO;
import se325.flights.dto.UserDTO;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Code that's common to all Integration Tests.
 */
public abstract class BaseIntegrationTests {

    protected static final Map<String, AirportDTO> AIRPORTS = new HashMap<>();
    protected static final Map<String, FlightDTO> FLIGHTS = new HashMap<>();

    /** The following airports and flights are auto-generated. */
    static {
        AIRPORTS.put("AKL", new AirportDTO(1L, "Auckland International Airport", "AKL", -37.008, 174.792, "Pacific/Auckland"));
        AIRPORTS.put("SYD", new AirportDTO(2L, "Sydney International Airport", "SYD", -33.946, 151.177, "Australia/Sydney"));
        AIRPORTS.put("NRT", new AirportDTO(3L, "Tokyo Narita International Airport", "NRT", 35.765, 140.386, "Asia/Tokyo"));
        AIRPORTS.put("SIN", new AirportDTO(4L, "Singapore Changi International Airport", "SIN", 1.356, 103.987, "Asia/Singapore"));
        AIRPORTS.put("LAX", new AirportDTO(5L, "Los Angeles International Airport", "LAX", 33.942, -118.408, "America/Los_Angeles"));

        FLIGHTS.put("ZJS-735", new FlightDTO(1L, "ZJS-735", time("2022-09-01T08:00:00"), AIRPORTS.get("AKL"), time("2022-09-01T11:10:00"), AIRPORTS.get("SYD"), "777-200ER"));
        FLIGHTS.put("EVR-976", new FlightDTO(2L, "EVR-976", time("2022-08-10T15:00:00"), AIRPORTS.get("AKL"), time("2022-08-10T18:10:00"), AIRPORTS.get("SYD"), "777-200ER"));
        FLIGHTS.put("LVT-200", new FlightDTO(3L, "LVT-200", time("2022-08-30T20:00:00"), AIRPORTS.get("AKL"), time("2022-08-30T23:10:00"), AIRPORTS.get("SYD"), "787-9 Dreamliner"));
        FLIGHTS.put("ABH-259", new FlightDTO(4L, "ABH-259", time("2022-08-26T05:00:00"), AIRPORTS.get("AKL"), time("2022-08-26T08:10:00"), AIRPORTS.get("SYD"), "777-200ER"));
        FLIGHTS.put("ZWU-462", new FlightDTO(5L, "ZWU-462", time("2022-09-07T05:00:00"), AIRPORTS.get("AKL"), time("2022-09-07T16:30:00"), AIRPORTS.get("NRT"), "777-200ER"));
        FLIGHTS.put("JUF-567", new FlightDTO(6L, "JUF-567", time("2022-08-29T08:00:00"), AIRPORTS.get("AKL"), time("2022-08-29T19:30:00"), AIRPORTS.get("NRT"), "777-200ER"));
        FLIGHTS.put("GNU-012", new FlightDTO(7L, "GNU-012", time("2022-08-18T10:00:00"), AIRPORTS.get("AKL"), time("2022-08-18T21:30:00"), AIRPORTS.get("NRT"), "777-200ER"));
        FLIGHTS.put("KGN-907", new FlightDTO(8L, "KGN-907", time("2022-09-03T15:00:00"), AIRPORTS.get("AKL"), time("2022-09-04T02:30:00"), AIRPORTS.get("NRT"), "777-200ER"));
        FLIGHTS.put("UAN-776", new FlightDTO(9L, "UAN-776", time("2022-08-24T03:00:00"), AIRPORTS.get("AKL"), time("2022-08-24T14:00:00"), AIRPORTS.get("SIN"), "787-9 Dreamliner"));
        FLIGHTS.put("AFS-088", new FlightDTO(10L, "AFS-088", time("2022-08-21T13:00:00"), AIRPORTS.get("AKL"), time("2022-08-22T00:00:00"), AIRPORTS.get("SIN"), "787-9 Dreamliner"));
        FLIGHTS.put("KPF-695", new FlightDTO(11L, "KPF-695", time("2022-08-29T01:00:00"), AIRPORTS.get("AKL"), time("2022-08-29T12:00:00"), AIRPORTS.get("SIN"), "777-200ER"));
        FLIGHTS.put("YCT-364", new FlightDTO(12L, "YCT-364", time("2022-08-26T03:00:00"), AIRPORTS.get("AKL"), time("2022-08-26T16:30:00"), AIRPORTS.get("LAX"), "787-9 Dreamliner"));
        FLIGHTS.put("IWO-222", new FlightDTO(13L, "IWO-222", time("2022-08-13T03:00:00"), AIRPORTS.get("AKL"), time("2022-08-13T16:30:00"), AIRPORTS.get("LAX"), "787-9 Dreamliner"));
        FLIGHTS.put("OFM-134", new FlightDTO(14L, "OFM-134", time("2022-08-17T20:00:00"), AIRPORTS.get("AKL"), time("2022-08-18T09:30:00"), AIRPORTS.get("LAX"), "787-9 Dreamliner"));
        FLIGHTS.put("IWM-268", new FlightDTO(15L, "IWM-268", time("2022-08-14T21:00:00"), AIRPORTS.get("AKL"), time("2022-08-15T10:30:00"), AIRPORTS.get("LAX"), "787-9 Dreamliner"));
        FLIGHTS.put("ECW-922", new FlightDTO(16L, "ECW-922", time("2022-08-31T18:00:00"), AIRPORTS.get("SYD"), time("2022-08-31T21:10:00"), AIRPORTS.get("AKL"), "777-200ER"));
        FLIGHTS.put("THV-097", new FlightDTO(17L, "THV-097", time("2022-09-01T13:00:00"), AIRPORTS.get("SYD"), time("2022-09-01T16:10:00"), AIRPORTS.get("AKL"), "777-200ER"));
        FLIGHTS.put("SFX-730", new FlightDTO(18L, "SFX-730", time("2022-08-23T01:00:00"), AIRPORTS.get("SYD"), time("2022-08-23T04:10:00"), AIRPORTS.get("AKL"), "777-200ER"));
        FLIGHTS.put("MMU-632", new FlightDTO(19L, "MMU-632", time("2022-08-29T11:00:00"), AIRPORTS.get("SYD"), time("2022-08-29T14:10:00"), AIRPORTS.get("AKL"), "787-9 Dreamliner"));
        FLIGHTS.put("PQM-345", new FlightDTO(20L, "PQM-345", time("2022-08-10T01:00:00"), AIRPORTS.get("SYD"), time("2022-08-10T11:10:00"), AIRPORTS.get("NRT"), "787-9 Dreamliner"));
        FLIGHTS.put("QPY-697", new FlightDTO(21L, "QPY-697", time("2022-09-04T13:00:00"), AIRPORTS.get("SYD"), time("2022-09-04T23:10:00"), AIRPORTS.get("NRT"), "787-9 Dreamliner"));
        FLIGHTS.put("HVJ-982", new FlightDTO(22L, "HVJ-982", time("2022-08-20T11:00:00"), AIRPORTS.get("SYD"), time("2022-08-20T21:10:00"), AIRPORTS.get("NRT"), "787-9 Dreamliner"));
        FLIGHTS.put("IYQ-243", new FlightDTO(23L, "IYQ-243", time("2022-09-07T05:00:00"), AIRPORTS.get("SYD"), time("2022-09-07T15:10:00"), AIRPORTS.get("NRT"), "777-200ER"));
        FLIGHTS.put("FWE-164", new FlightDTO(24L, "FWE-164", time("2022-08-25T09:00:00"), AIRPORTS.get("SYD"), time("2022-08-25T17:20:00"), AIRPORTS.get("SIN"), "787-9 Dreamliner"));
        FLIGHTS.put("JCU-134", new FlightDTO(25L, "JCU-134", time("2022-08-10T04:00:00"), AIRPORTS.get("SYD"), time("2022-08-10T12:20:00"), AIRPORTS.get("SIN"), "787-9 Dreamliner"));
        FLIGHTS.put("CDG-043", new FlightDTO(26L, "CDG-043", time("2022-09-05T19:00:00"), AIRPORTS.get("SYD"), time("2022-09-06T03:20:00"), AIRPORTS.get("SIN"), "777-200ER"));
        FLIGHTS.put("BBQ-587", new FlightDTO(27L, "BBQ-587", time("2022-08-31T08:00:00"), AIRPORTS.get("SYD"), time("2022-08-31T23:30:00"), AIRPORTS.get("LAX"), "787-9 Dreamliner"));
        FLIGHTS.put("EAH-101", new FlightDTO(28L, "EAH-101", time("2022-08-30T09:00:00"), AIRPORTS.get("SYD"), time("2022-08-31T00:30:00"), AIRPORTS.get("LAX"), "787-9 Dreamliner"));
        FLIGHTS.put("KKD-551", new FlightDTO(29L, "KKD-551", time("2022-08-23T13:00:00"), AIRPORTS.get("SYD"), time("2022-08-24T04:30:00"), AIRPORTS.get("LAX"), "787-9 Dreamliner"));
        FLIGHTS.put("OKR-295", new FlightDTO(30L, "OKR-295", time("2022-09-03T12:00:00"), AIRPORTS.get("SYD"), time("2022-09-04T03:30:00"), AIRPORTS.get("LAX"), "787-9 Dreamliner"));
        FLIGHTS.put("SGZ-100", new FlightDTO(31L, "SGZ-100", time("2022-08-09T12:00:00"), AIRPORTS.get("NRT"), time("2022-08-09T23:30:00"), AIRPORTS.get("AKL"), "777-200ER"));
        FLIGHTS.put("NTD-962", new FlightDTO(32L, "NTD-962", time("2022-08-13T17:00:00"), AIRPORTS.get("NRT"), time("2022-08-14T04:30:00"), AIRPORTS.get("AKL"), "777-200ER"));
        FLIGHTS.put("PMD-216", new FlightDTO(33L, "PMD-216", time("2022-08-26T23:00:00"), AIRPORTS.get("NRT"), time("2022-08-27T10:30:00"), AIRPORTS.get("AKL"), "777-200ER"));
        FLIGHTS.put("XAO-988", new FlightDTO(34L, "XAO-988", time("2022-09-04T16:00:00"), AIRPORTS.get("NRT"), time("2022-09-05T03:30:00"), AIRPORTS.get("AKL"), "787-9 Dreamliner"));
        FLIGHTS.put("ARQ-052", new FlightDTO(35L, "ARQ-052", time("2022-08-17T02:00:00"), AIRPORTS.get("NRT"), time("2022-08-17T12:10:00"), AIRPORTS.get("SYD"), "777-200ER"));
        FLIGHTS.put("OAM-615", new FlightDTO(36L, "OAM-615", time("2022-08-26T10:00:00"), AIRPORTS.get("NRT"), time("2022-08-26T20:10:00"), AIRPORTS.get("SYD"), "787-9 Dreamliner"));
        FLIGHTS.put("GLD-105", new FlightDTO(37L, "GLD-105", time("2022-09-04T18:00:00"), AIRPORTS.get("NRT"), time("2022-09-05T04:10:00"), AIRPORTS.get("SYD"), "787-9 Dreamliner"));
        FLIGHTS.put("TZU-411", new FlightDTO(38L, "TZU-411", time("2022-08-15T02:00:00"), AIRPORTS.get("NRT"), time("2022-08-15T12:10:00"), AIRPORTS.get("SYD"), "787-9 Dreamliner"));
        FLIGHTS.put("OES-770", new FlightDTO(39L, "OES-770", time("2022-09-02T03:00:00"), AIRPORTS.get("NRT"), time("2022-09-02T10:10:00"), AIRPORTS.get("SIN"), "777-200ER"));
        FLIGHTS.put("GPP-447", new FlightDTO(40L, "GPP-447", time("2022-08-30T05:00:00"), AIRPORTS.get("NRT"), time("2022-08-30T12:10:00"), AIRPORTS.get("SIN"), "787-9 Dreamliner"));
        FLIGHTS.put("XSK-412", new FlightDTO(41L, "XSK-412", time("2022-09-04T22:00:00"), AIRPORTS.get("NRT"), time("2022-09-05T05:10:00"), AIRPORTS.get("SIN"), "777-200ER"));
        FLIGHTS.put("WDU-047", new FlightDTO(42L, "WDU-047", time("2022-08-20T20:00:00"), AIRPORTS.get("NRT"), time("2022-08-21T07:30:00"), AIRPORTS.get("LAX"), "787-9 Dreamliner"));
        FLIGHTS.put("BCY-257", new FlightDTO(43L, "BCY-257", time("2022-09-04T10:00:00"), AIRPORTS.get("NRT"), time("2022-09-04T21:30:00"), AIRPORTS.get("LAX"), "787-9 Dreamliner"));
        FLIGHTS.put("TOL-999", new FlightDTO(44L, "TOL-999", time("2022-09-07T07:00:00"), AIRPORTS.get("NRT"), time("2022-09-07T18:30:00"), AIRPORTS.get("LAX"), "787-9 Dreamliner"));
        FLIGHTS.put("HQO-917", new FlightDTO(45L, "HQO-917", time("2022-09-03T03:00:00"), AIRPORTS.get("NRT"), time("2022-09-03T14:30:00"), AIRPORTS.get("LAX"), "787-9 Dreamliner"));
        FLIGHTS.put("VES-154", new FlightDTO(46L, "VES-154", time("2022-08-17T08:00:00"), AIRPORTS.get("SIN"), time("2022-08-17T19:00:00"), AIRPORTS.get("AKL"), "787-9 Dreamliner"));
        FLIGHTS.put("PKJ-347", new FlightDTO(47L, "PKJ-347", time("2022-08-27T07:00:00"), AIRPORTS.get("SIN"), time("2022-08-27T18:00:00"), AIRPORTS.get("AKL"), "777-200ER"));
        FLIGHTS.put("OFB-133", new FlightDTO(48L, "OFB-133", time("2022-08-11T13:00:00"), AIRPORTS.get("SIN"), time("2022-08-12T00:00:00"), AIRPORTS.get("AKL"), "787-9 Dreamliner"));
        FLIGHTS.put("DIQ-151", new FlightDTO(49L, "DIQ-151", time("2022-08-22T04:00:00"), AIRPORTS.get("SIN"), time("2022-08-22T12:20:00"), AIRPORTS.get("SYD"), "777-200ER"));
        FLIGHTS.put("NNI-190", new FlightDTO(50L, "NNI-190", time("2022-08-19T16:00:00"), AIRPORTS.get("SIN"), time("2022-08-20T00:20:00"), AIRPORTS.get("SYD"), "777-200ER"));
        FLIGHTS.put("PHZ-546", new FlightDTO(51L, "PHZ-546", time("2022-08-14T16:00:00"), AIRPORTS.get("SIN"), time("2022-08-15T00:20:00"), AIRPORTS.get("SYD"), "777-200ER"));
        FLIGHTS.put("XVV-873", new FlightDTO(52L, "XVV-873", time("2022-08-16T19:00:00"), AIRPORTS.get("SIN"), time("2022-08-17T02:10:00"), AIRPORTS.get("NRT"), "787-9 Dreamliner"));
        FLIGHTS.put("WWA-805", new FlightDTO(53L, "WWA-805", time("2022-09-06T03:00:00"), AIRPORTS.get("SIN"), time("2022-09-06T10:10:00"), AIRPORTS.get("NRT"), "777-200ER"));
        FLIGHTS.put("NXY-477", new FlightDTO(54L, "NXY-477", time("2022-08-22T04:00:00"), AIRPORTS.get("SIN"), time("2022-08-22T11:10:00"), AIRPORTS.get("NRT"), "777-200ER"));
        FLIGHTS.put("YXO-292", new FlightDTO(55L, "YXO-292", time("2022-08-12T03:00:00"), AIRPORTS.get("SIN"), time("2022-08-12T10:10:00"), AIRPORTS.get("NRT"), "787-9 Dreamliner"));
        FLIGHTS.put("RJW-903", new FlightDTO(56L, "RJW-903", time("2022-08-26T06:00:00"), AIRPORTS.get("SIN"), time("2022-08-27T00:00:00"), AIRPORTS.get("LAX"), "777-200ER"));
        FLIGHTS.put("ADA-507", new FlightDTO(57L, "ADA-507", time("2022-08-11T00:00:00"), AIRPORTS.get("SIN"), time("2022-08-11T18:00:00"), AIRPORTS.get("LAX"), "777-200ER"));
        FLIGHTS.put("CGO-235", new FlightDTO(58L, "CGO-235", time("2022-08-20T23:00:00"), AIRPORTS.get("SIN"), time("2022-08-21T17:00:00"), AIRPORTS.get("LAX"), "777-200ER"));
        FLIGHTS.put("RRD-003", new FlightDTO(59L, "RRD-003", time("2022-08-19T22:00:00"), AIRPORTS.get("LAX"), time("2022-08-20T11:30:00"), AIRPORTS.get("AKL"), "787-9 Dreamliner"));
        FLIGHTS.put("ZUY-474", new FlightDTO(60L, "ZUY-474", time("2022-08-15T12:00:00"), AIRPORTS.get("LAX"), time("2022-08-16T01:30:00"), AIRPORTS.get("AKL"), "787-9 Dreamliner"));
        FLIGHTS.put("IKU-951", new FlightDTO(61L, "IKU-951", time("2022-09-05T10:00:00"), AIRPORTS.get("LAX"), time("2022-09-05T23:30:00"), AIRPORTS.get("AKL"), "787-9 Dreamliner"));
        FLIGHTS.put("XJZ-467", new FlightDTO(62L, "XJZ-467", time("2022-08-29T11:00:00"), AIRPORTS.get("LAX"), time("2022-08-30T00:30:00"), AIRPORTS.get("AKL"), "777-200ER"));
        FLIGHTS.put("AXT-187", new FlightDTO(63L, "AXT-187", time("2022-08-17T08:00:00"), AIRPORTS.get("LAX"), time("2022-08-17T23:30:00"), AIRPORTS.get("SYD"), "787-9 Dreamliner"));
        FLIGHTS.put("GHA-892", new FlightDTO(64L, "GHA-892", time("2022-09-02T00:00:00"), AIRPORTS.get("LAX"), time("2022-09-02T15:30:00"), AIRPORTS.get("SYD"), "777-200ER"));
        FLIGHTS.put("NWD-275", new FlightDTO(65L, "NWD-275", time("2022-08-17T16:00:00"), AIRPORTS.get("LAX"), time("2022-08-18T07:30:00"), AIRPORTS.get("SYD"), "777-200ER"));
        FLIGHTS.put("ZLX-146", new FlightDTO(66L, "ZLX-146", time("2022-08-15T16:00:00"), AIRPORTS.get("LAX"), time("2022-08-16T03:30:00"), AIRPORTS.get("NRT"), "787-9 Dreamliner"));
        FLIGHTS.put("BPR-470", new FlightDTO(67L, "BPR-470", time("2022-08-27T07:00:00"), AIRPORTS.get("LAX"), time("2022-08-27T18:30:00"), AIRPORTS.get("NRT"), "787-9 Dreamliner"));
        FLIGHTS.put("BQY-363", new FlightDTO(68L, "BQY-363", time("2022-08-26T16:00:00"), AIRPORTS.get("LAX"), time("2022-08-27T03:30:00"), AIRPORTS.get("NRT"), "787-9 Dreamliner"));
        FLIGHTS.put("PEE-279", new FlightDTO(69L, "PEE-279", time("2022-08-27T22:00:00"), AIRPORTS.get("LAX"), time("2022-08-28T16:00:00"), AIRPORTS.get("SIN"), "787-9 Dreamliner"));
        FLIGHTS.put("DNU-037", new FlightDTO(70L, "DNU-037", time("2022-09-01T16:00:00"), AIRPORTS.get("LAX"), time("2022-09-02T10:00:00"), AIRPORTS.get("SIN"), "787-9 Dreamliner"));
        FLIGHTS.put("PIH-197", new FlightDTO(71L, "PIH-197", time("2022-08-29T12:00:00"), AIRPORTS.get("LAX"), time("2022-08-30T06:00:00"), AIRPORTS.get("SIN"), "777-200ER"));
        FLIGHTS.put("MOR-720", new FlightDTO(72L, "MOR-720", time("2022-08-21T08:00:00"), AIRPORTS.get("LAX"), time("2022-08-22T02:00:00"), AIRPORTS.get("SIN"), "777-200ER"));
    }

    protected static final ZonedDateTime time(String t) {
        return ZonedDateTime.parse(t + "+00:00", DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }

    protected static final String WEB_SERVICE_URI = "http://localhost:10000/services";

    protected Client client;

    /**
     * Runs before each unit test to create the web service client, and send a test request which will force-re-init
     * the database. This ensures each unit test is starting with a clean playing field.
     */
    @BeforeEach
    public void setUp() {
        client = ClientBuilder.newClient();

        Response response = clientRequest("/test/reset-db").delete();

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    /**
     * After each test, close the client to clear any leftover auth cookie data
     */
    @AfterEach
    public void tearDown() {
        client.close();
        client = null;
    }

    /** Authorizes as user "Alice" */
    protected void logInAsAlice() { logInAs("Alice", "pa55word"); }

    /** Authorizes as user "Bob" */
    protected void logInAsBob() { logInAs("Bob", "12345"); }

    /**
     * Authorizes as a user with the given username and password. Asserts that the given server response is a 204
     * No Content response, with an authToken cookie in the response header.
     *
     * @param username the username to authenticate
     * @param password the password to authenticate
     */
    protected void logInAs(String username, String password) {
        logInAs(username, password, client);
    }

    /**
     * Authorizes as a user with the given username and password. Asserts that the given server response is a 204
     * No Content response, with an authToken cookie in the response header.
     *
     * @param username the username to authenticate
     * @param password the password to authenticate
     * @param client   the client to use to send the request
     */
    protected void logInAs(String username, String password, Client client) {
        UserDTO user = new UserDTO(username, password);
        try (Response response = clientRequest(client, "/users/login").post(Entity.json(user))) {

            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
            assertNotNull(response.getCookies().get("authToken"));
        }
    }

    /**
     * A shorthand for writing client.target(WEB_SERVICE_URI + path).request()...
     *
     * @param path the path to append to {@link #WEB_SERVICE_URI}
     * @return the {@link Invocation.Builder} used to make a web request
     */
    protected Invocation.Builder clientRequest(String path) {
        return clientRequest(client, path);
    }

    /**
     * A shorthand for writing client.target(WEB_SERVICE_URI + path).request()...
     *
     * @param client the client to invoke
     * @param path   the path to append to {@link #WEB_SERVICE_URI}
     * @return the {@link Invocation.Builder} used to make a web request
     */
    protected Invocation.Builder clientRequest(Client client, String path) {
        return client.target(WEB_SERVICE_URI + path).request();
    }

    /**
     * Sends a request to the server to make a booking on the given flight, for the given seats. Checks the server
     * Response to make sure it is a 201 Created response, with a valid Location. Returns the URI pointing to the
     * booking.
     *
     * @param flightId the id of the flight to book
     * @param seats    the seats to book
     */
    protected URI makeBooking(long flightId, String... seats) {
        BookingRequestDTO request = new BookingRequestDTO(flightId, seats);
        try (Response response = clientRequest("/bookings").post(Entity.json(request))) {

            assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
            assertNotNull(response.getLocation());
            assertTrue(response.getLocation().toString().contains("/bookings/"));
            return response.getLocation();
        }
    }
}
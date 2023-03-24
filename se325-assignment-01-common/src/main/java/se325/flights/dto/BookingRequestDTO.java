package se325.flights.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * A DTO for a booking request for some number of seats on a particular flight.
 */
public class BookingRequestDTO {

    private long flightId;
    private List<String> requestedSeats = new ArrayList<>();

    public BookingRequestDTO() {

    }

    public BookingRequestDTO(long flightId, String... seats) {
        this.flightId = flightId;
        this.requestedSeats.addAll(List.of(seats));
    }

    public long getFlightId() {
        return flightId;
    }

    public void setFlightId(long flightId) {
        this.flightId = flightId;
    }

    public List<String> getRequestedSeats() {
        return requestedSeats;
    }

    public void setRequestedSeats(List<String> requestedSeats) {
        this.requestedSeats = requestedSeats;
    }
}

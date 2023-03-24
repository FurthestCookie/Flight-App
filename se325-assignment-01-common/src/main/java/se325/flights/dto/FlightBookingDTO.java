package se325.flights.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents information about a particular booking on a flight.
 */
public class FlightBookingDTO {

    private Long id;
    private FlightDTO flight;
    private List<String> bookedSeats = new ArrayList<>();
    private int totalCost;

    public FlightBookingDTO(){}

    public FlightBookingDTO(Long id, FlightDTO flight, List<String> bookedSeats) {
        this.id = id;
        this.flight = flight;
        this.bookedSeats = bookedSeats;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FlightDTO getFlight() {
        return flight;
    }

    public void setFlight(FlightDTO flight) {
        this.flight = flight;
    }

    public List<String> getBookedSeats() {
        return bookedSeats;
    }

    public void setBookedSeats(List<String> bookedSeats) {
        this.bookedSeats = bookedSeats;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(int totalCost) {
        this.totalCost = totalCost;
    }

}

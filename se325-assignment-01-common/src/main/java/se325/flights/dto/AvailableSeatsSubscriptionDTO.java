package se325.flights.dto;

import se325.flights.CabinClass;

import java.util.Objects;

/**
 * Represents a request to be notified when a particular flight has some number of seats available in a particular class.
 *
 * Note: You don't need to set the user id from the client-side. The server can set this on authentication.
 */
public class AvailableSeatsSubscriptionDTO {

    private long flightId;
    private CabinClass cabinClass;
    private int numSeats;
    private Long userId;

    public AvailableSeatsSubscriptionDTO() {
    }

    public AvailableSeatsSubscriptionDTO(long flightId, CabinClass cabinClass, int numSeats) {
        this.flightId = flightId;
        this.cabinClass = cabinClass;
        this.numSeats = numSeats;
    }

    public long getFlightId() {
        return flightId;
    }

    public void setFlightId(long flightId) {
        this.flightId = flightId;
    }

    public CabinClass getCabinClass() {
        return cabinClass;
    }

    public void setCabinClass(CabinClass cabinClass) {
        this.cabinClass = cabinClass;
    }

    public int getNumSeats() {
        return numSeats;
    }

    public void setNumSeats(int numSeats) {
        this.numSeats = numSeats;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AvailableSeatsSubscriptionDTO that = (AvailableSeatsSubscriptionDTO) o;
        return flightId == that.flightId && numSeats == that.numSeats && cabinClass == that.cabinClass && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flightId, cabinClass, numSeats, userId);
    }
}

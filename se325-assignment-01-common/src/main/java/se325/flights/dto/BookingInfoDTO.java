package se325.flights.dto;

import se325.flights.CabinClass;

import java.util.*;

/**
 * A DTO containing info about the seats which have already been booked on a flight, and the pricing for remaining
 * seats. This is the info a user might need when deciding whether / what seats to book on a flight.
 */
public class BookingInfoDTO {

    private AircraftTypeDTO aircraftType;
    private List<String> bookedSeats;
    private Map<CabinClass, Integer> pricingInfo;

    public BookingInfoDTO() {}

    public BookingInfoDTO(AircraftTypeDTO aircraftType, Collection<String> bookedSeats, Map<CabinClass, Integer> pricingInfo) {
        this.aircraftType = aircraftType;
        this.bookedSeats = new ArrayList<>(bookedSeats);
        this.pricingInfo = new HashMap<>(pricingInfo);
    }

    public AircraftTypeDTO getAircraftType() {
        return aircraftType;
    }

    public void setAircraftType(AircraftTypeDTO aircraftType) {
        this.aircraftType = aircraftType;
    }

    public List<String> getBookedSeats() {
        return bookedSeats;
    }

    public void setBookedSeats(List<String> bookedSeats) {
        this.bookedSeats = bookedSeats;
    }

    public Map<CabinClass, Integer> getPricingInfo() {
        return pricingInfo;
    }

    public void setPricingInfo(Map<CabinClass, Integer> pricingInfo) {
        this.pricingInfo = pricingInfo;
    }
}

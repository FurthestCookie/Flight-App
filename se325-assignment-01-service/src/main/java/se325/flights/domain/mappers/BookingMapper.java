package se325.flights.domain.mappers;

import se325.flights.domain.FlightBooking;
import se325.flights.domain.Seat;
import se325.flights.dto.FlightBookingDTO;

import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * A mapper to convert between {@link FlightBooking} and {@link FlightBookingDTO} instances
 */
public class BookingMapper {

    public static FlightBookingDTO toDTO(FlightBooking domainBooking) {
        FlightBookingDTO dto = new FlightBookingDTO(
                domainBooking.getId(),
                FlightMapper.toDTO(domainBooking.getFlight()),
                domainBooking.getSeats().stream()
                        .sorted(Comparator.naturalOrder())
                        .map(Seat::getSeatCode)
                        .collect(Collectors.toList())
        );

        dto.setTotalCost(domainBooking.getSeats().stream().mapToInt(Seat::getPrice).sum());
        return dto;
    }

}

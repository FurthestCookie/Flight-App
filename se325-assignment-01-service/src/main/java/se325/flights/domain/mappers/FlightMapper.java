package se325.flights.domain.mappers;

import se325.flights.domain.Flight;
import se325.flights.domain.Seat;
import se325.flights.dto.BookingInfoDTO;
import se325.flights.dto.FlightDTO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A mapper to convert between {@link Flight} and {@link FlightDTO} or {@link BookingInfoDTO} instances
 */
public class FlightMapper {

    public static FlightDTO toDTO(Flight domainFlight) {
        return new FlightDTO(
                domainFlight.getId(),
                domainFlight.getName(),
                domainFlight.getDepartureTime(),
                AirportMapper.toDTO(domainFlight.getOrigin()),
                domainFlight.getArrivalTime(),
                AirportMapper.toDTO(domainFlight.getDestination()),
                domainFlight.getAircraftType().getName()
        );
    }

    public static BookingInfoDTO toBookingInfoDTO(Flight domainFlight) {
        return new BookingInfoDTO(
                AircraftMapper.toDTO(domainFlight.getAircraftType()),
                domainFlight.getBookedSeats().stream().map(Seat::getSeatCode).collect(Collectors.toList()),
                domainFlight.getSeatPricings()
        );
    }
}

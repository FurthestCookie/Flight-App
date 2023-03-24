package se325.flights.domain.mappers;

import se325.flights.domain.Airport;
import se325.flights.dto.AirportDTO;

/**
 * A mapper to convert between {@link Airport} and {@link AirportDTO} instances
 */
public class AirportMapper {

    public static AirportDTO toDTO(Airport domainAirport) {
        return new AirportDTO(
                domainAirport.getId(),
                domainAirport.getName(),
                domainAirport.getCode(),
                domainAirport.getLatitude(),
                domainAirport.getLongitude(),
                domainAirport.getTimeZone()
        );
    }
}

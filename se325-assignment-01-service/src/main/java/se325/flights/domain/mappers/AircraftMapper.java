package se325.flights.domain.mappers;

import se325.flights.domain.AircraftType;
import se325.flights.dto.AircraftTypeDTO;

/**
 * A mapper to convert between {@link AircraftType} and {@link AircraftTypeDTO} instances
 */
public class AircraftMapper {

    public static AircraftTypeDTO toDTO(AircraftType domainAT) {
        return new AircraftTypeDTO(
                domainAT.getId(),
                domainAT.getName()
        );
    }
}

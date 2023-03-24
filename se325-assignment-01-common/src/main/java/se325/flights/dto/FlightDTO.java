package se325.flights.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import se325.flights.jackson.ZonedDateTimeDeserializer;
import se325.flights.jackson.ZonedDateTimeSerializer;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO with info about a flight, including where (and when) the flight arrives / departs.
 */
public class FlightDTO {

    private Long id;
    private String name;

    private ZonedDateTime departureTime;
    private AirportDTO origin;

    private ZonedDateTime arrivalTime;
    private AirportDTO destination;

    private String aircraftName;

    public FlightDTO() {
    }

    public FlightDTO(Long id, String name, ZonedDateTime departureTime, AirportDTO origin, ZonedDateTime arrivalTime, AirportDTO destination, String aircraftName) {
        this.id = id;
        this.name = name;
        this.departureTime = departureTime;
        this.origin = origin;
        this.arrivalTime = arrivalTime;
        this.destination = destination;
        this.aircraftName = aircraftName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    public ZonedDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(ZonedDateTime departureTime) {
        this.departureTime = departureTime;
    }

    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    public ZonedDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(ZonedDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getAircraftName() {
        return aircraftName;
    }

    public void setAircraftName(String aircraftName) {
        this.aircraftName = aircraftName;
    }

    public AirportDTO getOrigin() {
        return origin;
    }

    public void setOrigin(AirportDTO origin) {
        this.origin = origin;
    }

    public AirportDTO getDestination() {
        return destination;
    }

    public void setDestination(AirportDTO destination) {
        this.destination = destination;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlightDTO flightDTO = (FlightDTO) o;
        return Objects.equals(id, flightDTO.id) && Objects.equals(name, flightDTO.name) && departureTime.isEqual(flightDTO.departureTime) && Objects.equals(origin, flightDTO.origin) && arrivalTime.isEqual(flightDTO.arrivalTime) && Objects.equals(destination, flightDTO.destination) && Objects.equals(aircraftName, flightDTO.aircraftName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, departureTime, origin, arrivalTime, destination, aircraftName);
    }
}

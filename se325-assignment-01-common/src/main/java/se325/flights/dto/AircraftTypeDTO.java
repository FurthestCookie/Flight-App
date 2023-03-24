package se325.flights.dto;

/**
 * A DTO with enough info for a client to identify the kind of aircraft used on a flight.
 */
public class AircraftTypeDTO {

    private Long id;
    private String name;

    public AircraftTypeDTO() {

    }

    public AircraftTypeDTO(Long id, String name) {
        this.id = id;
        this.name = name;
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
}

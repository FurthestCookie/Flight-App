package se325.flights.dto;

import java.util.Objects;

/**
 * A DTO with info about origins / destinations for flights.
 */
public class AirportDTO {

    private Long id;
    private String name;
    private String code;
    private double latitude;
    private double longitude;
    private String timeZone;

    public AirportDTO() {

    }

    public AirportDTO(Long id, String name, String code, double latitude, double longitude, String timeZone) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timeZone = timeZone;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AirportDTO that = (AirportDTO) o;
        return Math.abs(that.latitude - latitude) < 1e-10 &&
                Math.abs(that.longitude - longitude) < 1e-10 &&
                Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(code, that.code) &&
                Objects.equals(timeZone, that.timeZone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, code, timeZone);
    }
}

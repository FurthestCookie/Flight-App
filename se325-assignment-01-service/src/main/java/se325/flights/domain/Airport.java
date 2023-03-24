package se325.flights.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Represents an Airport. Airports can be the origin / destination of {@link Flight}s.
 */
@Entity
public class Airport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String code;
    private double latitude;
    private double longitude;
    private String timeZone;

    /**
     * Default constructor, required by JPA / Hibernate
     */
    public Airport() {

    }

    /**
     * Creates a new Airport instance
     *
     * @param name      the airport's name (e.g. "Auckland International Airport")
     * @param code      the airport's code (e.g. "AKL")
     * @param latitude  the airport's latitude
     * @param longitude the airport's longitude
     * @param timeZone  the airport's timezone (e.g. "Pacific/Auckland"). For a list of available timezones,
     *                  see <a href="https://en.wikipedia.org/wiki/List_of_tz_database_time_zones">this Wikipedia page.</a>
     */
    public Airport(String name, String code, double latitude, double longitude, String timeZone) {
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
}

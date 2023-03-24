package se325.flights.domain;

import org.hibernate.annotations.FetchMode;
import se325.flights.CabinClass;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a type of aircraft on which a {@link Flight} can be made.
 */
@Entity
public class AircraftType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ElementCollection
    private Set<SeatingZone> seatingZones = new HashSet<>();

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

    public Set<SeatingZone> getSeatingZones() {
        return seatingZones;
    }

    /**
     * Gets the total number of seats on this aircraft.
     *
     * @return the total number of seats, calculated by summing the values of {@link SeatingZone#getNumSeatsInZone()}
     * for all zones on this aircraft.
     */
    public int getTotalNumSeats() {
        return getTotalNumSeats(null);
    }

    /**
     * Gets the total number of seats on this aircraft in the given cabin class. If the given cabin class is null,
     * returns the total number of seats of all classes.
     *
     * @return the total number of seats, calculated by summing the values of {@link SeatingZone#getNumSeatsInZone()}
     * for all zones of the given cabin class on this aircraft.
     */
    public int getTotalNumSeats(CabinClass cabinClass) {
        return this.seatingZones.stream()
                .filter(z -> cabinClass == null || z.getCabinClass().equals(cabinClass))
                .map(SeatingZone::getNumSeatsInZone)
                .reduce(0, Integer::sum);
    }

    /**
     * Gets the cabin class (e.g. Business, Economy) for the seat with the given seat code on this aircraft.
     *
     * @param seatCode the seat code to check
     * @return the seat's {@link CabinClass}
     * @throws BookingException if a seat with the given code doesn't exist on this aircraft
     */
    public CabinClass getCabinClass(String seatCode) throws BookingException {
        for (SeatingZone zone : seatingZones) {
            if (zone.isValidSeatCode(seatCode)) {
                return zone.getCabinClass();
            }
        }
        throw new BookingException("Seat with the given code not found on this aircraft");
    }
}

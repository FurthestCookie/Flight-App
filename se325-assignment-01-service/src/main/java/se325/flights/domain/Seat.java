package se325.flights.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import java.util.Objects;

/**
 * Represents a single Seat in a {@link FlightBooking}. Unbooked seats do not have associated Seat instances in the
 * database, only booked ones.
 */
@Embeddable
@Access(AccessType.FIELD)
public class Seat implements Comparable<Seat> {

    private int rowNumber;
    private String letterCode;
    private int price;

    /**
     * Default constructor, required by JPA / Hibernate
     */
    public Seat() {

    }

    public Seat(String seatCode, int price) {
        this.rowNumber = Integer.parseInt(seatCode.substring(0, seatCode.length() - 1));
        this.letterCode = "" + seatCode.charAt(seatCode.length() - 1);
        this.price = price;
    }

    /**
     * Creates a new Seat instance with the given row number, letter code, and price.
     *
     * @param rowNumber  the row number, e.g. 1, 2, 3
     * @param letterCode the letter code, e.g. A, B, C
     * @param price      The price to book this seat
     */
    public Seat(int rowNumber, String letterCode, int price) {
        this.rowNumber = rowNumber;
        this.letterCode = letterCode;
        this.price = price;
    }

    /**
     * Gets the seat code, which is a combination of the row number and letter code.
     *
     * @return
     */
    public String getSeatCode() {
        return rowNumber + letterCode;
    }

    public int getPrice() {
        return price;
    }

    /**
     * Gets a value indicating whether the given object is equal to this Seat. The given object is considered equal if
     * it is also a Seat instance, with the same row number, letter code, and price.
     *
     * @param other the object to check
     * @return true if the object is equal to this one, false otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Seat seat = (Seat) other;
        return rowNumber == seat.rowNumber && price == seat.price && letterCode.equals(seat.letterCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rowNumber, letterCode, price);
    }

    /**
     * Seat's natural ordering is by row number, then by letter code.
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(Seat o) {
        if (rowNumber == o.rowNumber) {
            return letterCode.compareTo(o.letterCode);
        }
        return Integer.compare(rowNumber, o.rowNumber);
    }
}
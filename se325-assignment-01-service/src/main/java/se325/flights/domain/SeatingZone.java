package se325.flights.domain;

import se325.flights.CabinClass;

import javax.persistence.*;
import java.util.Objects;

/**
 * Represents a particular seating zone on an {@link AircraftType}. A zone is a number of rows of seats on a plane,
 * which share a {@link CabinClass}.
 */
@Embeddable
@Access(AccessType.FIELD)
public class SeatingZone {

    private int startingRowNumber;
    private int numRows;
    @Enumerated(EnumType.STRING)
    private CabinClass cabinClass;
    private String seatCodeData;

    public int getStartingRowNumber() {
        return startingRowNumber;
    }

    public int getNumRows() {
        return numRows;
    }

    public CabinClass getCabinClass() {
        return cabinClass;
    }

    /**
     * Gets the seat code data for the zone. This is a string where each single character in the string is a particular
     * letter code for a seat which, combined with a row number, will form a valid seat code.
     * <p>
     * For example, if {@link #getStartingRowNumber()} is 1, and {@link #getNumRows()} is 2, then the valid row numbers
     * in this seating zone are 1 and 2. And if getSeatCodeData() is "ABC", then the valid letter codes are
     * "A", "B", and "C". Therefore, there would be six valid seat codes in this zone under those circumstances:
     * <p>
     * "1A", "1B", "1C", "2A", "2B", "2C".
     *
     * @return the seat code data for this zone
     */
    public String getSeatCodeData() {
        return seatCodeData;
    }

    public int getNumSeatsInZone() {
        return numRows * seatCodeData.length();
    }

    /**
     * Gets a value indicating whether the given seat code is a valid seat code in this seating zone.
     *
     * @param seatCode the code to check
     * @return true if the code is valid, false otherwise
     */
    public boolean isValidSeatCode(String seatCode) {
        try {
            int rowNumber = Integer.parseInt(seatCode.substring(0, seatCode.length() - 1));
            String letter = seatCode.substring(seatCode.length() - 1);

            boolean validRow = rowNumber >= startingRowNumber && rowNumber < startingRowNumber + numRows;
            boolean validColumn = seatCodeData.contains(letter);

            return validRow && validColumn;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Gets a value indicating whether the given object is equal to this one. An object is equal if it is also a
     * SeatingZone, with the same starting row number, number of rows, and seat code data.
     *
     * @param other the object to check
     * @return true if the object is equal, false otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        SeatingZone that = (SeatingZone) other;
        return startingRowNumber == that.startingRowNumber && numRows == that.numRows && cabinClass == that.cabinClass && seatCodeData.equals(that.seatCodeData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startingRowNumber, numRows, cabinClass, seatCodeData);
    }
}

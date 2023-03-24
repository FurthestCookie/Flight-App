package se325.flights.domain;

import se325.flights.CabinClass;

import javax.persistence.*;
import java.util.Objects;

/**
 * Represents the seat pricing for a particular {@link CabinClass} on a partcular {@link Flight}.
 */
@Embeddable
@Access(AccessType.FIELD)
public class SeatPricing {
    @Enumerated(EnumType.STRING)
    private CabinClass cabinClass;
    private int price;

    public CabinClass getCabinClass() {
        return cabinClass;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    /**
     * Gets a value indicating whether the given object is equal to this one. The object is considered equal if it is
     * also a SeatPricing instance, with the same cabinClass and price.
     *
     * @param other the object to check
     * @return true if the object is equal, false otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        SeatPricing that = (SeatPricing) other;
        return price == that.price && cabinClass == that.cabinClass;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cabinClass, price);
    }
}

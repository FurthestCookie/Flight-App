package se325.flights.domain;

/**
 * Represents an exception that might be thrown when trying to book a {@link Flight}.
 */
public class BookingException extends Exception {

    public BookingException(String message) {
        super(message);
    }
}

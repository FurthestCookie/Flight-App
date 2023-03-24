package se325.flights.service;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * JAX-RS Application class. JAX-RS creates an instance of all {@link Application} subclasses it finds, in order to
 * configure itself.
 */
@ApplicationPath("/services")
public class FlightBookingApplication extends Application {

    private final Set<Class<?>> classes = new HashSet<>();
    private final Set<Object> singletons = new HashSet<>();

    public FlightBookingApplication() {

        singletons.add(PersistenceManager.instance());
        singletons.add(SubscriptionManager.instance());

        classes.add(TestResource.class);
        classes.add(UserResource.class);
        classes.add(FlightsResource.class);
        classes.add(BookingsResource.class);

    }

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}

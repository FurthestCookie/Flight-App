package se325.flights.service;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;

/**
 * A Jax-RS resource only for testing purposes. When this resource's single method is called, it will reset the
 * persistence context which, according to the config in persistence.xml, will cause the database to be deleted and
 * re-created. This allows us to start each unit / integration test with the same data.
 */
@Path("/test")
public class TestResource {

    @Path("/reset-db")
    @DELETE
    public void resetDatabase() {
        PersistenceManager.instance().reset();
    }
}

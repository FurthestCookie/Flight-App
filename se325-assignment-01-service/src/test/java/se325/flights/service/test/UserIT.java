package se325.flights.service.test;

import org.junit.jupiter.api.Test;
import se325.flights.dto.UserDTO;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests functionality related to users and logging in / out.
 */
public class UserIT extends BaseIntegrationTests {

    /**
     * Tests that a user is allowed to login when supplying the correct username and password. The response should be a
     * 204 No Content, with an authToken cookie.
     */
    @Test
    public void testLogin() {
        logInAsAlice();
    }

    /**
     * Tests that a user is not allowed to login when supplying an incorrect username. The response should be a
     * 401 Unauthorized, and no auth cookie should be returned.
     */
    @Test
    public void testLoginFail_BadUsername() {
        UserDTO badUser = new UserDTO("Not_Alice", "pa55word");
        try (Response response = clientRequest("/users/login").post(Entity.json(badUser))) {

            assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
            assertNull(response.getCookies().get("authToken"));
        }
    }

    /**
     * Tests that a user is not allowed to login when supplying an incorrect password. The response should be a
     * 401 Unauthorized, and no auth cookie should be returned.
     */
    @Test
    public void testLoginFail_BadPassword() {
        UserDTO badUser = new UserDTO("Alice", "NotThePassword");
        try (Response response = clientRequest("/users/login").post(Entity.json(badUser))) {

            assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
            assertNull(response.getCookies().get("authToken"));
        }
    }

    /**
     * Tests that we can log out after logging in. The service should return a 204 No Content response, with an
     * authToken cookie set to max-age of 0, which will cause any existing authToken cookies to be deleted from the
     * client.
     */
    @Test
    public void testLogout() {
        logInAsBob();

        try (Response response = clientRequest("/users/logout").get()) {
            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
            NewCookie cookie = response.getCookies().get("authToken");
            assertNotNull(cookie);
            assertEquals(0, cookie.getMaxAge());
        }
    }

    /**
     * Tests that we can create new Users in the system. Creating a new user should return a 201 Created response with
     * a valid Location header. Once created, we should be able to login as the new user.
     */
    @Test
    public void testCreateUser() {

        UserDTO newUser = new UserDTO("myname", "mypass");
        try (Response response = clientRequest("/users").post(Entity.json(newUser))) {

            assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
            assertNotNull(response.getLocation());
        }

        // Attempt to login - should succeed.
        logInAs("myname", "mypass");
    }

    /**
     * Tests that we cannot create a new user with a duplicate username. Trying this should return a 409 Conflict
     * response
     */
    @Test
    public void testCreateUserFail_DuplicateUsername() {

        UserDTO badUser = new UserDTO("Bob", "hi");
        try (Response response = clientRequest("/users").post(Entity.json(badUser))) {

            assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
        }
    }

}
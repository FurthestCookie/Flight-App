package se325.flights.util;

import org.apache.commons.codec.digest.DigestUtils;
import se325.flights.domain.User;

import javax.persistence.EntityManager;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.UUID;

/**
 * Contains methods for generating SHA3-256 hashes and random UUID strings
 */
public class SecurityUtils {

    private static final ThreadLocal<DigestUtils> THREAD_LOCAL_DIGEST =
            ThreadLocal.withInitial(() -> new DigestUtils("SHA3-256"));

    /**
     * Generates a SHA3-256 hash of the given password string, and returns it as a hex string.
     *
     * @param password the password to hash
     * @return the hex-string representation of the SHA3-256 hash of the provided value
     */
    public static String getSHA256Hash(String password) {
        return THREAD_LOCAL_DIGEST.get().digestAsHex(password);
    }

    /**
     * Returns a value indicating whether the given hash is the SHA3-256 hash of the given password.
     *
     * @param password the password to check
     * @param hash     the hash to check
     * @return true if there is a match, false otherwise
     */
    public static boolean matches(String password, String hash) {
        return getSHA256Hash(password).equals(hash);
    }

    /**
     * Generates a random UUID, as a String. Can be used as an auth token.
     *
     * @return a random UUID string.
     */
    public static String generateRandomUUIDString() {
        return UUID.randomUUID().toString();
    }

    /**
     * Creates a {@link NewCookie} instance suitable for an authentication token.
     */
    public static NewCookie generateAuthCookie() {
        return new NewCookie(
                "authToken",
                generateRandomUUIDString(),
                "/",
                null,
                null,
                604800 * 2, // About two weeks
                false
        );
    }

    /**
     * Creates a {@link NewCookie} instance suitable for deleting the authentication token.
     */
    public static NewCookie generateDeleteAuthCookie() {
        return new NewCookie("authToken",
                null,
                "/",
                null,
                null,
                0,
                false);
    }

    /**
     * Gets the {@link User} object with the given auth token.
     *
     * @param em         the entity manager to use to execute the database query
     * @param authCookie the auth cookie to check
     * @return the {@link User} with the matching token
     * @throws NotAuthorizedException if there's no such user
     */
    public static User getUserWithAuthToken(EntityManager em, Cookie authCookie) throws NotAuthorizedException {

        if (authCookie == null) {
            em.getTransaction().rollback();
            throw new NotAuthorizedException(Response.status(401, "Not authenticated").build());
        }

        try {
            return em.createQuery("SELECT u FROM User u WHERE u.authToken = :authToken", User.class)
                    .setParameter("authToken", authCookie.getValue())
                    .getSingleResult();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            throw new NotAuthorizedException(Response.status(401, "Not authenticated").build());
        }
    }

}
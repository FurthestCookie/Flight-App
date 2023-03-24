package se325.flights.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se325.flights.domain.User;
import se325.flights.domain.mappers.UserMapper;
import se325.flights.dto.UserDTO;
import se325.flights.util.SecurityUtils;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

/**
 * A JAX-RS resource class which handles requests to create user accounts, log in, and log out.
 */

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    private static final Logger logger = LoggerFactory.getLogger(UserResource.class);

    /**
     * Endpoint allowing the user to register a new account using the username and password
     * the user specifies in the login form page.
     *
     * Set the URL for the newly created user as part of the header fields in the HTTP response
     * sent back to the user if successful.
     * @param userDTO
     * @return
     */
    @POST
    public Response createUserAccount(UserDTO userDTO){

        EntityManager em = PersistenceManager.instance().createEntityManager();

        // check that there doesnt exist a user with this username already in db
        em.getTransaction().begin();
        TypedQuery<User> userQuery = em.createQuery("select u from User u where u.username = :username",
                User.class).setParameter("username", userDTO.getUsername());
        List<User> existingUser = userQuery.getResultList();
        em.getTransaction().commit();

        if (!existingUser.isEmpty()) {
            // return 409 conflict status code for duplicate username
            return Response.status(Response.Status.CONFLICT).build();
        }

        // parse the dto into the corresponding domain object
        User user = UserMapper.toDomain(userDTO);
        // set the password to the hash of the current password
        user.setPassHash(SecurityUtils.getSHA256Hash(userDTO.getPassword()));

        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();

            // sets location header and returns 201 status code
            return Response.created(URI.create("/users/" + user.getId())).build();
        } finally {
            em.close();
        }
    }

    /**
     * Endpoint which allows the user to login with already registered account credentials.
     * The username being used to login must match one of the registered usernames in the db.
     *
     * An authentication cookie is added to the header field of the HTTP response sent back to
     * the user so that other endpoints can use the cookie to authenticate the user.
     * @param userDto
     * @return
     */
    @POST
    @Path("/login")
    public Response Login(UserDTO userDto){
        EntityManager em = PersistenceManager.instance().createEntityManager();

        try {
            // validate the credentials
            em.getTransaction().begin();
            TypedQuery<User> userQuery = em.createQuery("select u from User u where u.username = :username and u.passHash = :password", User.class)
                    .setParameter("username", userDto.getUsername())
                    .setParameter("password", SecurityUtils.getSHA256Hash(userDto.getPassword()));
            List<User> user = userQuery.getResultList();

            if (user.isEmpty()){
                em.getTransaction().commit();
                // return a 401 status code if invalid
                return Response.status(Response.Status.UNAUTHORIZED).build();
            } else {
                User validatedUser = user.get(0);
                //create a cookie
                NewCookie authCookie = SecurityUtils.generateAuthCookie();

                // set the user field with authCookie token
                validatedUser.setAuthToken(authCookie.getValue());
                em.getTransaction().commit();

                // returns 204 status code with cookie in header
                return Response.noContent().cookie(authCookie).build();
            }
        } finally {
            em.close();
        }
    }

    /**
     * Removes the authentication cookie from the header.
     * @return
     */
    @GET
    @Path("/logout")
    public Response Logout(){

        // set 204 response code and remove the cookie by setting maxAge to 0
        return Response.noContent().cookie(SecurityUtils.generateDeleteAuthCookie()).build();
    }
}

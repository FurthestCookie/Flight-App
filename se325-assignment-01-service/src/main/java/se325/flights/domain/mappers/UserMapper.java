package se325.flights.domain.mappers;

import se325.flights.domain.User;
import se325.flights.dto.UserDTO;
import se325.flights.util.SecurityUtils;

/**
 * A mapper to convert between {@link User} and {@link UserDTO} instances
 */
public class UserMapper {

    public static User toDomain(UserDTO dtoUser) {
        return new User(dtoUser.getUsername(), SecurityUtils.getSHA256Hash(dtoUser.getPassword()));
    }
}

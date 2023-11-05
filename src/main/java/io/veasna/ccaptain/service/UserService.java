package io.veasna.ccaptain.service;

import io.veasna.ccaptain.domain.User;
import io.veasna.ccaptain.dto.UserDTO;

/**
 * @author Veasna
 * @version 1.0
 * @license Veasna , LLC
 * @since 3/11/23 18:34
 */
public interface UserService
{
    UserDTO createUser(User user);
    UserDTO getUserByEmail(String email);
}

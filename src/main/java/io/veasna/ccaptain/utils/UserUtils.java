package io.veasna.ccaptain.utils;

import io.veasna.ccaptain.domain.UserPrincipal;
import io.veasna.ccaptain.dto.UserDTO;
import org.springframework.security.core.Authentication;

/**
 * @author Veasna
 * @version 1.0
 * @license Veasna , LLC
 * @since 24/11/23 15:37
 */
public class UserUtils {

    public static UserDTO getAuthenticatedUser(Authentication authentication){
        return ((UserDTO) authentication.getPrincipal());
    }
    public static UserDTO getLoggedInUser (Authentication authentication){
        return ((UserPrincipal) authentication.getPrincipal()).getUser();
    }
}

package io.veasna.ccaptain.enumeration;

import lombok.Getter;

/**
 * @author Veasna
 * @version 1.0
 * @license Veasna , LLC
 * @since 12/1/24 13:55
 */

public enum EventType {
    LOGIN_ATTEMPT("You tried to login"),
    LOGIN_ATTEMPT_SUCCESS("You logged in successfully"),
    LOGIN_ATTEMPT_FAILURE("You failed to login"),
    PROFILE_UPDATE("You updated your profile"),
    PROFILE_PICTURE_UPDATE("You updated your profile picture"),
    ROLE_UPDATE("You updated your role"),
    ACCOUNT_SETTINGS_UPDATE("You updated your account setting"),
    MFA_UPDATE("You updated your MFA setting"),
    PASSWORD_UPDATE("You updated your password"),
    ;

    private final String description;

    EventType(String description) {
        this.description = description;
    }

}

package io.veasna.ccaptain.form;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Veasna
 * @version 1.0
 * @license Veasna , LLC
 * @since 27/3/24 15:00
 */
@Getter
@Setter
public class NewPasswordForm {
        @NotNull(message = "ID cannot be null or empty")
        private Long userId;
        @NotEmpty(message = "Password cannot be empty")
        private String password;
        @NotEmpty(message = "Confirm password cannot be empty")
        private String confirmPassword;
}


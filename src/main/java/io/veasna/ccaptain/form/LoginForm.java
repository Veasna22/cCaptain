package io.veasna.ccaptain.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Veasna
 * @version 1.0
 * @license Veasna , LLC
 * @since 4/11/23 22:17
 */
@Getter
@Setter
public class LoginForm
{
    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Email is not valid")
    private String email;
    @NotEmpty(message = "Password cannot be empty")
    private String password;
}

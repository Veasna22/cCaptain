package io.veasna.ccaptain.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author Veasna
 * @version 1.0
 * @license Veasna , LLC
 * @since 25/11/23 14:23
 */
@Getter
@Setter
public class UpdateForm {
    @NotNull(message = "Id cannot be null or empty")
    private Long id;
    @NotEmpty(message = "First name cannot be empty")
    private String firstName;

    @NotEmpty(message = "Last name cannot be empty")
    private String lastName;

    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Email is not valid")
    private String email;
    @Pattern(regexp = "^\\d{11}$", message = "Phone number is not valid")
    private String phone;

    private String address;

    private String title;

    private String bio;

}

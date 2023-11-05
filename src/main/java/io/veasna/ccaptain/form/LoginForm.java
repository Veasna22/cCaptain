package io.veasna.ccaptain.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * @author Veasna
 * @version 1.0
 * @license Veasna , LLC
 * @since 4/11/23 22:17
 */
@Data
public class LoginForm
{
    @NotEmpty
    private String email;
    @NotEmpty
    private String password;
}

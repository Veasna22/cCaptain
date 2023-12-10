package io.veasna.ccaptain.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Veasna
 * @version 1.0
 * @license Veasna , LLC
 * @since 9/12/23 16:35
 */
@Getter
@Setter
public class UpdatePasswordForm {
    @NotEmpty(message = "Current Password cannot be empty")
    private String currentPassword;

    @NotEmpty(message = "New Password cannot be empty")
    private String newPassword;

    @NotEmpty(message = "Comfirm password be empty")
    private String comfirmNewPassword;

}

package io.veasna.ccaptain.form;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Veasna
 * @version 1.0
 * @license Veasna , LLC
 * @since 12/12/23 16:39
 */
@Getter
@Setter
public class SettingsForm {
    @NotNull(message="Not Locked cannot be null or Empty")
    private Boolean isNotLocked;
    @NotNull(message="Enabled cannot be null or Empty")
    private Boolean enabled;

}

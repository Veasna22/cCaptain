package io.veasna.ccaptain.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Veasna
 * @version 1.0
 * @license Veasna , LLC
 * @since 3/11/23 18:39
 */

@Data
public class UserDTO {
    private Long id;
    private String fistName;
    private String lastName;
    private String email;
    private String address;
    private String phone;
    private String title;
    private String bio;
    private String imageUrl;
    private Boolean enabled;
    private Boolean isNotLocked;
    private Boolean isUsingMfa;
    private LocalDateTime createdAt;
}

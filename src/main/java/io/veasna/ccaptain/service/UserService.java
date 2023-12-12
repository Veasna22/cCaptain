package io.veasna.ccaptain.service;

import io.veasna.ccaptain.domain.User;
import io.veasna.ccaptain.dto.UserDTO;
import io.veasna.ccaptain.form.UpdateForm;

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
    void sendVerificationCode(UserDTO user);
    UserDTO verifyCode(String email, String code);

    void resetPassword(String email);

    UserDTO verifyPasswordKey(String key);

    UserDTO updateUserDetails(UpdateForm user);

    UserDTO getUserById(Long userId);

    void renewPassword(String key, String password, String comfirmpassword);

    UserDTO verifyAccountKey(String key);

    void updatePassword(Long userId, String currentPassword, String newPassword, String comfirmNewPassword);

    void updateUserRole(Long userId, String roleName);
}

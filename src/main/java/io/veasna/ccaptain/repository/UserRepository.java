package io.veasna.ccaptain.repository;

import io.veasna.ccaptain.domain.User;
import io.veasna.ccaptain.dto.UserDTO;
import io.veasna.ccaptain.form.UpdateForm;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

public interface UserRepository <T extends User>{
    /* Basic CRUD OPERATION */
    T create(T data);
    Collection<T> list (int page, int pageSize);
    T get(Long id);
    T update(T data);
    Boolean delete(Long id);
    User getUserByEmail(String email);
    void sendVerificationCode(UserDTO user);

    User verifyCode(String email, String code);

    void resetPassword(String email);

    T verifyPasswordKey(String key);

    T updateUserDetails(UpdateForm user);

    void renewPassword(String key, String password, String comfirmpassword);

    T verifyAccountKey(String key);

    void updatePassword(Long id, String currentPassword, String newPassword, String comfirmNewPassword);

    void updateAccountSettings(Long userId, Boolean enabled, Boolean notLocked);

    User toggleMfa(String email);

    void updateImage(UserDTO user, MultipartFile image);
}

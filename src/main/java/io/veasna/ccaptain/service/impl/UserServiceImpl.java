package io.veasna.ccaptain.service.impl;

import io.veasna.ccaptain.domain.Role;
import io.veasna.ccaptain.domain.User;
import io.veasna.ccaptain.dto.UserDTO;
import io.veasna.ccaptain.form.UpdateForm;
import io.veasna.ccaptain.repository.RoleRepository;
import io.veasna.ccaptain.repository.UserRepository;
import io.veasna.ccaptain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static io.veasna.ccaptain.dtomapper.UserDTOMapper.fromUser;

/**
 * @author Veasna
 * @version 1.0
 * @license Veasna , LLC
 * @since 3/11/23 18:47
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository<User> userRepository;
    private final RoleRepository<Role> roleRoleRepository;

    @Override
    public UserDTO createUser(User user) {
        return maptoUserDTO(userRepository.create(user));
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        return maptoUserDTO(userRepository.getUserByEmail(email));
    }

    @Override
    public void sendVerificationCode(UserDTO user) {
        userRepository.sendVerificationCode(user);
    }
//
//    @Override
//    public User getUser(String email) {
//        return userRepository.getUserByEmail(email);
//    }

    @Override
    public UserDTO verifyCode(String email, String code) {
        return maptoUserDTO(userRepository.verifyCode(email,code));
    }

    @Override
    public void resetPassword(String email) {
        userRepository.resetPassword(email);
    }

    @Override
    public UserDTO verifyPasswordKey(String key) {
        return maptoUserDTO(userRepository.verifyPasswordKey(key));
    }

    @Override
    public UserDTO updateUserDetails(UpdateForm user) {
        return maptoUserDTO(userRepository.updateUserDetails(user));
    }

    @Override
    public UserDTO getUserById(Long userId) {
        return maptoUserDTO(userRepository.get(userId));
    }

    @Override
    public void renewPassword(String key, String password, String comfirmpassword) {
        userRepository.renewPassword(key,password,comfirmpassword);
    }

    @Override
    public UserDTO verifyAccountKey(String key) {
        return maptoUserDTO(userRepository.verifyAccountKey(key));
    }

    @Override
    public void updatePassword(Long id, String currentPassword, String newPassword, String comfirmNewPassword) {
        userRepository.updatePassword(id,currentPassword,newPassword,comfirmNewPassword);
    }

    private UserDTO maptoUserDTO(User user) {
        return fromUser(user,roleRoleRepository.getRoleByUserId(user.getId()));
    }
}

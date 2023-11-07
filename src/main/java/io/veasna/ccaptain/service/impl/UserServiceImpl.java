package io.veasna.ccaptain.service.impl;

import io.veasna.ccaptain.domain.Role;
import io.veasna.ccaptain.domain.User;
import io.veasna.ccaptain.dto.UserDTO;
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

    private UserDTO maptoUserDTO(User user) {
        return fromUser(user,roleRoleRepository.getRoleByUserId(user.getId()));
    }
}

package io.veasna.ccaptain.repository;

import io.veasna.ccaptain.domain.User;
import io.veasna.ccaptain.dto.UserDTO;

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
}

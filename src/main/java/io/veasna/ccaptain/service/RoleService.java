package io.veasna.ccaptain.service;

import io.veasna.ccaptain.domain.Role;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

/**
 * @author Veasna
 * @version 1.0
 * @license Veasna , LLC
 * @since 6/11/23 22:27
 */
public interface RoleService {
    Role getRoleByUserId(Long id);
}

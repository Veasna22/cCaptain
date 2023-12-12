package io.veasna.ccaptain.service.impl;

import io.veasna.ccaptain.domain.Role;
import io.veasna.ccaptain.repository.RoleRepository;
import io.veasna.ccaptain.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * @author Veasna
 * @version 1.0
 * @license Veasna , LLC
 * @since 6/11/23 22:34
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository<Role> roleRoleRepository;
    @Override
    public Role getRoleByUserId(Long id) {
        return roleRoleRepository.getRoleByUserId(id);
    }

    @Override
    public Collection<Role> getRoles() {
        return roleRoleRepository.list();
    }
}

package io.veasna.ccaptain.repository.impl;

import io.veasna.ccaptain.domain.Role;
import io.veasna.ccaptain.exception.ApiException;
import io.veasna.ccaptain.repository.RoleRepository;
import io.veasna.ccaptain.rowmapper.RoleRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;

import static io.veasna.ccaptain.enumeration.RoleType.ROLE_USER;
import static io.veasna.ccaptain.query.RoleQuery.INSERT_ROLE_TO_USER;
import static io.veasna.ccaptain.query.RoleQuery.SELECT_ROLE_BY_NAME_QUERY;
import static java.util.Map.*;
import static java.util.Objects.requireNonNull;

/**
 * @author Veasna
 * @version 1.0
 * @license Veasna , LLC
 * @since 3/11/23 17:53
 */

@Repository
@RequiredArgsConstructor
@Slf4j
public class RoleRepositoryImpl implements RoleRepository<Role> {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Role create(Role data) {
        return null;
    }

    @Override
    public Collection<Role> list(int page, int pageSize) {
        return null;
    }

    @Override
    public Role get(Long id) {
        return null;
    }

    @Override
    public Role update(Role data) {
        return null;
    }

    @Override
    public Boolean delete(Long id) {
        return null;
    }

    @Override
    public void addRoleToUser(Long userId, String roleName) {
        log.info("Adding role {} to user id : {}", roleName, userId);
        try{
            Role role = jdbc.queryForObject(SELECT_ROLE_BY_NAME_QUERY, of("name", roleName), new RoleRowMapper());
            jdbc.update(INSERT_ROLE_TO_USER, of("userId", userId, "roleId", requireNonNull(role).getId()));
        } catch(EmptyResultDataAccessException exception){
            throw new ApiException("No Role founded By name : " + ROLE_USER.name());
        } catch (Exception exception) {
            throw new ApiException("An error Occurred . Please Try Again .");
        }
    }

    @Override
    public Role getRoleByUserId(Long userId) {
        return null;
    }

    @Override
    public Role getRoleByUserEmail(String email) {
        return null;
    }

    @Override
    public void updateUserRole(Long userId, String roleName) {
    }
}
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
import static io.veasna.ccaptain.query.RoleQuery.*;
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
    public Collection<Role> list() {
        log.info("Fetching All Roles");
        try{
            return jdbc.query(SELECT_ROLES_QUERY,new RoleRowMapper());
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error Occurred . Please Try Again .");
        }
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

        log.info("Fetching role to user id : {}", userId);
        try{
            return jdbc.queryForObject(SELECT_ROLE_BY_ID_QUERY, of("id", userId), new RoleRowMapper());
        } catch(EmptyResultDataAccessException exception){
            throw new ApiException("No Role founded By name : " + ROLE_USER.name());
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error Occurred . Please Try Again .");
        }
    }

    @Override
    public Role getRoleByUserEmail(String email) {
        return null;
    }

    @Override
    public void updateUserRole(Long userId, String roleName) {
        log.info("Updating role for user id : {}", userId);
        try{
            Role role = jdbc.queryForObject(SELECT_ROLE_BY_NAME_QUERY, of("name", roleName), new RoleRowMapper());
            jdbc.update(UPDATE_USER_ROLE_QUERY, of("roleId", role.getId(),"userId", userId));
        } catch(EmptyResultDataAccessException exception){
            throw new ApiException("No Role founded By name : " + ROLE_USER.name());
        } catch (Exception exception) {
            throw new ApiException("An error Occurred . Please Try Again .");
        }
    }
}

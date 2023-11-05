package io.veasna.ccaptain.repository.impl;

import io.veasna.ccaptain.domain.Role;
import io.veasna.ccaptain.domain.User;
import io.veasna.ccaptain.domain.UserPrincipal;
import io.veasna.ccaptain.exception.ApiException;
import io.veasna.ccaptain.repository.RoleRepository;
import io.veasna.ccaptain.repository.UserRepository;
import io.veasna.ccaptain.rowmapper.UserRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import static io.veasna.ccaptain.enumeration.RoleType.ROLE_USER;
import static io.veasna.ccaptain.enumeration.VerificationType.ACCOUNT;
import static io.veasna.ccaptain.query.UserQuery.*;
import static java.util.Map.*;
import static java.util.Objects.requireNonNull;

/**
 * @author Veasna
 * @version 1.0
 * @license Veasna , LLC
 * @since 3/11/23 15:55
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository<User> , UserDetailsService {

    private final NamedParameterJdbcTemplate jdbc;
    private final RoleRepository<Role> roleRepository;
    private final BCryptPasswordEncoder encoder;

    @Override
    public User create(User user) {
        // Check the email is unique or nah
        if (getEmailCount(user.getEmail().trim().toLowerCase()) > 0) {
            throw new ApiException("Email already exist");
        }
        // Save new User
        try{
            KeyHolder holder = new GeneratedKeyHolder();
            SqlParameterSource parameters = getSqlParameterSource(user);
            jdbc.update(INSERT_USER_QUERY, parameters, holder);
            user.setId(requireNonNull(holder.getKey()).longValue());
            // Add role to User
            roleRepository.addRoleToUser(user.getId(), ROLE_USER.name());
            // Send verify URL
            String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(), ACCOUNT.getType());
            // save url to verification table

            jdbc.update(INSERT_ACCOUNT_VERIFICATION_URL_QUERY, of("userId", user.getId(), "url", verificationUrl));
            // send email to user with verify url
//            emailService.sendVerificationUrl(user.getFistName(),user.getEmail(), verificationUrl, ACCOUNT.getType());
            user.setEnabled(false);
            user.setIsNotLocked(true);
            // return newly Created User
            return user;
            // if any errors , throw exception with Proper Message
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error Occurred . Please Try Again .");

        }
    }



    @Override
    public Collection<User> list(int page, int pageSize) {
        return null;
    }

    @Override
    public User get(Long id) {
        return null;
    }

    @Override
    public User update(User data) {
        return null;
    }

    @Override
    public Boolean delete(Long id) {
        return null;
    }


    private SqlParameterSource getSqlParameterSource(User user) {
        return new MapSqlParameterSource()
                .addValue("firstName", user.getFirstName())
                .addValue("lastName", user.getLastName())
                .addValue("email", user.getEmail())
                .addValue("password", encoder.encode(user.getPassword()));
    }

    private Integer getEmailCount(String email) {
        return jdbc.queryForObject(COUNT_USER_EMAIL_QUERY, of("email", email), Integer.class);
    }

    private String getVerificationUrl(String key, String type) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/verify/" + type + "/" + key)
                .toUriString();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = getUserByEmail(email);
        if(user == null ){
            log.error("User not found in the database {}", email);
            throw new UsernameNotFoundException("User not found in the database");
        }else{
            log.info("User found in the database {}", email);
            return new UserPrincipal(user, roleRepository.getRoleByUserId(user.getId()).getPermission());
        }
    }
    @Override
    public User getUserByEmail(String email) {
        try{
            return jdbc.queryForObject(SELECT_USER_BY_EMAIL_QUERY, of("email", email), new UserRowMapper());
            // if any errors , throw exception with Proper Message
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("No USER FOUNDED By Email : " + email);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error Occurred . Please Try Again .");
        }
    }
}

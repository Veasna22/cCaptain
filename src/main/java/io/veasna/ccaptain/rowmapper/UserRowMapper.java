package io.veasna.ccaptain.rowmapper;

import io.veasna.ccaptain.domain.Role;
import io.veasna.ccaptain.domain.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Veasna
 * @version 1.0
 * @license Veasna , LLC
 * @since 4/11/23 22:01
 */
public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("id"))
                .firstName(resultSet.getString("first_name"))
                .lastName(resultSet.getString("last_name"))
                .email(resultSet.getString("email"))
                .password(resultSet.getString("password"))
                .address(resultSet.getString("address"))
                .phone(resultSet.getString("phone"))
                .title(resultSet.getString("title"))
                .bio(resultSet.getString("bio"))
                .imageUrl(resultSet.getString("image_url"))
                .enabled(resultSet.getBoolean("enabled"))
                .isNotLocked(resultSet.getBoolean("non_locked"))
                .isUsingMfa(resultSet.getBoolean("using_mfa"))
                .createdAt(resultSet.getTimestamp("created_date").toLocalDateTime())
                .build();


    }
}


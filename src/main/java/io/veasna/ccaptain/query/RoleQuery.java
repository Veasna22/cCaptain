package io.veasna.ccaptain.query;

/**
 * @author Veasna
 * @version 1.0
 * @license Veasna , LLC
 * @since 3/11/23 18:28
 */
public class RoleQuery
{
    public static final String SELECT_ROLE_BY_NAME_QUERY = "SELECT * FROM Roles WHERE name = :name";
    public static final String INSERT_ROLE_TO_USER = "INSERT INTO UserRoles (user_id, role_id) " +
            "VALUE (:userId, :roleId)";
}

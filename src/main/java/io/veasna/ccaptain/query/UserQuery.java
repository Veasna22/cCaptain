package io.veasna.ccaptain.query;

/**
 * @author Veasna
 * @version 1.0
 * @license Veasna , LLC
 * @since 3/11/23 16:12
 */
public class UserQuery
{
    public static final String COUNT_USER_EMAIL_QUERY = "SELECT COUNT(*) FROM Users WHERE email = :email";
    public static final String INSERT_USER_QUERY = "INSERT INTO Users (first_name, last_name, email, password) VALUE (:firstName, :lastName, :email, :password)";
    public static final String INSERT_ACCOUNT_VERIFICATION_URL_QUERY = "INSERT INTO AccountVerifications (user_id, url) " +
            "VALUE (:userId, :url)";
    public static final String SELECT_USER_BY_EMAIL_QUERY = "SELECT * FROM Users WHERE email = :email";
    public static final String DELETE_VERIFICATION_CODE_BY_USER_ID = "DELETE FROM TwoFactorVerifications WHERE user_id = :id";
    public static final String INSERT_VERIFICATION_CODE_QUERY = "INSERT INTO TwoFactorVerifications (user_Id, code, expiration_date) VALUES (:userId, :code, :expirationDate)";
    public static final String SELECT_USER_BY_CODE_QUERY = "SELECT * FROM Users WHERE id = (SELECT user_id FROM TwoFactorVerifications WHERE code = :code)";
    public static final String DELETE_CODE = "DELETE FROM TwoFactorVerifications WHERE code = :code";
    public static final String SELECT_CODE_EXPIRATION_QUERY = "SELECT expiration_date < NOW() AS is_expired FROM TwoFactorVerifications WHERE code = :code";
    public static final String DELETE_PASSWORD_VERIFICATION_BY_USER_ID_QUERY = "DELETE FROM ResetPasswordVerifications WHERE user_id = :userID";
    public static final String INSERT_PASSWORD_VERIFICATION_QUERY = "INSERT INTO ResetPasswordVerifications (user_id, url ,expiration_date) VALUES (:userID, :url,:expirationDate)";
    public static final String SELECT_EXPIRATION_BY_URL = "SELECT expiration_date < NOW() AS is_expired FROM ResetPasswordVerifications WHERE url = :url";
    public static final String SELECT_USER_BY_PASSWORD_URL_QUERY = "SELECT * FROM Users WHERE id = (SELECT user_id FROM ResetPasswordVerifications WHERE url = :url)";
    public static final String UPDATE_USER_DETAILS_QUERY = "UPDATE Users SET first_name = :firstName, last_name = :lastName, email = :email, phone = :phone, address = :address, title = :title, bio = :bio WHERE id = :id";
    public static final String SELECT_USER_BY_ID_QUERY = "SELECT * FROM Users WHERE id = :id";
    public static final String UPDATE_USER_PASSWORD_BY_URL_QUERY = "UPDATE Users SET password = :password WHERE id = (SELECT user_id FROM ResetPasswordVerifications WHERE url = :url)";
    public static final String DELETE_VERIFICATION_BY_URL_QUERY = "DELETE FROM ResetPasswordVerifications WHERE url = :url";
    public static final String SELECT_USER_BY_ACCOUNT_URL_QUERY = "SELECT * FROM Users WHERE id = (SELECT user_id FROM AccountVerifications WHERE url = :url)";
    public static final String UPDATE_USER_ENABLED_QUERY = "UPDATE Users SET enabled = 1 WHERE id = :id";
    public static final String UPDATE_USER_PASSWORD_BY_ID_QUERY = "UPDATE Users SET password = :password WHERE id = :id";
    public static final String UPDATE_USER_ACCOUNT_SETTINGS_QUERY = "UPDATE Users SET enabled = :enabled, non_locked = :notLocked WHERE id = :userId";




}

package io.veasna.ccaptain.repository.impl;

import io.veasna.ccaptain.domain.Role;
import io.veasna.ccaptain.domain.User;
import io.veasna.ccaptain.domain.UserPrincipal;
import io.veasna.ccaptain.dto.UserDTO;
import io.veasna.ccaptain.enumeration.VerificationType;
import io.veasna.ccaptain.exception.ApiException;
import io.veasna.ccaptain.form.UpdateForm;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import static io.micrometer.common.util.StringUtils.isBlank;
import static io.veasna.ccaptain.enumeration.RoleType.ROLE_USER;
import static io.veasna.ccaptain.enumeration.VerificationType.ACCOUNT;
import static io.veasna.ccaptain.enumeration.VerificationType.PASSWORD;
import static io.veasna.ccaptain.query.UserQuery.*;
import static io.veasna.ccaptain.utils.SmsUtils.sendSMS;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Map.of;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.time.DateFormatUtils.format;
import static org.apache.commons.lang3.time.DateUtils.addDays;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;

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

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
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
        try {
            return jdbc.queryForObject(SELECT_USER_BY_ID_QUERY, of("id", id), new UserRowMapper());
        }catch(EmptyResultDataAccessException exception){
            throw new ApiException("No User Found By ID : " + id);
        }catch(Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An error Occurred . Please Try Again .");
        }
    }

    @Override
    public User update(User data) {
        return null;
    }

    @Override
    public Boolean delete(Long id) {
        return null;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = getUserByEmail(email);
        if(user == null ){
            log.error("User not found in the database {}", email);
            throw new UsernameNotFoundException("User not found in the database");
        }else{
            log.info("User found in the database {}", email);
            return new UserPrincipal(user, roleRepository.getRoleByUserId(user.getId()));
        }
    }
    @Override
    public User getUserByEmail(String email) {
        try{
            return jdbc.queryForObject(SELECT_USER_BY_EMAIL_QUERY, of("email", email), new UserRowMapper());
            // if any errors , throw exception with Proper Message
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("No User Found By Email : " + email);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error Occurred . Please Try Again .");
        }
    }

    @Override
    public void sendVerificationCode(UserDTO user) {
        String expirationDate = format(addDays(new Date(), 1),DATE_FORMAT);
        String verificationCode = randomAlphabetic(8).toUpperCase();
        try{
            jdbc.update(DELETE_VERIFICATION_CODE_BY_USER_ID, of("id", user.getId()));
            jdbc.update(INSERT_VERIFICATION_CODE_QUERY, of("userId", user.getId(), "code", verificationCode, "expirationDate", expirationDate));
//            sendSMS(user.getPhone(),"From : cCaptain \nVerification Code\n" + verificationCode);
            // if any errors , throw exception with Proper Message
            log.info("Verification Code : {} ",verificationCode);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error Occurred . Please Try Again .");
        }
    }

    @Override
    public User verifyCode(String email, String code) {
        if (isVerificationCodeExpired(code)) {
            throw new ApiException("This code has expired . Please Try Again .");
        }
        try{
            User userByCode = jdbc.queryForObject(SELECT_USER_BY_CODE_QUERY, of("code", code), new UserRowMapper());
            User userByEmail = jdbc.queryForObject(SELECT_USER_BY_EMAIL_QUERY, of("email", email), new UserRowMapper());
            if(userByCode.getEmail().equals(userByEmail.getEmail())) {
                jdbc.update(DELETE_CODE, of("code", code));
                return userByCode;
            }else{
                throw new ApiException("Code is not valid, Try Again !");
            }
        }catch(EmptyResultDataAccessException exception){
            throw new ApiException("Could not Find Record");
        }catch(Exception exception){
            throw new ApiException("An error Occurred . Please Try Again .");
        }
    }

    @Override
    public void resetPassword(String email) {
        if(getEmailCount(email.trim().toLowerCase()) <= 0 ) throw new ApiException("Email not found");
        try{
                String expirationDate = format(addDays(new Date(), 1),DATE_FORMAT);
                User user = getUserByEmail(email);
                String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(), PASSWORD.getType());
                jdbc.update(DELETE_PASSWORD_VERIFICATION_BY_USER_ID_QUERY, of("userID", user.getId()));
                jdbc.update(INSERT_PASSWORD_VERIFICATION_QUERY, of("userID", user.getId(), "url", verificationUrl, "expirationDate", expirationDate));
            // TODO send EMAIL with URL To User
                log.info("Verification URL : {} ",verificationUrl);
        }catch(Exception exception){
            throw new ApiException("An error Occurred . Please Try Again .");
        }
    }

    @Override
    public User verifyPasswordKey(String key) {
        if (isLinkedExpired(key,PASSWORD)) throw new ApiException("This link has expired . Please Try Again .");
        try{
            User user = jdbc.queryForObject(SELECT_USER_BY_PASSWORD_URL_QUERY, of("url", getVerificationUrl(key, PASSWORD.getType())), new UserRowMapper());
//            jdbc.update(DELETE_USER_FROM_PASSWORD_VERIFICATION_QUERY, of("id", user.getId()));
            return user;
        }catch(EmptyResultDataAccessException exception){
            log.error(exception.getMessage());
            throw new ApiException("This Code is not Valid Please login Again .");
        }catch(Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An error Occurred . Please Try Again .");
        }
    }

    @Override
    public User updateUserDetails(UpdateForm user) {
        try{
            jdbc.update(UPDATE_USER_DETAILS_QUERY, getUserDetailsSqlParameterSource(user));
            return get(user.getId());
        }catch(Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An error Occurred . Please Try Again .");
        }
    }

    @Override
    public void renewPassword(String key, String password, String confirmPassword) {
        if(!password.equals(confirmPassword)) throw new ApiException("Passwords don't match. Please try again.");
        try {
            jdbc.update(UPDATE_USER_PASSWORD_BY_URL_QUERY, of("password", encoder.encode(password), "url", getVerificationUrl(key, PASSWORD.getType())));
            jdbc.update(DELETE_VERIFICATION_BY_URL_QUERY, of("url", getVerificationUrl(key, PASSWORD.getType())));
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }
    @Override
    public void renewPassword(Long userId, String password, String confirmPassword) {
        if(!password.equals(confirmPassword)) throw new ApiException("Passwords don't match. Please try again.");
        try {
            jdbc.update(UPDATE_USER_PASSWORD_BY_USER_ID_QUERY, of("id", userId, "password", encoder.encode(password)));
            //jdbc.update(DELETE_PASSWORD_VERIFICATION_BY_USER_ID_QUERY, of("userId", userId));
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public User verifyAccountKey(String key) {

        try{
            User user = jdbc.queryForObject(SELECT_USER_BY_ACCOUNT_URL_QUERY, of("url", getVerificationUrl(key, ACCOUNT.getType())), new UserRowMapper());
            jdbc.update(UPDATE_USER_ENABLED_QUERY, of("enabled", true, "id", user.getId()));
            return user;
        }catch(EmptyResultDataAccessException exception){
            log.error(exception.getMessage());
            throw new ApiException("This Link is not Valid Please login Again .");
        }catch(Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An error Occurred . Please Try Again .");
        }
    }

    @Override
    public void updatePassword(Long id, String currentPassword, String newPassword, String comfirmNewPassword) {
        if(!newPassword.equals(comfirmNewPassword))
            { throw new ApiException("Password and Confirm Password does not match"); }
        User user = get(id);
        if(encoder.matches(currentPassword,user.getPassword()))
        {
            try{
                jdbc.update(UPDATE_USER_PASSWORD_BY_ID_QUERY, of("password", encoder.encode(newPassword), "id", id));
            }catch(Exception exception){
                log.error(exception.getMessage());
                throw new ApiException("An error Occurred . Please Try Again .");
            }
        } else{
            throw new ApiException("Current Password is not Correct");
        }
    }

    @Override
    public void updateAccountSettings(Long userId, Boolean enabled, Boolean notLocked) {
        try{
            jdbc.update(UPDATE_USER_ACCOUNT_SETTINGS_QUERY, of("userId", userId,"enabled", enabled, "notLocked", notLocked));
        }catch(Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An error Occurred . Please Try Again .");
        }
    }

    @Override
    public User toggleMfa(String email) {
        User user = getUserByEmail(email);
        if(isBlank(user.getPhone())){ throw new ApiException("Please Update Your Phone Number First"); }
        user.setIsUsingMfa(!user.getIsUsingMfa());
        try{
            jdbc.update(TOGGLE_USER_MFA_QUERY, of("email", email, "isUsingMfa", user.getIsUsingMfa()));
            return user;
        }catch(Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("Unable to Update MFA . Please Try Again .");
        }

    }

    @Override
    public void updateImage(UserDTO user, MultipartFile image) {
        String userImageUrl = setUserImageUrl(user.getEmail());
        saveImage(user.getEmail(), image);
        jdbc.update(UPDATE_USER_IMAGE_QUERY, of("imageUrl", userImageUrl, "id", user.getId()));

    }

    private void saveImage(String email, MultipartFile image){
        Path fileStorageLocation = Paths.get(System.getProperty("user.home") + "/Downloads/images/").toAbsolutePath().normalize();
        if(!Files.exists(fileStorageLocation)){
            try{
                Files.createDirectories(fileStorageLocation);
            }catch(Exception exception){
                log.error(exception.getMessage());
                throw new ApiException("Could not create the directory where the uploaded files will be stored.");
            }
            log.info("Directory Created: {} ",fileStorageLocation);
        }
        try{
            Files.copy(image.getInputStream(), fileStorageLocation.resolve(email + ".png"),REPLACE_EXISTING);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        log.info("File Saved in : {} folder ", fileStorageLocation);
    }

    private String setUserImageUrl(String email) {
        return fromCurrentContextPath()
                .path("/user/image/" + email + ".png")
                .toUriString();
    }


    private SqlParameterSource getSqlParameterSource(User user) {
        return new MapSqlParameterSource()
                .addValue("firstName", user.getFirstName())
                .addValue("lastName", user.getLastName())
                .addValue("email", user.getEmail())
                .addValue("password", encoder.encode(user.getPassword()));
    }
    private SqlParameterSource getUserDetailsSqlParameterSource(UpdateForm user) {
        return new MapSqlParameterSource()
                .addValue("id", user.getId())
                .addValue("firstName", user.getFirstName())
                .addValue("lastName", user.getLastName())
                .addValue("email", user.getEmail())
                .addValue("phone", user.getPhone())
                .addValue("address", user.getAddress())
                .addValue("title", user.getTitle())
                .addValue("bio", user.getBio());
    }


    private Integer getEmailCount(String email) {
        return jdbc.queryForObject(COUNT_USER_EMAIL_QUERY, of("email", email), Integer.class);
    }

    private String getVerificationUrl(String key, String type) {
        return fromCurrentContextPath().path("/user/verify/" + type + "/" + key).toUriString();
    }

    private Boolean isLinkedExpired(String key, VerificationType password) {
        try{
            return jdbc.queryForObject(SELECT_EXPIRATION_BY_URL, of("url", getVerificationUrl(key, password.getType())), Boolean.class);
        }catch(EmptyResultDataAccessException exception){
            log.error(exception.getMessage());
            throw new ApiException("This Code is not Valid Please login Again .");
        }catch(Exception exception){
            throw new ApiException("An error Occurred . Please Try Again .");
        }
    }

    private Boolean isVerificationCodeExpired(String code) {
        try{
          return jdbc.queryForObject(SELECT_CODE_EXPIRATION_QUERY, of("code", code), Boolean.class);
        }catch(EmptyResultDataAccessException exception){
            throw new ApiException("This Code is not Valid Please login Again .");
        }catch(Exception exception){
            throw new ApiException("An error Occurred . Please Try Again .");
        }
    }

}

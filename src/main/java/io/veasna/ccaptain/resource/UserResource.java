package io.veasna.ccaptain.resource;

import io.veasna.ccaptain.domain.HttpResponse;
import io.veasna.ccaptain.domain.User;
import io.veasna.ccaptain.domain.UserPrincipal;
import io.veasna.ccaptain.dto.UserDTO;
import io.veasna.ccaptain.exception.ApiException;
import io.veasna.ccaptain.form.LoginForm;
import io.veasna.ccaptain.form.SettingsForm;
import io.veasna.ccaptain.form.UpdateForm;
import io.veasna.ccaptain.form.UpdatePasswordForm;
import io.veasna.ccaptain.provider.TokenProvider;
import io.veasna.ccaptain.service.RoleService;
import io.veasna.ccaptain.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static io.veasna.ccaptain.dtomapper.UserDTOMapper.toUser;
import static io.veasna.ccaptain.utils.UserUtils.getAuthenticatedUser;
import static io.veasna.ccaptain.utils.UserUtils.getLoggedInUser;
import static java.time.Instant.now;
import static java.util.Map.of;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.unauthenticated;
import static org.springframework.util.MimeTypeUtils.IMAGE_PNG_VALUE;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;

/**
 * @author Veasna
 * @version 1.0
 * @license Veasna , LLC
 * @since 3/11/23 18:49
 */
@RestController
@RequestMapping(path = "/user")
@RequiredArgsConstructor
public class UserResource {
    public static final String TOKEN_PREFIX = "Bearer ";
    private final UserService userService;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    @PostMapping("/login")
    public ResponseEntity<HttpResponse> login(@RequestBody @Valid LoginForm loginForm){
        Authentication authentication = authenticate(loginForm.getEmail(),loginForm.getPassword());
        UserDTO user = getLoggedInUser(authentication);
        System.out.println(authentication);
        System.out.println(((UserPrincipal) authentication.getPrincipal()).getUser());
        return user.getIsUsingMfa() ? sendVerificationCode(user) : sendResponse(user);

    }


    @PostMapping("/register")
    public ResponseEntity<HttpResponse> saveUser(@RequestBody @Valid User user){
        UserDTO userDto = userService.createUser(user);
        return ResponseEntity.created(getUri()).body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(of("user",userDto))
                        .message("User created successfully")
                        .status(CREATED)
                        .statusCode(CREATED.value())
                        .build());
    }
    @GetMapping("/profile")
    public ResponseEntity<HttpResponse> profile (Authentication authentication){
        UserDTO user = userService.getUserByEmail(getAuthenticatedUser(authentication).getEmail());
        System.out.println(authentication);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(of("user",user,"roles", roleService.getRoles()))
                        .message("Profile Retrieved ")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }
    @PatchMapping("/update")
    public ResponseEntity<HttpResponse> updateUser (@RequestBody @Valid UpdateForm user) throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
        UserDTO updatedUser = userService.updateUserDetails(user);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(of("user",updatedUser))
                        .message("User Updated")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }
    @GetMapping("/verify/code/{email}/{code}")
    public ResponseEntity<HttpResponse> verifyCode (@PathVariable("email") String email, @PathVariable ("code") String code){
        UserDTO user = userService.verifyCode(email,code);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(of("user",user,"access_token",
                                tokenProvider.createAccessToken(getUserPrincipal(user)),"refresh_token",
                                tokenProvider.createRefreshToken(getUserPrincipal(user))))
                        .message("Login successfully")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @GetMapping("/resetpassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword (@PathVariable("email") String email){
        userService.resetPassword(email);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .message("Email Sent. Please check your email to reset your Password")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }
    @GetMapping("/verify/password/{key}")
    public ResponseEntity<HttpResponse> verifyPasswordUrl (@PathVariable("key") String key){
        UserDTO user = userService.verifyPasswordKey(key);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(of("user",user))
                        .message("Please Enter a new Password")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }
    @PostMapping("/resetpassword/{key}/{password}/{confirmPassword}")
    public ResponseEntity<HttpResponse> resetPasswordWithKey (@PathVariable("key") String key,
                                                           @PathVariable("password") String password,
                                                           @PathVariable("confirmPassword") String confirmPassword){
        userService.renewPassword(key,password,confirmPassword);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .message("Password reset Sucessfully")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @GetMapping("/verify/account/{key}")
    public ResponseEntity<HttpResponse> verifyAccount (@PathVariable("key") String key){
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .message(userService.verifyAccountKey(key).getEnabled()? "Account Already Verified" : "Account Verified")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }
    @GetMapping("/refresh/token")
    public ResponseEntity<HttpResponse> refreshToken (HttpServletRequest request){
        if(isHeaderAndTokenValid(request)){
            String token = request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length());
            UserDTO user = userService.getUserById(tokenProvider.getSubject(token, request));
            return ResponseEntity.ok().body(
                    HttpResponse.builder()
                            .timeStamp(now().toString())
                            .data(of("user",user,"access_token",
                                    tokenProvider.createAccessToken(getUserPrincipal(user)),
                                    "refresh_token", token))
                            .message("Token Refreshed Successfully")
                            .status(OK)
                            .statusCode(OK.value())
                            .build());
        }else{
            return ResponseEntity.badRequest().body(
                    HttpResponse.builder()
                            .timeStamp(now().toString())
                            .message("Refresh Token Missing or Invalid")
                            .developerMessage("Refresh Token Missing or Invalid")
                            .status(BAD_REQUEST)
                            .statusCode(BAD_REQUEST.value())
                            .build());
        }
    }
    @PatchMapping("/update/password")
    public ResponseEntity<HttpResponse> updatePassword (Authentication authentication, @RequestBody @Valid UpdatePasswordForm form){
        UserDTO userDTO = getAuthenticatedUser(authentication);
        userService.updatePassword(userDTO.getId(),form.getCurrentPassword(),form.getNewPassword(),form.getComfirmNewPassword());
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .message("Password Updated Successfully")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }
    @PatchMapping("/update/role/{roleName}")
    public ResponseEntity<HttpResponse> updateUserRole (Authentication authentication,@PathVariable ("roleName") String roleName){
        UserDTO userDTO = getAuthenticatedUser(authentication);
        userService.updateUserRole(userDTO.getId(),roleName);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(of("user",userService.getUserById(userDTO.getId()),"role",roleService.getRoles()))
                        .message("Role Updated Successfully")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }
    @PatchMapping("/update/settings")
    public ResponseEntity<HttpResponse> updateAccountSetting (Authentication authentication,@RequestBody @Valid SettingsForm form){
        UserDTO userDTO = getAuthenticatedUser(authentication);
        userService.updateAccountSettings(userDTO.getId(),form.getEnabled(),form.getIsNotLocked());
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(of("user",userService.getUserById(userDTO.getId()),"roles",roleService.getRoles()))
                        .message("Account Setting Updated Successfully")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }
    @PatchMapping("/togglemfa")
    public ResponseEntity<HttpResponse> toggleMfa (Authentication authentication) throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
        UserDTO user = userService.toggleMfa(getAuthenticatedUser(authentication).getEmail());
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .data(of("user",user,"roles",roleService.getRoles()))
                        .timeStamp(now().toString())
                        .message("Multi-Factor Authentication Updated Successfully")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }
    @PatchMapping("/update/image")
    public ResponseEntity<HttpResponse> updateProfileImage (Authentication authentication, @RequestParam ("image")MultipartFile image) throws InterruptedException {
        UserDTO user = getAuthenticatedUser(authentication);
        userService.updateImage(user, image);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .data(of("user",userService.getUserById(user.getId()),"roles",roleService.getRoles()))
                        .timeStamp(now().toString())
                        .message("Profile Image Updated Successfully")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }
    @GetMapping(value = "/image/{fileName}", produces = IMAGE_PNG_VALUE)
    public byte[] getProfileImage (@PathVariable ("fileName") String fileName) throws Exception {
        return Files.readAllBytes(Paths.get(System.getProperty("user.home")+"/Downloads/images/"+fileName));
    }
    private boolean isHeaderAndTokenValid(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION) != null && request.getHeader(AUTHORIZATION).startsWith(TOKEN_PREFIX)
                && tokenProvider.isTokenValid(tokenProvider.getSubject(request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length()), request)
                , request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length()));
    }

    @RequestMapping ("/error")
    public ResponseEntity<HttpResponse> handleError (HttpServletRequest request){
        return ResponseEntity.badRequest().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .reason("There is no mapping for a " + request.getMethod() + " request for this path on server")
                        .status(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.value())
                        .build());
    }
//    @RequestMapping ("/error")
//    public ResponseEntity<HttpResponse> handleError1 (HttpServletRequest request){
//        return new ResponseEntity<>(
//                HttpResponse.builder()
//                        .timeStamp(now().toString())
//                        .reason("There is no mapping for a " + request.getMethod() + " request for this path on server")
//                        .status(NOT_FOUND)
//                        .statusCode(NOT_FOUND.value())
//                        .build(),NOT_FOUND);
//    }
    private URI getUri() {
        return URI.create(fromCurrentContextPath().path("/user/get/<userId>").toUriString());
    }
    private Authentication authenticate (String email, String password){
        try{
            Authentication authentication = authenticationManager.authenticate(unauthenticated(email,password));
            return authentication;

        }catch(Exception exception){
//            processError(request,response,exception);
            throw new ApiException(exception.getMessage());
        }
    }
    private ResponseEntity<HttpResponse> sendVerificationCode(UserDTO user) {
        userService.sendVerificationCode(user);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(of("user",user))
                        .message("Verification Code Sent")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    private ResponseEntity<HttpResponse> sendResponse(UserDTO user) {
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(of("user",user,"access_token",
                                tokenProvider.createAccessToken(getUserPrincipal(user)),"refresh_token",
                                tokenProvider.createRefreshToken(getUserPrincipal(user))))
                        .message("Login successfully")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    private UserPrincipal getUserPrincipal(UserDTO user) {
        return new UserPrincipal(toUser(userService.getUserByEmail(user.getEmail())),roleService.getRoleByUserId(user.getId()));
    }
}

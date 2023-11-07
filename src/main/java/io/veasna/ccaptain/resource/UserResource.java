package io.veasna.ccaptain.resource;

import io.veasna.ccaptain.domain.HttpResponse;
import io.veasna.ccaptain.domain.User;
import io.veasna.ccaptain.domain.UserPrincipal;
import io.veasna.ccaptain.dto.UserDTO;
import io.veasna.ccaptain.form.LoginForm;
import io.veasna.ccaptain.provider.TokenProvider;
import io.veasna.ccaptain.service.RoleService;
import io.veasna.ccaptain.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static io.veasna.ccaptain.dtomapper.UserDTOMapper.toUser;
import static java.time.Instant.now;
import static java.util.Map.of;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
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
    private final UserService userService;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<HttpResponse> login(@RequestBody @Valid LoginForm loginForm){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginForm.getEmail(),loginForm.getPassword()));

        UserDTO user = userService.getUserByEmail(loginForm.getEmail());
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
    @PostMapping("/verify/code/{email}/{code}")
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

    private URI getUri() {
        return URI.create(fromCurrentContextPath().path("/user/get/<userId>").toUriString());
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
        return new UserPrincipal(toUser(userService.getUserByEmail(user.getEmail())),roleService.getRoleByUserId(user.getId()).getPermission());
    }
}

package io.veasna.ccaptain.configuration;

import io.veasna.ccaptain.filter.CustomAuthorizationFilter;
import io.veasna.ccaptain.handler.CustomAccessDeniedHandler;
import io.veasna.ccaptain.handler.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

/**
 * @author Veasna
 * @version 1.0
 * @license Veasna , LLC
 * @since 4/11/23 15:01
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig
{
    private final BCryptPasswordEncoder encoder;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final UserDetailsService userDetailsService;
    private static final String[] PUBLIC_URL = {"/user/verify/password/**",
            "/user/login/**", "/user/verify/code/**", "/user/register/**", "/user/resetpassword/**", "/user/verify/account/**",
            "/user/refresh/token/**", "/user/image/**", "/user/new/password/**" };
//    private static final String[] PUBLIC_URL = {"/**"};

    private final CustomAuthorizationFilter customAuthorizationFilter;
    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception {
        http.csrf().disable().cors();
        http.sessionManagement().sessionCreationPolicy(STATELESS);
        http.authorizeHttpRequests().requestMatchers(PUBLIC_URL).permitAll();
        http.authorizeHttpRequests().requestMatchers(HttpMethod.DELETE, "/user/delete/**").hasAuthority("DELETE:USER");
        http.authorizeHttpRequests().requestMatchers(HttpMethod.DELETE, "/customer/delete/**").hasAuthority("DELETE:CUSTOMER");
        http.exceptionHandling().accessDeniedHandler(customAccessDeniedHandler).authenticationEntryPoint(customAuthenticationEntryPoint);
        http.authorizeHttpRequests().anyRequest().authenticated();
        http.addFilterBefore(customAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(){

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(encoder);
        return new ProviderManager(authProvider);
    }

}

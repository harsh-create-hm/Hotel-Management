package com.hotel.management.service;

import com.hotel.management.dto.AuthResponse;
import com.hotel.management.dto.LoginRequest;
import com.hotel.management.dto.RegisterRequest;
import com.hotel.management.exception.BadRequestException;
import com.hotel.management.model.User;
import com.hotel.management.repository.UserRepository;
import com.hotel.management.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider tokenProvider;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L).name("Alice").email("alice@test.com")
                .password("encoded").role(User.Role.USER)
                .build();
    }

    @Test
    void register_newUser_returnsToken() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Alice");
        request.setEmail("alice@test.com");
        request.setPassword("password");

        when(userRepository.existsByEmail("alice@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(tokenProvider.generateToken("alice@test.com", "USER")).thenReturn("mock-token");

        AuthResponse result = authService.register(request);

        assertThat(result.getToken()).isEqualTo("mock-token");
        assertThat(result.getEmail()).isEqualTo("alice@test.com");
        assertThat(result.getRole()).isEqualTo("USER");
    }

    @Test
    void register_existingEmail_throwsBadRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("alice@test.com");
        request.setName("Alice");
        request.setPassword("password");

        when(userRepository.existsByEmail("alice@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already registered");
    }

    @Test
    void login_validCredentials_returnsToken() {
        LoginRequest request = new LoginRequest();
        request.setEmail("alice@test.com");
        request.setPassword("password");

        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(user));
        when(tokenProvider.generateToken("alice@test.com", "USER")).thenReturn("mock-token");

        AuthResponse result = authService.login(request);

        assertThat(result.getToken()).isEqualTo("mock-token");
        assertThat(result.getName()).isEqualTo("Alice");
    }
}

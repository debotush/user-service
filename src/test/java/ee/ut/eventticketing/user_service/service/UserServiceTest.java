package ee.ut.eventticketing.user_service.service;

import ee.ut.eventticketing.user_service.domain.Role;
import ee.ut.eventticketing.user_service.domain.User;
import ee.ut.eventticketing.user_service.dto.JwtResponse;
import ee.ut.eventticketing.user_service.dto.JwtValidationResponse;
import ee.ut.eventticketing.user_service.dto.LoginRequest;
import ee.ut.eventticketing.user_service.dto.RegisterRequest;
import ee.ut.eventticketing.user_service.dto.RoleUpdateRequest;
import ee.ut.eventticketing.user_service.dto.UpdateProfileRequest;
import ee.ut.eventticketing.user_service.dto.UserResponse;
import ee.ut.eventticketing.user_service.repository.UserRepository;
import ee.ut.eventticketing.user_service.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private RegisterRequest registerRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("123456789")
                .role(Role.USER)
                .build();

        user = User.builder()
                .userId(1L)
                .username("testuser")
                .email("test@example.com")
                .hashedPassword("encodedPassword")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("123456789")
                .role(Role.USER)
                .build();
    }

    @Test
    void register_Success() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserResponse response = userService.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals(user.getUsername(), response.getUsername());
        assertEquals(user.getEmail(), response.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_ThrowsException_WhenUsernameExists() {
        // Arrange
        when(userRepository.findByUsername(registerRequest.getUsername())).thenReturn(Optional.of(user));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.register(registerRequest));
        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_ThrowsException_WhenEmailExists() {
        // Arrange
        when(userRepository.findByUsername(registerRequest.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.of(user));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.register(registerRequest));
        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_Success() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("testuser", "password");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("testuser", "USER")).thenReturn("mockToken");

        // Act
        JwtResponse response = userService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("mockToken", response.getToken());
    }

    @Test
    void login_ThrowsException_WhenUserNotFound() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("unknown", "password");
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.login(loginRequest));
        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    void login_ThrowsException_WhenPasswordInvalid() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("testuser", "wrongpassword");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.login(loginRequest));
        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    void getUserProfile_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        UserResponse response = userService.getUserProfile(1L);

        // Assert
        assertNotNull(response);
        assertEquals(user.getUsername(), response.getUsername());
    }

    @Test
    void getUserProfile_ThrowsException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.getUserProfile(99L));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void updateProfile_Success() {
        // Arrange
        UpdateProfileRequest updateRequest = new UpdateProfileRequest("Jane", "Smith", "987654321");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserResponse response = userService.updateProfile(1L, updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals("Jane", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateRole_Success() {
        // Arrange
        RoleUpdateRequest roleRequest = new RoleUpdateRequest(Role.ADMIN);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserResponse response = userService.updateRole(1L, roleRequest);

        // Assert
        assertNotNull(response);
        assertEquals(Role.ADMIN, user.getRole());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void validateToken_Success() {
        // Arrange
        String token = "validToken";
        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.extractUsername(token)).thenReturn("testuser");
        when(jwtUtil.extractRole(token)).thenReturn("USER");

        // Act
        JwtValidationResponse response = userService.validateToken(token);

        // Assert
        assertTrue(response.isValid());
        assertEquals("testuser", response.getUsername());
        assertEquals("USER", response.getRole());
    }

    @Test
    void validateToken_Fail() {
        // Arrange
        String token = "invalidToken";
        when(jwtUtil.validateToken(token)).thenReturn(false);

        // Act
        JwtValidationResponse response = userService.validateToken(token);

        // Assert
        assertFalse(response.isValid());
    }
}

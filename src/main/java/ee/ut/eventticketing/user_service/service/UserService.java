package ee.ut.eventticketing.user_service.service;

import ee.ut.eventticketing.user_service.domain.Role;
import ee.ut.eventticketing.user_service.domain.User;
import ee.ut.eventticketing.user_service.dto.*;
import ee.ut.eventticketing.user_service.repository.UserRepository;
import ee.ut.eventticketing.user_service.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public UserResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .hashedPassword(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .role(request.getRole() != null ? request.getRole() : Role.USER)
                .build();

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    public JwtResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getHashedPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return new JwtResponse(token);
    }

    public UserResponse getUserProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToResponse(user);
    }

    public UserResponse updateProfile(Long id, UpdateProfileRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());

        User updatedUser = userRepository.save(user);
        return mapToResponse(updatedUser);
    }

    public UserResponse updateRole(Long id, RoleUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRole(request.getRole());
        User updatedUser = userRepository.save(user);
        return mapToResponse(updatedUser);
    }

    public JwtValidationResponse validateToken(String token) {
        boolean valid = jwtUtil.validateToken(token);
        if (valid) {
            return JwtValidationResponse.builder()
                    .valid(true)
                    .username(jwtUtil.extractUsername(token))
                    .role(jwtUtil.extractRole(token))
                    .build();
        }
        return JwtValidationResponse.builder().valid(false).build();
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .registrationDate(user.getRegistrationDate())
                .build();
    }
}

package ee.ut.eventticketing.user_service.dto;

import ee.ut.eventticketing.user_service.domain.Role;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Role role;
    private LocalDateTime registrationDate;
}

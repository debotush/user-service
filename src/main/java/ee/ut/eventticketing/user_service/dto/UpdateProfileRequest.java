package ee.ut.eventticketing.user_service.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    private String firstName;
    private String lastName;
    private String phoneNumber;
}

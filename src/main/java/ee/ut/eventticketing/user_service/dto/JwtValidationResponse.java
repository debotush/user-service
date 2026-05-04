package ee.ut.eventticketing.user_service.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtValidationResponse {
    private boolean valid;
    private String username;
    private String role;
}

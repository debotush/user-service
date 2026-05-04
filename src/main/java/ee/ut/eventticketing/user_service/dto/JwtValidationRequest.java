package ee.ut.eventticketing.user_service.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtValidationRequest {
    private String token;
}

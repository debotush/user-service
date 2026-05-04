package ee.ut.eventticketing.user_service.dto;

import ee.ut.eventticketing.user_service.domain.Role;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleUpdateRequest {
    private Role role;
}

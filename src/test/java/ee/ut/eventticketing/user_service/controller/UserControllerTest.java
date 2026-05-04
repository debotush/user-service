package ee.ut.eventticketing.user_service.controller;

import ee.ut.eventticketing.user_service.dto.JwtValidationResponse;
import ee.ut.eventticketing.user_service.service.UserService;
import ee.ut.eventticketing.user_service.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil; // Required by security context or controllers if used

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    public void validateToken_HappyPath_ReturnsValid() throws Exception {
        JwtValidationResponse response = JwtValidationResponse.builder()
                .valid(true)
                .username("testuser")
                .role("USER")
                .build();

        when(userService.validateToken(anyString())).thenReturn(response);

        mockMvc.perform(post("/users/validate")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\": \"valid-token\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @WithMockUser
    public void validateToken_InvalidToken_ReturnsInvalid() throws Exception {
        JwtValidationResponse response = JwtValidationResponse.builder()
                .valid(false)
                .build();

        when(userService.validateToken(anyString())).thenReturn(response);

        mockMvc.perform(post("/users/validate")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\": \"invalid-token\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.username").doesNotExist());
    }
}

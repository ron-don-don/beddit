package app.rondondon.beddit;

import app.rondondon.beddit.dto.request.*;
import app.rondondon.beddit.dto.response.EmailVerificationResponse;
import app.rondondon.beddit.dto.response.JwtResponse;
import app.rondondon.beddit.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
class AccountsTests extends AbstractTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockitoBean
    private final EmailService emailService;

    private String accessToken;
    private String username;

    @BeforeEach
    void setUp() throws Exception {
        doNothing().when(emailService).sendVerificationCode(anyString(), anyLong());
        when(emailService.verifyVerificationCode(anyString(), anyLong(), anyString())).thenReturn(new EmailVerificationResponse(true));
        username = UUID.randomUUID().toString();
        var response = registerUser(new AuthRequest(username, "12345"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        accessToken = objectMapper.readValue(response, JwtResponse.class).getAccess();
    }

    @Test
    void changePassword() throws Exception {
        mockMvc.perform(put("/account/change/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(objectMapper.writeValueAsString(new ChangePasswordRequest("234", "12345"))))
                .andExpect(status().isOk());

        loginUser(new AuthRequest(username, "234")).andExpect(status().isOk());
        loginUser(new AuthRequest(username, "12345")).andExpect(status().isUnauthorized());
    }

    @Test
    void changeEmail() throws Exception {
        mockMvc.perform(put("/account/change/email/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(objectMapper.writeValueAsString(new ChangeEmailStartRequest("newBob@gmail.com"))))
                .andExpect(status().isOk());
        mockMvc.perform(put("/account/change/email/finish")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(objectMapper.writeValueAsString(new ChangeEmailFinishRequest("123456", "newBob@gmail.com"))))
                .andExpect(status().isOk());
    }

    @Test
    void changeUsername() throws Exception {
        mockMvc.perform(put("/account/change/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(objectMapper.writeValueAsString(new ChangeUsernameRequest("newBob"))))
                .andExpect(status().isOk());

        mockMvc.perform(put("/account/change/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(objectMapper.writeValueAsString(new ChangeUsernameRequest("newBob"))))
                .andExpect(status().isConflict());
        loginUser(new AuthRequest("newBob", "12345")).andExpect(status().isOk());
    }

    private ResultActions registerUser(AuthRequest authRequest) throws Exception {
        return mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)));
    }
    private ResultActions loginUser(AuthRequest authRequest) throws Exception {
        return mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)));
    }
}

package app.rondondon.beddit;

import app.rondondon.beddit.dto.request.*;
import app.rondondon.beddit.dto.response.EmailVerificationResponse;
import app.rondondon.beddit.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
class AccountsTests extends AbstractTest {
    @MockitoBean
    private final EmailService emailService;

    private String accessToken;
    private String username;

    @BeforeEach
    void setUp() throws Exception {
        doNothing().when(emailService).sendVerificationCode(anyString(), anyLong());
        when(emailService.verifyVerificationCode(anyString(), anyLong(), anyString())).thenReturn(new EmailVerificationResponse(false));
        when(emailService.verifyVerificationCode(eq("123456"), anyLong(), eq("newBob@gmail.com"))).thenReturn(new EmailVerificationResponse(true));
        username = TestUtils.generateRandomString(8);
        accessToken = authActions.registerAndExtractToken(new AuthRequest(username, "12345b")).getAccess();
    }

    @Test
    void changePassword() throws Exception {
        accountActions.changePassword(new ChangePasswordRequest("234bbb", "12345b"), accessToken)
                .andExpect(status().isOk());

        accountActions.changePassword(new ChangePasswordRequest("234", "234bbb"), accessToken)
                .andExpect(status().isBadRequest());

        authActions.loginUser(new AuthRequest(username, "234bbb")).andExpect(status().isOk());
        authActions.loginUser(new AuthRequest(username, "12345b")).andExpect(status().isUnauthorized());
    }

    @Test
    void changeEmail() throws Exception {
        accountActions.startChangingEmail(new ChangeEmailStartRequest("newBob@gmail.com"), accessToken)
                .andExpect(status().isOk());

        var ver = objectMapper.readValue(accountActions.finishChangingEmail(new ChangeEmailFinishRequest("123444", "newBob@gmail.com"), accessToken)
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), EmailVerificationResponse.class).verified();
        assertThat(ver).isFalse();
        ver = objectMapper.readValue(accountActions.finishChangingEmail(new ChangeEmailFinishRequest("123456", "newBob123@gmail.com"), accessToken)
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), EmailVerificationResponse.class).verified();
        assertThat(ver).isFalse();

        ver = objectMapper.readValue(accountActions.finishChangingEmail(new ChangeEmailFinishRequest("123456", "newBob@gmail.com"), accessToken)
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), EmailVerificationResponse.class).verified();
        assertThat(ver).isTrue();
    }

    @Test
    void changeUsername() throws Exception {

        accountActions.changeUsername(new ChangeUsernameRequest("newBob"), accessToken)
                .andExpect(status().isOk());

        accountActions.changeUsername(new ChangeUsernameRequest("newBob"), accessToken)
                .andExpect(status().isConflict());
        authActions.loginUser(new AuthRequest("newBob", "12345b")).andExpect(status().isOk());
    }
}

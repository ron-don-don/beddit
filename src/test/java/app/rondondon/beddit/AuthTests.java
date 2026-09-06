package app.rondondon.beddit;

import app.rondondon.beddit.dto.request.AuthRequest;
import app.rondondon.beddit.dto.request.GoogleAuthRequest;
import app.rondondon.beddit.dto.request.JwtLogoutRequest;
import app.rondondon.beddit.dto.request.JwtRefreshRequest;
import app.rondondon.beddit.dto.response.JwtResponse;
import app.rondondon.beddit.security.GoogleTokenVerifier;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
class AuthTests extends AbstractTest {
    @MockitoBean
    private final GoogleTokenVerifier googleTokenVerifier;

    @Test
    void testAuth() throws Exception {
        var authActions = new AuthTestActions(mockMvc, objectMapper);
        authActions.registerUser(new AuthRequest("bob", "12345b"))
                .andExpect(status().isCreated());
        authActions.registerUser(new AuthRequest("bob", "12345b"))
                .andExpect(status().isConflict());
        String stringJwtResponse = authActions.loginUser(new AuthRequest("bob", "12345b"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        var jwtResponse = objectMapper.readValue(stringJwtResponse, JwtResponse.class);

        stringJwtResponse = authActions.refreshUserToken(new JwtRefreshRequest(jwtResponse.getRefresh()))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        jwtResponse = objectMapper.readValue(stringJwtResponse, JwtResponse.class);

        authActions.logoutUser(new JwtLogoutRequest(jwtResponse.getRefresh()))
                .andExpect(status().isOk());

        authActions.refreshUserToken(new JwtRefreshRequest(jwtResponse.getRefresh()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGoogleAuth() throws Exception {
        when(googleTokenVerifier.verify("correct-google-id-token"))
                .thenReturn("bob1029@gmail.com");
        authActions.loginWithGoogle(new GoogleAuthRequest("correct-google-id-token")).andExpect(status().isOk());
        authActions.loginWithGoogle(new GoogleAuthRequest("incorrect-google-id-token")).andExpect(status().isUnauthorized());
    }
}

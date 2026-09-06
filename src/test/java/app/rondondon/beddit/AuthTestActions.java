package app.rondondon.beddit;


import app.rondondon.beddit.dto.request.AuthRequest;
import app.rondondon.beddit.dto.request.GoogleAuthRequest;
import app.rondondon.beddit.dto.request.JwtLogoutRequest;
import app.rondondon.beddit.dto.request.JwtRefreshRequest;
import app.rondondon.beddit.dto.response.JwtResponse;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthTestActions extends AbstractTestActions {
    AuthTestActions(MockMvc mockMvc, ObjectMapper objectMapper) {
        super(mockMvc, objectMapper);
    }

    public ResultActions registerUser(AuthRequest authRequest) throws Exception {
        return mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)));
    }

    public JwtResponse registerAndExtractToken(AuthRequest authRequest) throws Exception {
        var result = registerUser(authRequest).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(result, JwtResponse.class);
    }

    public ResultActions loginUser(AuthRequest authRequest) throws Exception {
        return mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)));
    }

    public ResultActions refreshUserToken(JwtRefreshRequest req) throws Exception {
        return mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));
    }

    public ResultActions logoutUser(JwtLogoutRequest req) throws Exception {
        return mockMvc.perform(post("/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));
    }

    public ResultActions loginWithGoogle(GoogleAuthRequest req) throws Exception {
        return mockMvc.perform(post("/auth/oauth2/google/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));
    }
}

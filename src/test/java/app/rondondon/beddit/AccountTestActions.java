package app.rondondon.beddit;

import app.rondondon.beddit.dto.request.ChangeEmailFinishRequest;
import app.rondondon.beddit.dto.request.ChangeEmailStartRequest;
import app.rondondon.beddit.dto.request.ChangePasswordRequest;
import app.rondondon.beddit.dto.request.ChangeUsernameRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class AccountTestActions extends AbstractTestActions {

    public AccountTestActions(MockMvc mockMvc, ObjectMapper objectMapper) {
        super(mockMvc, objectMapper);
    }

    public ResultActions changePassword(ChangePasswordRequest req, String access) throws Exception {
        return mockMvc.perform(put("/account/change/password")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + access)
                .content(objectMapper.writeValueAsString(req)));
    }

    public ResultActions startChangingEmail(ChangeEmailStartRequest req, String access) throws Exception {
        return mockMvc.perform(put("/account/change/email/start")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + access)
                .content(objectMapper.writeValueAsString(req)));
    }

    public ResultActions finishChangingEmail(ChangeEmailFinishRequest req, String access) throws Exception {
        return mockMvc.perform(put("/account/change/email/finish")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + access)
                .content(objectMapper.writeValueAsString(req)));
    }

    public ResultActions changeUsername(ChangeUsernameRequest req, String access) throws Exception {
        return mockMvc.perform(put("/account/change/username")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + access)
                .content(objectMapper.writeValueAsString(req)));
    }
}

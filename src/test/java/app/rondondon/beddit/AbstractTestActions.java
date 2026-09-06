package app.rondondon.beddit;

import lombok.RequiredArgsConstructor;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
public class AbstractTestActions {
    protected final MockMvc mockMvc;
    protected final ObjectMapper objectMapper;
}

package app.rondondon.beddit;

import app.rondondon.beddit.dto.request.AuthRequest;
import app.rondondon.beddit.dto.request.GoogleAuthRequest;
import app.rondondon.beddit.dto.request.JwtLogoutRequest;
import app.rondondon.beddit.dto.request.JwtRefreshRequest;
import app.rondondon.beddit.dto.response.JwtResponse;
import app.rondondon.beddit.security.GoogleTokenVerifier;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
class AuthTests extends AbstractTest {
	private final MockMvc mockMvc;
	private final ObjectMapper objectMapper;

	@MockitoBean
	private final GoogleTokenVerifier  googleTokenVerifier;

	@Test
	void testAuth() throws Exception {
		registerUser(new AuthRequest("bob", "12345"))
				.andExpect(status().isCreated());
		String stringJwtResponse = loginUser(new AuthRequest("bob", "12345"))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		var jwtResponse = objectMapper.readValue(stringJwtResponse, JwtResponse.class);

		stringJwtResponse = refreshUserToken(new JwtRefreshRequest(jwtResponse.getRefresh()))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		jwtResponse = objectMapper.readValue(stringJwtResponse, JwtResponse.class);

		logoutUser(new JwtLogoutRequest(jwtResponse.getRefresh()))
				.andExpect(status().isOk());

		refreshUserToken(new JwtRefreshRequest(jwtResponse.getRefresh()))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void testGoogleAuth() throws Exception {
		when(googleTokenVerifier.verify(anyString()))
				.thenReturn("bob1029@gmail.com");

		loginWithGoogle(new GoogleAuthRequest("correct-google-id-token")).andExpect(status().isOk());
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
	private ResultActions refreshUserToken(JwtRefreshRequest req) throws Exception {
		return mockMvc.perform(post("/auth/refresh")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)));
	}
	private ResultActions logoutUser(JwtLogoutRequest req) throws Exception {
		return mockMvc.perform(post("/auth/logout")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)));
	}
	private ResultActions loginWithGoogle(GoogleAuthRequest req) throws Exception {
		return mockMvc.perform(post("/auth/oauth2/google/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)));
	}

}

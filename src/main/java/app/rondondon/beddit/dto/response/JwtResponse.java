package app.rondondon.beddit.dto.response;


import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;


@Getter
public class JwtResponse {
    public JwtResponse(String access, String refresh, Integer expiresIn) {
        this.access = access;
        this.refresh = refresh;
        this.expiresIn = expiresIn;
    }
    private final String access;
    private final String refresh;
    private final String type = "Bearer";
    private final Integer expiresIn;
}

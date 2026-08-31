package app.rondondon.beddit.security;


import app.rondondon.beddit.exception.InvalidGoogleTokenException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URI;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;

@Component
public class GoogleTokenVerifier {

    private final String GOOGLE_JWKS_URI = "https://www.googleapis.com/oauth2/v3/token";
    private final String ISSUER = "accounts.google.com";

    @Value("${app.oidc.google.client-id}")
    private String clientId;

    private final ConfigurableJWTProcessor<SecurityContext> jwtProcessor;

    GoogleTokenVerifier() throws MalformedURLException {
        var keySource = JWKSourceBuilder.create(URI.create(GOOGLE_JWKS_URI).toURL()).build();
        var keySelector = new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, keySource);
        jwtProcessor = new DefaultJWTProcessor<>();
        jwtProcessor.setJWSKeySelector(keySelector);
    }

    public String verify(String idToken) {
        try {
            var claims = jwtProcessor.process(idToken, null);
            if (!claims.getAudience().contains(clientId)) {
                throw new BadJOSEException("Invalid audience");
            }
            if (!claims.getIssuer().equals(ISSUER)) {
                throw new BadJOSEException("Invalid issuer");
            }
            if (claims.getExpirationTime().before(Date.from(Instant.now()))){
                throw new BadJOSEException("Token expired");
            }

            return claims.getClaim("email").toString();
        }
        catch (Exception e) {
            throw new InvalidGoogleTokenException("Invalid Google Token");
        }
    }
}

package app.rondondon.beddit.security;


import app.rondondon.beddit.exception.AuthenticationException;
import app.rondondon.beddit.exception.ErrorCode;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
public class GoogleTokenVerifier {

    private final Issuer GOOGLE_ISSUER = new Issuer("https://accounts.google.com");

    private final ConfigurableJWTProcessor<SecurityContext> jwtProcessor;

    GoogleTokenVerifier(@Value("${app.oidc.google.client-id}") String clientId) {
        var tempJwtProcessor = new DefaultJWTProcessor<>();
        try{
            OIDCProviderMetadata metadata = OIDCProviderMetadata.resolve(GOOGLE_ISSUER);
            var jwksUri = metadata.getJWKSetURI().toURL();
            var keySource = JWKSourceBuilder.create(jwksUri)
                    .cache(true)
                    .rateLimited(true)
                    .build();
            var keySelector = new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, keySource);
            tempJwtProcessor.setJWSKeySelector(keySelector);

            tempJwtProcessor.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier<>(
                    new JWTClaimsSet.Builder()
                            .issuer(metadata.getIssuer().getValue())
                            .audience(clientId)
                            .build()
                    , Set.of("exp")
            ));
        }
        catch (Exception e) {
            this.jwtProcessor = null;
            log.error("Cannot connect to google oidc provider");
            return;
        }
        this.jwtProcessor = tempJwtProcessor;
    }

    public String verify(String idToken) {
        JWTClaimsSet claims;
        try {
            claims = jwtProcessor.process(idToken, null);
        }
        catch (Exception e) {
            log.warn("Incorrect google id token");
            throw new AuthenticationException(ErrorCode.INCORRECT_GOOGLE_TOKEN);
        }
        if (!(boolean) claims.getClaim("email_verified")) {
            log.trace("User has unverified email: {}", claims.getClaim("email"));
            throw new AuthenticationException(ErrorCode.UNVERIFIED_EMAIL);
        }
        log.trace("Google id token verified for email: {}", claims.getClaim("email"));
        return claims.getClaim("email").toString();
    }
}

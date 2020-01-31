package com.mes.gotogether.security.jwt;

import com.mes.gotogether.security.domain.SecurityUserLibrary;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClock;
import java.io.Serializable;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Slf4j
@Component
public class JWTUtil implements Serializable  {

    private static final long serialVersionUID = 1L;
    private Environment env;
    static final String CLAIM_KEY_USERNAME = "sub";
    static final String CLAIM_KEY_AUDIENCE = "aud";
    static final String CLAIM_KEY_CREATED = "iat";
    static final String AUDIENCE_UNKNOWN = "unknown";
    static final String AUDIENCE_WEB = "web";
    static final String AUDIENCE_MOBILE = "mobile";
    static final String AUDIENCE_TABLET = "tablet";
    
    private final Clock clock = DefaultClock.INSTANCE;
    @Value("${jwt.secret}")
    private final String secret;
    private final Key signingKey;
    @Value("${jwt.expiration}")
    private final Long expiration;

    public JWTUtil(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") Long expiration) {
        Assert.notNull(secret, "secret cannot be null");
        Assert.notNull(expiration, "expiration cannot be null");
        log.info("Secret is: " + secret);
        this.secret = secret;
        this.expiration = expiration;

        // Sign JWT with ApiKey secret
        String encodedString = Base64.getEncoder().encodeToString(secret.getBytes());
        byte[] apiKeySecretBytes = Base64.getDecoder().decode(encodedString);
        this.signingKey = new SecretKeySpec(apiKeySecretBytes, "HmacSHA512");
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getIssuedAtDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getIssuedAt);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public String getAudienceFromToken(String token) {
        return getClaimFromToken(token, Claims::getAudience);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expirationPeriod = getExpirationDateFromToken(token);
        return expirationPeriod.before(clock.now());
    }

    private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return (lastPasswordReset != null && created.before(lastPasswordReset));
    }

    private String generateAudience() {
        String audience = AUDIENCE_UNKNOWN;
        return audience;
    }

    private Boolean ignoreTokenExpiration(String token) {
        String audience = getAudienceFromToken(token);
        return (AUDIENCE_TABLET.equals(audience) || AUDIENCE_MOBILE.equals(audience));
    }

    public String generateToken(SecurityUserLibrary userDetails) {
        log.info("Inside generate token function");
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getRoles());
        log.info("Claims are put:");
        String value = doGenerateToken(claims, userDetails.getUsername(), generateAudience());
        log.info("Inside generate token function generated value: " + value);
        return value;
    }

    private String doGenerateToken(Map<String, Object> claims, String subject, String audience) {
        log.info("Inside doGenerate function");
        final Date createdDate = clock.now();
        final Date expirationDate = calculateExpirationDate(createdDate);
        log.info("expirationDate: " + expirationDate + " createdDate: " + createdDate);
        log.info("signing key is: " + signingKey);
        log.info("Claims: " +claims + " audience: " + audience + " subject: " + subject);
        log.info("Set the signature algorithm: " + SignatureAlgorithm.HS512);
        JwtBuilder jwtbuilder =Jwts.builder();
        log.info("jwtBuilder is created");
        jwtbuilder =jwtbuilder.setClaims(claims);
        log.info("jwtBuilder claims is created");
        jwtbuilder =jwtbuilder.setSubject(subject);
        log.info("jwtBuilder subject is created");
        jwtbuilder =jwtbuilder.setAudience(audience);
        log.info("jwtBuilder audience is created");
        jwtbuilder =jwtbuilder.setIssuedAt(createdDate);
        log.info("jwtBuilder set issued date is created");
        jwtbuilder =jwtbuilder.setExpiration(expirationDate);
        log.info("jwtBuilder set expiration date is created");
        jwtbuilder =jwtbuilder.signWith(signingKey, SignatureAlgorithm.HS256);
        log.info("jwtBuilder set expiration date is created");
        String testValue =jwtbuilder.compact();
        log.info("jwtBuilder test value is: " + testValue);
        String value = Jwts.builder()
                            .setClaims(claims)
                            .setSubject(subject)
                            .setAudience(audience)
                            .setIssuedAt(createdDate)
                            .setExpiration(expirationDate)
                            .signWith(signingKey, SignatureAlgorithm.HS512)
                            .compact();
        log.info("value: " + value);
        return value;
    }

    public Boolean canTokenBeRefreshed(String token, Date lastPasswordReset) {
        final Date created = getIssuedAtDateFromToken(token);
        return !isCreatedBeforeLastPasswordReset(created, lastPasswordReset)
                && (!isTokenExpired(token) || ignoreTokenExpiration(token));
    }

    public String refreshToken(String token) {
        final Date createdDate = clock.now();
        final Date expirationDate = calculateExpirationDate(createdDate);

        final Claims claims = getAllClaimsFromToken(token);
        claims.setIssuedAt(createdDate);
        claims.setExpiration(expirationDate);

        return Jwts.builder()
                            .setClaims(claims)
                            .signWith(signingKey, SignatureAlgorithm.HS512 )
                            .compact();
    }

    public Boolean validateToken(String token) {
        final String username = getUsernameFromToken(token);
        final Date created = getIssuedAtDateFromToken(token);
        //final Date expiration = getExpirationDateFromToken(token);
        return (
                username.equals(username)
                        && !isTokenExpired(token)
        );
    }

    private Date calculateExpirationDate(Date createdDate) {
        return new Date(createdDate.getTime() + expiration * 1000);
    }
}

package com.mes.gotogether.security.jwt;

import com.mes.gotogether.domains.Role;
import com.mes.gotogether.security.service.SecurityUserLibraryUserDetailsService;
import io.jsonwebtoken.Claims;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JWTReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    // private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SecurityUserLibraryUserDetailsService securityUserDetailsService;
    private final JWTUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;

    public JWTReactiveAuthenticationManager(SecurityUserLibraryUserDetailsService securityUserDetailsService,
                                            JWTUtil jwtTokenUtil) {
        Assert.notNull(securityUserDetailsService, "userDetailsService cannot be null");
        Assert.notNull(jwtTokenUtil, "jwtTokenUtil cannot be null");
        log.info("***********IN AUTHENTICATION MANAGER*******************");
        this.securityUserDetailsService = securityUserDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }    
    
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();

        String username;
        try {
            username = jwtTokenUtil.getUsernameFromToken(authToken);
            if (jwtTokenUtil.validateToken(authToken)) {
                Claims claims = jwtTokenUtil.getAllClaimsFromToken(authToken);
                List<String> rolesMap = claims.get("role", List.class);
                List<Role> roles = new ArrayList<>();
                for (String rolemap : rolesMap) {
                    roles.add(Role.valueOf(rolemap));
                }

                log.info("HERE IN AUTHENTICATE*****: " + "- Roles: " + roles);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        roles.stream().map(authority -> new SimpleGrantedAuthority("ROLE_"+authority.name()))
                                .collect(Collectors.toList())
                );
                log.info("Finalized auth: " + auth);
                return Mono.just(auth);
            }else{
                log.info("Invalid token. Send null");
                 return Mono.empty();
            }
        } catch (Exception ex) {
                log.info("Error in examining token");
                return Mono.empty();
        }
    }

    /*
    @Override
    public Mono<Authentication> authenticate(final Authentication authentication) {
        System.out.println("*************** IN AUTHENTICATE**********");

        if (authentication instanceof JwtPreAuthenticationToken) {

            System.out.println("**** INSIDE PREAUTH CASE ******");
            return Mono.just(authentication)
                    .switchIfEmpty(Mono.defer(this::raiseBadCredentials))
                    .cast(JwtPreAuthenticationToken.class)
                    .flatMap(this::authenticateToken)
                    .publishOn(Schedulers.parallel())
                    .onErrorResume(e -> raiseBadCredentials())
                    .map(u -> new JwtAuthenticationToken(u.getUsername(), u.getAuthorities()));
        }

        return Mono.just(authentication);
    }

    private <T> Mono<T> raiseBadCredentials() {
        return Mono.error(new BadCredentialsException("Invalid Credentials"));
    }

    private Mono<UserDetails> authenticateToken(final JwtPreAuthenticationToken jwtPreAuthenticationToken) {
        try {
            String authToken = jwtPreAuthenticationToken.getAuthToken();
            String username = jwtPreAuthenticationToken.getUsername();
            String bearerRequestHeader = jwtPreAuthenticationToken.getBearerRequestHeader();

            logger.info("checking authentication for user " + username);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtTokenUtil.validateToken(authToken)) {
                    logger.info("authenticated user " + username + ", setting security context");
                    final String token = authToken;
                    return this.securityUserDetailsService.findByUserId(username);
                }
            }
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid token...");
        }

        return null;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        Assert.notNull(passwordEncoder, "passwordEncoder cannot be null");
        this.passwordEncoder = passwordEncoder;
    }*/
}

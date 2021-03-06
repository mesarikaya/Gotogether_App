package com.mes.gotogether.security.config;

import com.mes.gotogether.security.jwt.JWTReactiveAuthenticationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class SecurityContextRepository implements ServerSecurityContextRepository {

    private final JWTReactiveAuthenticationManager JWTReactiveAuthenticationManager;

    public SecurityContextRepository(JWTReactiveAuthenticationManager JWTReactiveAuthenticationManager) {
        this.JWTReactiveAuthenticationManager = JWTReactiveAuthenticationManager;
    }

    @Override
    public Mono<Void> save(ServerWebExchange swe, SecurityContext sc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange swe) {

        log.info("Load security context Server Exchange load");
        ServerHttpRequest request = swe.getRequest();
        log.info("Request is:" + request.getHeaders());
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        log.info("Auth header: " + authHeader);
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String authToken = authHeader.substring(7);
                Authentication auth = new UsernamePasswordAuthenticationToken(authToken, authToken);
               log.info("Auth is set: " + auth);
                return this.JWTReactiveAuthenticationManager.authenticate(auth).map((authentication) -> {
                    log.info("Calling authenticate method");
                    SecurityContextImpl scImpl =  new SecurityContextImpl(authentication);
                    log.info("In auth manager map: " + scImpl);
                    return scImpl;
                });
            } else {
                log.info("********WRONG CREDENTIALS OR SECURITY IS MISSED*********");
                return Mono.empty();
            }
        }catch(Exception ex){
            return Mono.error(new RuntimeException(ex.getMessage()));
        }
    }
}
package com.mes.gotogether.security.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;


public class JwtAuthenticationConverter implements ServerAuthenticationConverter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ReactiveUserDetailsService userDetailsService;
    private final JWTUtil jwtTokenUtil;

    public JwtAuthenticationConverter(ReactiveUserDetailsService userDetailsService
            , JWTUtil jwtTokenUtil) {
        Assert.notNull(userDetailsService, "userDetailsService cannot be null");
        Assert.notNull(jwtTokenUtil, "jwtTokenUtil cannot be null");

        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${jwt.param}")
    private String tokenParam;

    @Value("${jwt.prefix}")
    private String bearerPrefix;

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) throws BadCredentialsException {
        ServerHttpRequest request = exchange.getRequest();
        try {

            Authentication authentication = null;
            String authToken = null;
            String username = null;

            String bearerRequestHeader = exchange.getRequest().getHeaders().getFirst(tokenHeader);

            System.out.println("Auth token: " + authToken + " username: " + username);
            System.out.println("BearerRequestHeader: " + bearerRequestHeader);


            System.out.println("IF CHECK: " + authToken + " - " + request.getQueryParams()  + "-"  + request.getQueryParams());
            System.out.println("IF CHECK: " + authToken == null );
            System.out.println("IF CHECK: " + request.getQueryParams() != null );
            System.out.println("IF CHECK: " + !request.getQueryParams().isEmpty());


            if (bearerRequestHeader != null && bearerRequestHeader.startsWith(bearerPrefix + " ")) {
                authToken = bearerRequestHeader.substring(7);
            }

            if (authToken == null && request.getQueryParams() != null && !request.getQueryParams().isEmpty()) {
                String authTokenParam = request.getQueryParams().getFirst(tokenParam);

                if (authTokenParam != null) authToken = authTokenParam;
            }

            if (authToken != null) {
                try {
                    username = jwtTokenUtil.getUsernameFromToken(authToken);
                } catch (IllegalArgumentException e) {
                    logger.error("an error occured during getting username from token", e);
                } catch (Exception e) {
                    logger.warn("the token is expired and not valid anymore", e);
                }
            } else {
                logger.warn("couldn't find bearer string, will ignore the header");
            }

            logger.info("checking authentication for user " + username);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                return Mono.just(new JwtPreAuthenticationToken(authToken, bearerRequestHeader, username));
            }

            return Mono.just(authentication);
        } catch (Exception e) {
            System.out.println("Error: " + e);
            throw new BadCredentialsException("Invalid token...");
        }
    }
}

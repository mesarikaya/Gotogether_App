package com.mes.gotogether.controllers.viewControllers;

import com.mes.gotogether.security.domain.AuthRequest;
import com.mes.gotogether.security.domain.SecurityUserLibrary;
import com.mes.gotogether.security.jwt.JWTUtil;
import com.mes.gotogether.security.service.SecurityUserLibraryUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Mono;


public class AuthenticationController2 {

    private JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private SecurityUserLibraryUserDetailsService securityUserService;

    @Autowired
    public AuthenticationController2(JWTUtil jwtUtil, PasswordEncoder passwordEncoder,
                                    SecurityUserLibraryUserDetailsService securityUserService) {
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.securityUserService = securityUserService;
    }

    @PostMapping(value = "/auth")
    public Mono<String> login(AuthRequest ar, ServerHttpResponse serverHttpResponse) {

        System.out.println("Authorization: " + ar.getUserName()
                + "password: " + ar.getPassword()
                + "encoded: " + passwordEncoder.encode(ar.getPassword()));

        return securityUserService.findByUserId(ar.getUserName()).map((userDetails) -> {
            System.out.println("userdetails password: " + userDetails.getPassword());
            if (passwordEncoder.matches(ar.getPassword(), userDetails.getPassword())) {
                System.out.println("Authorized! YEAH!!!!");

                String token = jwtUtil.generateToken((SecurityUserLibrary) userDetails);

                ResponseCookie cookie = ResponseCookie.from("JWTCookie", token)
                        .maxAge(3600)
                        .build();

                serverHttpResponse.addCookie(cookie);

                return "user";
            } else {
                System.out.println("Returning unauthorized");
                return "login";
            }
        }).defaultIfEmpty("login").single();
    }
}
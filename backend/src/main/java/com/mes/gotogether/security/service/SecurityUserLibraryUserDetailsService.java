package com.mes.gotogether.security.service;

import org.springframework.security.core.userdetails.UserDetails;

import reactor.core.publisher.Mono;

public interface SecurityUserLibraryUserDetailsService{

    Mono<UserDetails> findByUserId(String userId);
    Mono<UserDetails> updatePassword(UserDetails userDetails,  String password);
}

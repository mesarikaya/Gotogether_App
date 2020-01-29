package com.mes.gotogether.security.repository;

import com.mes.gotogether.security.domain.SecurityUserLibrary;
import com.mes.gotogether.services.domain.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class SecurityUserLibraryUserDetailsRepository
        implements ReactiveUserDetailsService, ReactiveUserDetailsPasswordService {

    private UserService userService;

    public SecurityUserLibraryUserDetailsRepository(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Mono<UserDetails> findByUsername(String userId) {
        log.warn("****CALLING SECURITYDETAILS REPOSITORY FINDBYUSERNAME FUNCTION");
        return userService.findByUserId(userId)
                                     .switchIfEmpty(Mono.defer(() -> {
                                         log.warn("User not found in In memory User Details Repository method");
                                         return Mono.error(new UsernameNotFoundException("User Not Found"));
                                     }))
                                     .map(SecurityUserLibrary::new);
    }

    @Override
    public Mono<UserDetails> updatePassword(UserDetails user, String newPassword) {
        log.warn("Password upgrade for user with name '{}'", user.getUsername());
        log.warn("Password upgraded from '{}' to '{}'", user.getPassword(), newPassword);
        return userService.findByUserId(user.getUsername())
                                     .switchIfEmpty(Mono.defer(() -> {
                                         return Mono.error(new UsernameNotFoundException("User Not Found"));
                                     }))
                                     .doOnSuccess(u -> u.setPassword(newPassword))
                                     .flatMap(userService::saveOrUpdateUser)
                                     .map(SecurityUserLibrary::new);
    }
}

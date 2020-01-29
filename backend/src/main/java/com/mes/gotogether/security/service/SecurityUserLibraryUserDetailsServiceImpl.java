package com.mes.gotogether.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.mes.gotogether.security.repository.SecurityUserLibraryUserDetailsRepository;

import reactor.core.publisher.Mono;

@Service
public class SecurityUserLibraryUserDetailsServiceImpl implements SecurityUserLibraryUserDetailsService {

    private SecurityUserLibraryUserDetailsRepository securityUserDataDetailsRepository;

    public SecurityUserLibraryUserDetailsServiceImpl(SecurityUserLibraryUserDetailsRepository securityUserDataDetailsRepository) {
        this.securityUserDataDetailsRepository = securityUserDataDetailsRepository;
    }

    @Override
    public Mono<UserDetails> findByUserId(String userId) {
        return securityUserDataDetailsRepository.findByUsername(userId);
    }

    @Override
    public Mono<UserDetails> updatePassword(UserDetails user, String newPassword) {
        return securityUserDataDetailsRepository.updatePassword(user, newPassword);
    }
}

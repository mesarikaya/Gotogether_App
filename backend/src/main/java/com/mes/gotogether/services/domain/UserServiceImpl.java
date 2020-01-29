package com.mes.gotogether.services.domain;

import com.mes.gotogether.domains.User;
import com.mes.gotogether.repositories.domain.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final  UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<User> findUserById(ObjectId id) {

        if (Objects.isNull(id)) {
            return Mono.empty();
        }

        return userRepository.findById(id);
    }

    @Override
    public Mono<List<User>> findAllUsers() {
        return userRepository.findAll().collectList();
    }

    @Override
    @PreAuthorize("isAnonymous() or isAuthenticated()")
    public Mono<User> findByUserId(String userId) {

        // Check null cases
        if (ObjectUtils.isEmpty(userId)) {
            return Mono.empty();
        }
        
        Sort sort = new Sort(Sort.Direction.ASC, "id"); 
        // Check if user exists
        return userRepository.findByUserId(userId, sort);
    }

    @Override
    public Mono<User> createUser(User user) {

        if (Objects.isNull(user)) {
            return Mono.empty();
        }

        return userRepository.save(user);
    }

    @Override
    public Mono<User> saveOrUpdateUser(User user) {

        // Check if new user is null, empty cases
        if (!Objects.isNull(user)){
            if (!Objects.isNull(user.getUserId())){
                return this.createUser(user);
            } else{
                Sort sort = new Sort(Sort.Direction.ASC, "id");
                return userRepository.findByUserId(user.getUserId(), sort)
                                                    .flatMap(userInDb -> {
                                                        log.debug("user in db is: " + userInDb);
                                                        log.info("Update the user");
                                                        user.setId(userInDb.getId());
                                                        log.info("USER in repository: " + user);
                                                        return userRepository.save(user);
                                                    })
                                                    .switchIfEmpty(Mono.defer(() -> {
                                                        log.info("Creating a new User");
                                                        log.info("USER in repository: " + user);
                                                        return this.createUser(user);
                                                     }));
            }
        }else{
            log.info("A Null user data is entered. Do not process!");
            return Mono.empty();
        }
    }

    @Override
    public Mono<Void> deleteUserById(ObjectId id) {

        if (Objects.isNull(id)) {
            return Mono.empty();
        }

        return userRepository.deleteById(id);
    }

    @Override
    public Mono<Void> deleteByUserId(String userId) {

        // Check null cases
        if (ObjectUtils.isEmpty(userId)) {
            return Mono.empty();
        }

        return userRepository.deleteByUserId(userId);
    }

    @Override
    public Mono<Void> deleteAll() {
        return userRepository.deleteAll();
    }

    @Override
    public Mono<User> renewVerificationDetails(String userId) {
        
        String verificationToken = UUID.randomUUID().toString();
        LocalDateTime verificationTokenExpiresAt = LocalDateTime.now().plusMinutes(10);
        Sort sort = new Sort(Sort.Direction.ASC, "id"); 
        return userRepository.findByUserId(userId, sort)
                                            .flatMap( u -> {
                                                u.setVerificationToken(verificationToken);
                                                u.setVerificationExpiresAt(verificationTokenExpiresAt);
                                                u.setVerified(false);
                                                return this.saveOrUpdateUser(u);
                                            });

    }
    
    
}

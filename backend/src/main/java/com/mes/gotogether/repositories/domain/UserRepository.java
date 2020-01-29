package com.mes.gotogether.repositories.domain;

import com.mes.gotogether.domains.User;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveMongoRepository<User, ObjectId> {
    @Query("{ 'userId' : ?0}")
    Mono<User> findByUserId(String userId, Sort sort);
    Mono<Void> deleteByUserId(String userId);
}

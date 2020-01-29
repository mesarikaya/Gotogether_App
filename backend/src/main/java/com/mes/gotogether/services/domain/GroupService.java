package com.mes.gotogether.services.domain;

import com.mes.gotogether.domains.Address;
import com.mes.gotogether.domains.Group;
import com.mes.gotogether.domains.User;
import java.util.Set;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupService {

    Mono<Group> findById(ObjectId id);
    Flux<Group> findGroupsByOriginAddress(Address originAddress);
    Flux<Group> findGroupsByDestinationAddress(Address destinationAddress);
    Flux<Group> findGroupsByName(String groupName);

    Flux<Group> findGroupsByOriginAndDestinationAddress(Address originAddress, Address destinationAddress);
    
    Flux<Group> findGroupsByOriginAndDestinationAddress(String originAddress, String destinationAddress, 
                                                                                                          double originRadius, double destinationRadius, 
                                                                                                          Pageable page);
    
    Flux<Group> findGroupsByOriginAndDestinationGeoLocationDetails(
            double originLatitude,
            double originLongitude,
            double destinationLatitude,
            double destinationLongitude
    );

    Flux<Group> findGroupsByOriginWithinThresholds(
            double originLatMin, double originLatMax,
            double originLongMin, double originLongMax);

    Flux<Group> findGroupsByDestinationWithinThresholds(
            double destLatMin, double destLatMax,
            double destLongMin, double destLongMax);

    Flux<Group> findGroupsByOriginAndDestinationWithinThresholds(
            double originLatMin, double originLatMax,
            double originLongMin, double originLongMax,
            double destLatMin, double destLatMax,
            double destLongMin, double destLongMax,
            Pageable page);

    Flux<Group> findGroupsByOriginWithinRadius(
            double originLat, double originLong, double originRadius);

    Flux<Group> findGroupsByDestinationWithinRadius(
            double destLat, double destLong, double destRadius);

    Flux<Group> findGroupsByOriginAndDestinationWithinRadius(
            double originLat, double originLong, double originRadius,
            double destLat, double destLong, double destRadius,
            Pageable page);
    
    Mono<Set<User>> findMembers(ObjectId id);
    
    Flux<Group> findAll();

    Mono<Group> saveOrUpdate(Group group);
    Mono<Group> saveFakeGroup(Group group);
    
    Mono<Group> deleteUserFromWaitingList(ObjectId id, String userId);
    Mono<Group> addUserToWaitingList(ObjectId id, String userId);
    
    Mono<Group> deleteUserFromInvitesList(ObjectId id, String userId);
    Mono<Group> addUserToInvitesList(ObjectId id, String userId);
    
    Mono<Void> deleteById(ObjectId id);
    Mono<Void> deleteAll();

    Mono<Group> addMemberByUserId(ObjectId id, String userId);
    Mono<Group> deleteMemberByUserId(ObjectId id, String userId);
}

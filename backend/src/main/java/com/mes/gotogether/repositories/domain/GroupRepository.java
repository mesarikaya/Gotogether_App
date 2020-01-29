package com.mes.gotogether.repositories.domain;

import com.mes.gotogether.domains.Address;
import com.mes.gotogether.domains.Group;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface GroupRepository extends ReactiveMongoRepository<Group, ObjectId> {

    Flux<Group> findGroupsByOriginAddress(Address originAddress);
    Flux<Group> findGroupsByDestinationAddress(Address destinationAddress);
    Flux<Group> findGroupsByOriginAddressAndDestinationAddress(Address originAddress, Address destinationAddress);
    Flux<Group> findGroupsByName(String groupName);

    @Query("{$and: [{'originAddress.latitude': ?0}, {'originAddress.longitude': ?1}, {'destinationAddress.latitude': ?2}, {'destinationAddress.longitude': ?3}]}")
    Flux<Group> findGroupsByOriginAndDestinationGeoLocationDetails(
            double originLatitude,
            double originLongitude,
            double destinationLatitude,
            double destinationLongitude
    );

    @Query("{$and: [{'originAddress.latitude': {$gte: ?0, $lte: ?1 }},  {'originAddress.longitude': {$gte: ?2, $lte: ?3 }}]}")
    Flux<Group> findGroupsByOriginWithinThresholds(
            double originLatMin, double originLatMax,
            double originLongMin, double originLongMax);

    @Query("{$and: [{'destinationAddress.latitude': {$gte: ?0, $lte: ?1 }}, {'destinationAddress.longitude': {$gte: ?2, $lte: ?3 }}]}")
    Flux<Group> findGroupsByDestinationWithinThresholds(
            double destLatMin, double destLatMax,
            double destLongMin, double destLongMax);

    @Query("{$and: [{'originAddress.latitude': {$gte: ?0, $lte: ?1 }},  {'originAddress.longitude': {$gte: ?2, $lte: ?3 }}, {'destinationAddress.latitude': {$gte: ?4, $lte: ?5 }}, {'destinationAddress.longitude': {$gte: ?6, $lte: ?7 }}] }")
    Flux<Group> findGroupsByOriginAndDestinationWithinThresholds(
            double originLatMin, double originLatMax,
            double originLongMin, double originLongMax,
            double destLatMin, double destLatMax,
            double destLongMin, double destLongMax,
            Pageable page);    
}

package com.mes.gotogether.services.domain;

import com.mes.gotogether.domains.Address;
import com.mes.gotogether.domains.Group;
import com.mes.gotogether.domains.User;
import com.mes.gotogether.repositories.domain.GroupRepository;
import com.mes.gotogether.services.externalconnections.GeoLocationService;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import static org.springframework.util.ObjectUtils.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class GroupServiceImpl implements GroupService{

    private final GroupRepository groupRepository;
    private final GeoLocationService geoLocationService;
    private final UserService userService;
    
    public GroupServiceImpl(GroupRepository groupRepository, GeoLocationService geoLocationService, UserService userservice) {
        this.groupRepository = groupRepository;
        this.geoLocationService = geoLocationService;
        this.userService = userservice;
    }

    // FIND METHODS
    @Override
    public Mono<Group> findById(ObjectId id) {

        if (Objects.isNull(id)) {
            return Mono.empty();
        }

        return groupRepository.findById(id);
    }

    @Override
    public Flux<Group> findGroupsByName(String groupName) {
       
        if (isEmpty(groupName)) {
            return Flux.empty();
        }
        
        System.out.println("Searching the group by name");
        return groupRepository.findGroupsByName(groupName);
    }

    @Override
    public Flux<Group> findGroupsByOriginAddress(Address originAddress) {

        if (Objects.isNull(originAddress)) {
            return Flux.empty();
        }

        return groupRepository.findGroupsByOriginAddress(originAddress);
    }

    @Override
    public Flux<Group> findGroupsByDestinationAddress(Address destinationAddress) {

        if (Objects.isNull(destinationAddress)) {
            return Flux.empty();
        }

        return groupRepository.findGroupsByDestinationAddress(destinationAddress);
    }

    @Override
    public Flux<Group> findGroupsByOriginAndDestinationAddress(Address originAddress, Address destinationAddress) {

        if (Objects.isNull(originAddress) || Objects.isNull(destinationAddress)) {
            return Flux.empty();
        }

        return groupRepository.findGroupsByOriginAddressAndDestinationAddress(originAddress, destinationAddress);
    }

    @Override
    public Flux<Group> findGroupsByOriginAndDestinationAddress(String originAddress, String destinationAddress, 
                                                                                                                     double originRadius, double destinationRadius, Pageable page) {
    	
          log.info("Sending latitude and longitude");
          Optional<Double[]> originLatitudeAndLongitude = geoLocationService.getAddressLongitudeAndLatitude(originAddress);
          Optional<Double[]> destinationLatitudeAndLongitude = geoLocationService.getAddressLongitudeAndLatitude(destinationAddress);
    	
          if (originLatitudeAndLongitude.isEmpty() || destinationLatitudeAndLongitude.isEmpty()) {
    	log.info("Sending flux empty due to empty origin or destination latitude");
    	return Flux.empty();
          }
    	
          Double[] originGeoLocations = originLatitudeAndLongitude.get();
          Double[] destinationGeoLocations = destinationLatitudeAndLongitude.get();
          log.info("Searching the groups within origin: " + originGeoLocations + " and destination: " + destinationGeoLocations);
		
          return this.findGroupsByOriginAndDestinationWithinRadius(originGeoLocations[0], originGeoLocations[1], originRadius,
                                                                                                             destinationGeoLocations[0], destinationGeoLocations[1], destinationRadius,
                                                                                                             page); 	
   }

   @Override
    public Flux<Group> findGroupsByOriginAndDestinationGeoLocationDetails(
            double originLatitude, double originLongitude,
            double destinationLatitude, double destinationLongitude) {

        if (Double.isNaN(originLatitude)
                || Double.isNaN(originLongitude)
                || Double.isNaN(destinationLatitude)
                || Double.isNaN(destinationLongitude)) {
            return Flux.empty();
        }

        return groupRepository.findGroupsByOriginAndDestinationGeoLocationDetails(originLatitude, originLongitude,
                                                                                                                                            destinationLatitude, destinationLongitude);
    }

    @Override
    public Flux<Group> findGroupsByOriginWithinThresholds(
            double originLatMin, double originLatMax,
            double originLongMin, double originLongMax) {

        if (Double.isNaN(originLatMin) || Double.isNaN(originLatMax)
                || Double.isNaN(originLongMin) || Double.isNaN(originLongMax)) {
                return Flux.empty();
            }

        return groupRepository.findGroupsByOriginWithinThresholds(originLatMin, originLatMax,
                                                                                                               originLongMin, originLongMax);
    }

    @Override
    public Flux<Group> findGroupsByDestinationWithinThresholds(
            double destLatMin, double destLatMax,
            double destLongMin, double destLongMax) {

        if (Double.isNaN(destLatMin) || Double.isNaN(destLatMax)
                || Double.isNaN(destLongMin) || Double.isNaN(destLongMax)) {
            return Flux.empty();
        }

        return groupRepository.findGroupsByDestinationWithinThresholds(destLatMin, destLatMax,
                                                                                                                       destLongMin, destLongMax);
    }

    @Override
    public Flux<Group> findGroupsByOriginAndDestinationWithinThresholds(
            double originLatMin, double originLatMax,
            double originLongMin, double originLongMax,
            double destLatMin, double destLatMax,
            double destLongMin, double destLongMax,
            Pageable page) {

        if (Double.isNaN(originLatMin) ||Double.isNaN(originLatMax)
                || Double.isNaN(originLongMin) || Double.isNaN(originLongMax)
                || Double.isNaN(destLatMin) || Double.isNaN(destLatMax)
                || Double.isNaN(destLongMin) || Double.isNaN(destLongMax)) {
            return Flux.empty();
        }

        log.info("Requested mongo query parameters\n" + 
        "Origin min latitude: " + originLatMin + "\n" +
        "Origin max latitude: " + originLatMax + "\n" +
        "Origin min longitude: " + originLongMin + "\n" +
        "Origin max longitude: " + originLongMax + "\n" +
        "Destination min latitude: " + destLatMin + "\n" +
        "Destination max latitude: " + destLatMax + "\n" +
        "Destination min longitude: " + destLongMin + "\n" +
        "Destination max longitude: " + destLongMax + "\n");
        
        return groupRepository.findGroupsByOriginAndDestinationWithinThresholds(originLatMin, originLatMax,
                                                                                                                                        originLongMin, originLongMax,
                                                                                                                                        destLatMin, destLatMax,
                                                                                                                                        destLongMin, destLongMax, 
                                                                                                                                        page);
    }

    @Override
    public Flux<Group> findGroupsByOriginWithinRadius(
            double originLat, double originLong, double originRadius){

        if (Double.isNaN(originLat) || Double.isNaN(originLong)
                || Double.isNaN(originRadius) || originRadius<0 ) {
            return Flux.empty();
        }

        GeoLocationThreshold originThresholds = new GeoLocationThreshold(originLat, originLong, originRadius);

        return this.findGroupsByOriginWithinThresholds(
                originThresholds.getLatMin(), originThresholds.getLatMax(),
                originThresholds.getLongMin(), originThresholds.getLongMax());
    }

    @Override
    public Flux<Group> findGroupsByDestinationWithinRadius(
            double destLat, double destLong, double destRadius){

        if (Double.isNaN(destLat) || Double.isNaN(destLong)
                || Double.isNaN(destRadius) || destRadius < 0) {
            return Flux.empty();
        }

        GeoLocationThreshold destThresholds = new GeoLocationThreshold(destLat, destLong, destRadius);

        return this.findGroupsByDestinationWithinThresholds(
                destThresholds.getLatMin(), destThresholds.getLatMax(),
                destThresholds.getLongMin(), destThresholds.getLongMax());
    }

    @Override
    public Flux<Group> findGroupsByOriginAndDestinationWithinRadius(
            double originLat, double originLong, double originRadius,
            double destLat, double destLong, double destRadius,
            Pageable page){

        if (Double.isNaN(originLat) || Double.isNaN(originLong)
                || Double.isNaN(destLat) ||Double.isNaN(destLong)
                ||Double.isNaN(originRadius) || Double.isNaN(destRadius)
                || originRadius<0 || destRadius < 0) {
            return Flux.empty();
        }

        GeoLocationThreshold originThresholds = new GeoLocationThreshold(originLat, originLong, originRadius);
        GeoLocationThreshold destThresholds = new GeoLocationThreshold(destLat, destLong, destRadius);
        
        return this.findGroupsByOriginAndDestinationWithinThresholds(
                originThresholds.getLatMin(), originThresholds.getLatMax(),
                originThresholds.getLongMin(), originThresholds.getLongMax(),
                destThresholds.getLatMin(), destThresholds.getLatMax(),
                destThresholds.getLongMin(), destThresholds.getLongMax(),
                page);
    }

    @Override
    public Flux<Group> findAll() {
        log.info("Calling all the groups");
        return groupRepository.findAll();
    }

    // SAVE OR UPDATE
    @Override
    public Mono<Group> saveOrUpdate(Group group) {
        
        if (!Objects.isNull(group)){
            if (group.getId() == null){
                return groupRepository.save(group);
            }else{
                return groupRepository.findById(group.getId())
                                                       .log()
                                                       .flatMap(groupInDb -> {
                                                                group.setId(groupInDb.getId());
                                                                log.debug("Updating the group:");
                                                                return groupRepository.save(group);
                                                       })
                                                       .switchIfEmpty(Mono.defer(() -> {
                                                                log.debug("Creating a new Group");
                                                                return groupRepository.save(group);
                                                       }));
            }
        }else{
            // TODO: CREATE ERROR HANDLERS
            log.debug("Could not save the GROUP");
            log.debug("A Null group data is entered. Do not process!");
            return Mono.empty();
        }
    }

    @Override
    public Mono<Set<User>> findMembers(ObjectId id) {
        
        return this.findById(id)
                         .flatMap(group -> Mono.just(group.getMembers()))
                         .switchIfEmpty(Mono.defer(() -> Mono.empty()));
    }
    
    @Override
    public Mono<Group> addUserToWaitingList(ObjectId id, String userId){
    
        return userService.findByUserId(userId)
                                      .flatMap(user->{
                                           return this.findById(id)
                                                 .flatMap(group->{
                                                     if(!group.getMembershipRequests().contains(user)){ 
                                                        group.getMembershipRequests().add(user);
                                                     }
                                                     return this.saveOrUpdate(group);
                                           }).switchIfEmpty(Mono.defer(() -> Mono.empty()));
                                       }).switchIfEmpty(Mono.defer(() -> Mono.empty()));
    }
    
    @Override
    public Mono<Group> deleteUserFromWaitingList(ObjectId id, String userId){
    
        return this.findById(id)
                         .flatMap(group -> {
                                     Set<User> membershipRequests = group.getMembershipRequests();
                                     Predicate<User> isUser = user -> user.getUserId().equals(userId);
                                     membershipRequests.removeIf(isUser);
                                     group.setInvites(membershipRequests);
                                     return this.saveOrUpdate(group);
                          }).switchIfEmpty(Mono.defer(() -> Mono.empty()));
    }

    @Override
    public Mono<Group> addUserToInvitesList(ObjectId id, String userId){
    
        return userService.findByUserId(userId)
                                      .flatMap(user->{
                                           return this.findById(id)
                                                 .flatMap(group->{
                                                     if (!group.getInvites().contains(user)){
                                                        group.getInvites().add(user);
                                                     }
                                                     return this.saveOrUpdate(group);
                                           }).switchIfEmpty(Mono.defer(() -> Mono.empty()));
                                       }).switchIfEmpty(Mono.defer(() -> Mono.empty()));
    }
    
    @Override
    public Mono<Group> deleteUserFromInvitesList(ObjectId id, String userId){
    
        return this.findById(id)
                         .flatMap(group -> {
                                     Set<User> invites = group.getInvites();
                                     Predicate<User> isUser = user -> user.getUserId().equals(userId);
                                     invites.removeIf(isUser);
                                     group.setInvites(invites);
                                     return this.saveOrUpdate(group);
                          }).switchIfEmpty(Mono.defer(() -> Mono.empty()));
    }

    @Override
    public Mono<Group> addMemberByUserId(ObjectId id, String userId) {
         
        return userService.findByUserId(userId)
                                      .flatMap(user->{
                                          return this.findById(id)
                                                .flatMap(group->{
                                                    Set<User> members = group.getMembers();
                                                    if (!members.contains(user)){
                                                       members.add(user);
                                                       group.setMembers(members);
                                                    }
                                                    return this.saveOrUpdate(group);
                                          }).switchIfEmpty(Mono.defer(() -> Mono.empty()));
                                      }).switchIfEmpty(Mono.defer(() -> Mono.empty()));
    }
    
    @Override
    public Mono<Group> deleteMemberByUserId(ObjectId id, String userId) {
              
        return this.findById(id)
                .flatMap(group -> {
                    Set<User> members = group.getMembers();
                    Predicate<User> isUser = user -> user.getUserId().equals(userId);                    
                    members.removeIf(isUser);
                    group.setMembers(members);
                    return this.saveOrUpdate(group);
                })
                .switchIfEmpty(Mono.defer(() -> Mono.empty()));
    }
    
    // SAVE Fake group
    @Override
    public Mono<Group> saveFakeGroup(Group group) {
        return groupRepository.save(group);
    }

    // DELETE
    @Override
    public Mono<Void> deleteById(ObjectId id) {

        if (isEmpty(id)) {
            return Mono.empty();
        }

        return groupRepository.deleteById(id);
    }

    @Override
    public Mono<Void> deleteAll() {

        return groupRepository.deleteAll();
    }

}

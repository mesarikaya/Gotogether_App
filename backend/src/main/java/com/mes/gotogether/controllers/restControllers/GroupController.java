package com.mes.gotogether.controllers.restControllers;

import com.mes.gotogether.domains.requests.GroupUser;
import com.mes.gotogether.domains.responses.GroupSearchResponse;
import com.mes.gotogether.security.jwt.JWTUtil;
import com.mes.gotogether.security.service.SecurityUserLibraryUserDetailsService;
import com.mes.gotogether.services.domain.GroupService;
import com.mes.gotogether.services.domain.UserService;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class GroupController {

    private final UserService userService;
    private final GroupService groupService;
    private final JWTUtil jwtUtil;
    private final SecurityUserLibraryUserDetailsService securityUserService;
    private final PasswordEncoder passwordEncoder;

    public GroupController(UserService userService, GroupService groupService,
                                           JWTUtil jwtUtil, PasswordEncoder passwordEncoder,
                                           SecurityUserLibraryUserDetailsService securityUserService) {
        this.userService = userService;
        this.groupService = groupService;
        this.jwtUtil = jwtUtil;
        this.securityUserService = securityUserService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/groups")
    @ResponseStatus(HttpStatus.OK)
    public Flux<GroupSearchResponse> getGroupsByOriginAndDestinationWithinRadius(@RequestParam("origin") String origin,  
                                                                                                                                                        @RequestParam("destination") String destination,
    							            @RequestParam("originRange") double originRadius,
                                                                                                                                                        @RequestParam("destinationRange") double destRadius,
                                                                                                                                                        @RequestParam("page") int page, 
                                                                                                                                                        @RequestParam("size") int size) {
        
                    Pageable pageable = PageRequest.of(page, size, Sort.Direction.ASC, "id");        
    	return groupService.findGroupsByOriginAndDestinationAddress(origin, destination, 
                                                                                                                               originRadius, destRadius, 
                                                                                                                               pageable)
                                                     .log("Source GET GEROUPS")
                                                     .checkpoint("In get groups")
                                                     .map(group-> new GroupSearchResponse(group));
    }
    
    @GetMapping("/groups/id")
    @ResponseStatus(HttpStatus.OK)
    public Mono<GroupSearchResponse> getGroupById(@RequestParam("groupId") String groupId){
        
        ObjectId id= new ObjectId(groupId);
        return groupService.findById(id)
                                        .map(group -> new GroupSearchResponse(group));
    }
    
    @DeleteMapping("/groups")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> deleteGroup(@RequestParam("groupId") String groupId) {   	
         
         ObjectId groupIx= new ObjectId(groupId);
         return groupService.deleteById(groupIx);
    }
    
    @PutMapping("/groups/members")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @ResponseStatus(HttpStatus.OK)
    public Mono<GroupSearchResponse> addGroupMember(@RequestBody GroupUser groupUser)
    {   	
         ObjectId groupId= new ObjectId(groupUser.getGroupId());
         String userId = groupUser.getUserId();
         
         return groupService.deleteUserFromWaitingList(groupId, userId)
                                          .flatMap(group -> {
                                              return groupService.addMemberByUserId(group.getId(), userId)
                                                                                .flatMap(groupLvl1 -> groupService.deleteUserFromInvitesList(groupLvl1.getId(), userId)
                                                                                                                                            .map(item->new GroupSearchResponse(item)))
                                                                               .switchIfEmpty(Mono.defer(() -> Mono.empty()));
                                          }).switchIfEmpty(Mono.defer(() -> Mono.empty()));
    }
    
    @DeleteMapping("/groups/members")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @ResponseStatus(HttpStatus.OK)
    public Mono<GroupSearchResponse> deleteGroupMember(@RequestParam("groupId") String groupId,  @RequestParam("userId") String userId)
    {   	
         ObjectId groupIx= new ObjectId(groupId); 
         return groupService.deleteUserFromWaitingList(groupIx, userId)
                                          .flatMap(group -> {
                                              return groupService.deleteMemberByUserId(group.getId(), userId)
                                                                                .flatMap(groupLvl1 -> groupService.deleteUserFromInvitesList(groupLvl1.getId(), userId)
                                                                                                                                            .map(item->new GroupSearchResponse(item)))
                                                                               .switchIfEmpty(Mono.defer(() -> Mono.empty()));
                                          }).switchIfEmpty(Mono.defer(() -> Mono.empty()));
    }
    
    @PutMapping("/groups/waitingList")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @ResponseStatus(HttpStatus.OK)
    public Mono<GroupSearchResponse> addToWaitingList(@RequestBody GroupUser groupUser)
    {   	 
          return groupService.addUserToWaitingList(new ObjectId(groupUser.getGroupId()), groupUser.getUserId())
                                           .map(group-> new GroupSearchResponse(group));
    }
    
    @DeleteMapping("/groups/waitingList")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @ResponseStatus(HttpStatus.OK)
    public Mono<GroupSearchResponse> deleteFromWaitingList(@RequestParam("groupId") String groupId,  
                                                                                                               @RequestParam("userId") String userId, 
                                                                                                               @RequestParam("isMember") String isMember) {  
          ObjectId groupIx= new ObjectId(groupId);
          if(Boolean.parseBoolean(isMember)) {
              return groupService.deleteUserFromWaitingList(groupIx, userId)
                                               .flatMap(group -> {
                                                   return groupService.addMemberByUserId(group.getId(), userId)
                                                                                    .map(groupItem->new GroupSearchResponse(groupItem))
                                                                                    .switchIfEmpty(Mono.defer(() -> Mono.empty()));
                                               }).switchIfEmpty(Mono.defer(() -> Mono.empty()));
          }
          
          return groupService.deleteUserFromWaitingList(new ObjectId(groupId), userId)
                                           .map(group-> new GroupSearchResponse(group));
    }
    
    @DeleteMapping("/groups/invitationsList")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @ResponseStatus(HttpStatus.OK)
    public Mono<GroupSearchResponse> deleteFromInviteList(@RequestParam("groupId") String groupId,  
                                                                                                           @RequestParam("userId") String userId)
    {   	
          return groupService.deleteUserFromInvitesList(new ObjectId(groupId), userId)
                                           .map(group-> new GroupSearchResponse(group));
    }
    
    @PutMapping("/groups/invitationsList")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @ResponseStatus(HttpStatus.OK)
    public Mono<GroupSearchResponse> addToInvitesList(@RequestBody GroupUser groupUser)
    {   	
          return groupService.addUserToInvitesList(new ObjectId(groupUser.getGroupId()), groupUser.getUserId())
                                           .map(group-> new GroupSearchResponse(group));
    }
}

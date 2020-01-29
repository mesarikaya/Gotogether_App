package com.mes.gotogether.controllers.restControllers;

import com.mes.gotogether.domains.Address;
import com.mes.gotogether.domains.Group;
import com.mes.gotogether.domains.User;
import com.mes.gotogether.domains.requests.GroupForm;
import com.mes.gotogether.domains.responses.GroupSearchResponse;
import com.mes.gotogether.domains.responses.UserSearchResponse;
import com.mes.gotogether.security.jwt.JWTUtil;
import com.mes.gotogether.security.service.SecurityUserLibraryUserDetailsService;
import com.mes.gotogether.services.domain.AddressService;
import com.mes.gotogether.services.domain.GroupService;
import com.mes.gotogether.services.domain.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
@PreAuthorize("hasAnyRole('ADMIN', 'USER', 'GUEST')")
public class UserController {

    private final UserService userService;
    private final GroupService groupService;
    private final JWTUtil jwtUtil;
    private final SecurityUserLibraryUserDetailsService securityUserService;
    private final PasswordEncoder passwordEncoder;
    private final AddressService addressService;

    public UserController(UserService userService, 
                                          GroupService groupService,
                                          JWTUtil jwtUtil, 
                                          PasswordEncoder passwordEncoder,
                                          SecurityUserLibraryUserDetailsService securityUserService,
                                          AddressService addressService) {
        this.userService = userService;
        this.groupService = groupService;
        this.jwtUtil = jwtUtil;
        this.securityUserService = securityUserService;
        this.passwordEncoder = passwordEncoder;
        this.addressService = addressService;
    }

    @GetMapping("/allUsers")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public Mono<List<User>> getAllUsers(){
        System.out.println();
        return userService.findAllUsers();
    }
    
    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Flux<List<UserSearchResponse>> getUsersByOriginAndDestinationWithinRadius(@RequestParam("origin") String origin,  
                                                                                                                                                             @RequestParam("destination") String destination,
    							                 @RequestParam("originRange") double originRadius, 
                                                                                                                                                             @RequestParam("destinationRange") double destRadius,
                                                                                                                                                             @RequestParam("page") int page, @RequestParam("size") int size) {
        
                    Pageable pageable = PageRequest.of(page, size, Sort.Direction.ASC, "id");  
    	return groupService.findGroupsByOriginAndDestinationAddress(origin, destination, 
                                                                                                                               originRadius, destRadius, 
                                                                                                                               pageable)
                                                     .map(group -> {
                                                         List<UserSearchResponse> response= new ArrayList<>();
                                                         group.getMembers().stream().forEach(user -> response.add(new UserSearchResponse(user)));
                                                        return response;
                                                     });
    }
    
    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @ResponseStatus(HttpStatus.OK)
    public Flux<GroupSearchResponse> getUserMembershipInfo(@RequestParam("userId") String userId){
        
        Predicate<User> isUser = user -> user.getUserId().equals(userId);
        Predicate<Group> isMemberOrInvited = (group) -> group.getMembers().stream().anyMatch(isUser) || group.getInvites().stream().anyMatch(isUser);
        return groupService.findAll()
                                         .filter(isMemberOrInvited)
                                         .map(group -> new GroupSearchResponse(group));
    }
    
    @PostMapping("/user/group")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Mono<GroupSearchResponse> createGroupForUser(@RequestBody GroupForm groupForm){
        String userId = groupForm.getUserId();
        Address originAddress = createAddress(groupForm.getOriginStreetName(), 
                                                                          groupForm.getOriginHouseNumber(),
                                                                          groupForm.getOriginCity(),
                                                                          groupForm.getOriginCountry(),
                                                                          groupForm.getOriginState(),
                                                                          groupForm.getOriginCountry());
        Address destinationAddress = createAddress(groupForm.getDestinationStreetName(), 
                                                                                   groupForm.getDestinationHouseNumber(),
                                                                                   groupForm.getDestinationCity(),
                                                                                   groupForm.getDestinationCountry(),
                                                                                   groupForm.getDestinationState(),
                                                                                   groupForm.getDestinationCountry());
       
        addressService.saveOrUpdateAddress(destinationAddress);
        Group newGroup = new Group();
        newGroup.setName(groupForm.getGroupName());            
        newGroup.setOriginSearchRadius(groupForm.getOriginSearchRadius());
        newGroup.setDestinationSearchRadius(groupForm.getDestinationSearchRadius());
        newGroup.setId(new ObjectId());
        log.info("Created the group");
        return userService.findByUserId(userId)
                                      .map( (user) -> {
                                          newGroup.getOwners().add(user);
                                          newGroup.getMembers().add(user);
                                          return newGroup;
                                      })
                                      .flatMap( (group) -> {
                                         return addressService.saveOrUpdateAddress(originAddress)
                                                                              .flatMap( (address) -> {
                                                                                  group.setOriginAddress(address);
                                                                                  return groupService.saveOrUpdate(group);
                                                                              });
                                      } )
                                       .flatMap( (group) -> {
                                         return addressService.saveOrUpdateAddress(destinationAddress)
                                                                              .flatMap( (address) -> {
                                                                                  group.setDestinationAddress(address);
                                                                                  return groupService.saveOrUpdate(group);
                                                                              });
                                      } )
                                      .map(group -> new GroupSearchResponse(group));
    }
    
    private Address createAddress(String streetName, String houseNumber, String city, String state, String country, String zipcode){
          
          Address address = new Address();
          address.setStreetName(streetName);
          address.setHouseNumber(houseNumber);
          address.setCity(city);
          address.setState(state);
          address.setCountry(country);
          address.setZipcode(zipcode);
          address.setId(new ObjectId());
          return address;
    }
}

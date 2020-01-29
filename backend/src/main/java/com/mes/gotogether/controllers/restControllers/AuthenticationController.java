package com.mes.gotogether.controllers.restControllers;

import com.mes.gotogether.domains.Address;
import com.mes.gotogether.domains.Role;
import com.mes.gotogether.domains.User;
import com.mes.gotogether.domains.requests.UserDTO;
import com.mes.gotogether.domains.responses.HttpResponse;
import com.mes.gotogether.mail.EmailService;
import com.mes.gotogether.security.domain.AuthRequest;
import com.mes.gotogether.security.domain.AuthResponse;
import com.mes.gotogether.security.domain.SecurityUserLibrary;
import com.mes.gotogether.security.domain.SendVerificationForm;
import com.mes.gotogether.security.jwt.JWTUtil;
import com.mes.gotogether.security.service.SecurityUserLibraryUserDetailsService;
import com.mes.gotogether.services.domain.AddressService;
import com.mes.gotogether.services.domain.UserService;
import java.time.LocalDateTime;
import java.util.Arrays;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping(path = "/api/auth", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class AuthenticationController {

     private final JWTUtil jwtUtil;
     private final PasswordEncoder passwordEncoder;
     private final SecurityUserLibraryUserDetailsService securityUserService;
     private final UserService userService;
     private final AddressService addressService;
     private final EmailService emailService;
     @Autowired
     private Environment env;

     @Autowired
     public AuthenticationController(JWTUtil jwtUtil,
         PasswordEncoder passwordEncoder,
         SecurityUserLibraryUserDetailsService securityUserService,
         UserService userService,
         AddressService addressService,
         EmailService emailService) {
          this.jwtUtil = jwtUtil;
          this.passwordEncoder = passwordEncoder;
          this.securityUserService = securityUserService;
          this.userService = userService;
          this.addressService = addressService;
          this.emailService = emailService;
     }

     @PostMapping("/login")
     public Mono<ResponseEntity<?>> login(@RequestBody AuthRequest ar, ServerHttpResponse serverHttpResponse) {

          return securityUserService.findByUserId(ar.getUserName().split("@")[0])
                                                     .map( (userDetails) -> {
                                                          if (passwordEncoder.matches(ar.getPassword(), userDetails.getPassword())) {
                                                               log.info("Authorized! YEAH!!!!");
                                                               String token = jwtUtil.generateToken((SecurityUserLibrary) userDetails);
                                                               ResponseCookie cookie = ResponseCookie.from("System", token)
                                                                   .sameSite("Strict")
                                                                   .path("/")
                                                                   .maxAge(3000)
                                                                   .httpOnly(true)
                                                                   .build();
                                                               serverHttpResponse.addCookie(cookie);

                                                               log.info("Server response: " + serverHttpResponse.getCookies().toSingleValueMap().values());
                                                               return ResponseEntity.ok()
                                                                                                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                                                                                                    .body(new AuthResponse(token, userDetails.getUsername()));
                                                         } else {
                                                              log.info("Returning unauthorized");
                                                              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                                                         }
                                                     }).defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
     }
     
     
     @GetMapping("/logout")
     public Mono<HttpResponse> logout(@RequestParam("userName") String userName, 
                                                                     @RequestParam("token") String token,
                                                                     ServerHttpResponse serverHttpResponse){
         log.info("Trying to logout!");
         log.info("delete the existing cookie");
         ResponseCookie cookie = ResponseCookie.from("System", token)
                                                                   .sameSite("Strict")
                                                                   .path("/")
                                                                   .maxAge(0)
                                                                   .httpOnly(true)
                                                                   .build();
                                                               serverHttpResponse.addCookie(cookie);
          log.info("Cookie is deleted");
          return securityUserService.findByUserId(userName.split("@")[0])
                                                     .flatMap( (userDetails) -> {
                                                         log.info("jwt trials token: " + token);
                                                          if ( userDetails.getUsername().equals(jwtUtil.getUsernameFromToken(token))){
                                                              log.debug("Logging out user: " + jwtUtil.getUsernameFromToken(token));
                                                              return Mono.just(new HttpResponse(HttpStatus.OK, 
                                                                                                                           HttpResponse.ResponseType.SUCCESS,
                                                                                                                           "Logout is successful!"));
                                                         } else {
                                                              log.info("Returning unauthorized");
                                                              return Mono.just(new HttpResponse(HttpStatus.BAD_REQUEST, 
                                                                                                                           HttpResponse.ResponseType.FAILURE,
                                                                                                                           "Token is invalid!"));
                                                         }
                                                     }).defaultIfEmpty(new HttpResponse(HttpStatus.OK, 
                                                                                                                           HttpResponse.ResponseType.FAILURE,
                                                                                                                           "Logout is not successful!"))
                                                    .onErrorResume(ex -> Mono.just(new HttpResponse(HttpStatus.BAD_REQUEST, 
                                                                                                                                  HttpResponse.ResponseType.FAILURE,
                                                                                                                                  ex.getMessage())));
     }
     
     @PostMapping("/register")
     public Mono<HttpResponse> registerUser(@RequestBody UserDTO userDTO, ServerHttpRequest serverHttpRequest) {

          User user = new User();
          user.setEmail(userDTO.getEmail());
          user.setFirstName(userDTO.getFirstName());
          user.setMiddleName(userDTO.getMiddleName());
          user.setLastName(userDTO.getLastName());
          user.setOauthId("");
          user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
          user.setRoles(Arrays.asList(new Role[]{Role.USER, Role.GUEST}));
          Address address = new Address();
          address.setStreetName(userDTO.getStreetName());
          address.setHouseNumber(userDTO.getHouseNumber());
          address.setCity(userDTO.getCity());
          address.setCountry(userDTO.getCountry());
          address.setZipcode(userDTO.getZipcode());
          user.setAddress(address);
          String origin = serverHttpRequest.getHeaders().getOrigin();
          addressService.saveOrUpdateAddress(address); // TODO remove this and integrate with registration
          
          return userService.findByUserId(user.getUserId()).hasElement()
                                        .flatMap( (item) -> {
                                            if (item) {
                                                 return Mono.just(new HttpResponse(HttpStatus.ALREADY_REPORTED, 
                                                                                                               HttpResponse.ResponseType.FAILURE,
                                                                                                               "Already registered! If needed, request "
                                                                                                                   + "account verification!"));
                                            } else {
                                                return userService.saveOrUpdateUser(user)
                                                                              .flatMap( u -> sendEmail(u.getUserId(), origin));
                                            }                                         
                                        }).onErrorResume(ex -> Mono.just(new HttpResponse(HttpStatus.BAD_REQUEST, 
                                                                                                                                  HttpResponse.ResponseType.FAILURE,
                                                                                                                                  ex.getMessage())));
                                        
                                              
                                                             
     }
     
     @PostMapping("/verify")
     public Mono<HttpResponse> sendUserVerification(@RequestBody SendVerificationForm sendVerificationForm, 
                                                                                                                         ServerHttpRequest serverHttpRequest) {
          
          String origin = serverHttpRequest.getHeaders().getOrigin();
          String userId = sendVerificationForm.getUserName();
          return userService.findByUserId(userId)
                                        .flatMap(u -> {
                                            return userService.renewVerificationDetails(userId);
                                        })
                                        .hasElement()
                                        .flatMap( (item) -> {
                                            if (!item){
                                                return Mono.just(new HttpResponse(HttpStatus.BAD_REQUEST, 
                                                                                                              HttpResponse.ResponseType.FAILURE,
                                                                                                              "No such user exists! Please register!"));
                                            }
                                            return sendEmail(userId, origin);
                                        }).onErrorResume(ex -> Mono.just(new HttpResponse(HttpStatus.BAD_REQUEST, 
                                                                                                                                  HttpResponse.ResponseType.FAILURE,
                                                                                                                                  ex.getMessage())));         
     }
     
     @GetMapping("/verify/validate")
     public Mono<HttpResponse> validateVerificationToken(@RequestParam("userID") String userId, 
                                                                                                       @RequestParam("verificationToken") String verificationToken){
         log.debug("Request: Validating user verification token user" + userId + " and verification token: " + verificationToken);
         return userService.findByUserId(userId)
                                       .flatMap( u -> {
                                           LocalDateTime currentTime = LocalDateTime.now();
                                           if (u.getVerificationToken().equals(verificationToken) & currentTime.compareTo(u.getVerificationExpiresAt())<=0){
                                               u.setVerified(true);
                                               return userService.saveOrUpdateUser(u)
                                                                             .map(t ->new HttpResponse(HttpStatus.OK, 
                                                                                                              HttpResponse.ResponseType.SUCCESS,
                                                                                                              "Verification is complete"));
                                           } else {
                                               return Mono.just(new HttpResponse(HttpStatus.BAD_REQUEST, 
                                                                                                              HttpResponse.ResponseType.FAILURE,
                                                                                                              "Verification is NOT completed!"));
                                           
                                           }
                                       }).defaultIfEmpty(new HttpResponse(HttpStatus.BAD_REQUEST, 
                                                                                                              HttpResponse.ResponseType.FAILURE,
                                                                                                              "Verification is NOT completed!"))
                                       .onErrorResume(ex -> {
                                           log.info("Inside on error resume");
                                           return Mono.just(new HttpResponse(HttpStatus.BAD_REQUEST, 
                                                                                                                                  HttpResponse.ResponseType.FAILURE,
                                                                                                                                  ex.getMessage())); });         
     }
        
    private Mono<HttpResponse> sendEmail(@NotNull String userId, String origin){
        
        log.info("Attempting to send the email");
        return userService.findByUserId(userId)
                                     .flatMap( (u) -> userService.saveOrUpdateUser(u)) 
                                     .flatMap(u -> userService.findByUserId(u.getUserId()))
                                     .flatMap((savedUser) -> {
                                         log.info("Sending email with user: \n" + savedUser);
                                         return emailService.sendEmail(savedUser, origin);
                                     });
     }
}

/*                                                    
                                                   
                                                   ResponseEntity.ok()
                                                                                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                                                                                        .body(new HttpResponse(HttpStatus.BAD_REQUEST,
                                                                                                                                  HttpResponse.ResponseType.SUCCESS,
                                                                                                                                  ex.getMessage()));*/
//.build();
//serverHttpResponse.getHeaders().add(HttpHeaders.SET_COOKIE,"test: " + cookie.toString());
//System.out.println("Server Http response: " + serverHttpResponse.getCookies().toString());
//                        .header(HttpHeaders.AUTHORIZATION,
//                                String.join(" ","Bearer", token))
/*return ResponseEntity
                        .ok()
                        //.header(HttpHeaders.SET_COOKIE, serverHttpResponse.getCookies().toSingleValueMap().values().toString())
                        .header(HttpHeaders.SET_COOKIE,
                                cookie.getName() + "=" + cookie.getValue() + ";"
                                        + "httpOnly="+ cookie.isHttpOnly() + ";"
                                        + "expires=" + cookie.getMaxAge() + ";"
                                        + "sameSite=" + cookie.getSameSite())
                        //.contentType(MediaType.APPLICATION_JSON_UTF8)
                        .body(new AuthResponse(token, userDetails.getUsername()));*/
// ResponseEntity.ok(new AuthResponse(jwtUtil.generateToken((SecurityUserLibrary) userDetails)));

/*
authRequestMono.flatMap(
            ar ->{
                System.out.println("Authorization request is: " + ar );
                System.out.println("Authorization: " + ar.getUsername() +
                        "password: " + ar.getPassword() +
                        "encoded: " +passwordEncoder.encode(ar.getPassword()));
                return securityUserService.findByUserId(ar.getUsername()).map((userDetails) -> {
                    System.out.println("userdetails password: " + userDetails.getPassword());
                    if (passwordEncoder.matches(ar.getPassword(), userDetails.getPassword())) {
                        System.out.println("Authorized! YEAH!!!!");

                        String token = jwtUtil.generateToken((SecurityUserLibrary) userDetails);

                        ResponseCookie cookie = ResponseCookie.from("JWTCookie", token)
                                .maxAge(3600)
                                .build();

                        serverHttpResponse.addCookie(cookie);

                        return ResponseEntity.ok()
                                .header(HttpHeaders.AUTHORIZATION,
                                        String.join(" ","Bearer", token))
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                .body(new AuthResponse(token, userDetails.getUsername()));

                        // ResponseEntity.ok(new AuthResponse(jwtUtil.generateToken((SecurityUserLibrary) userDetails)));
                    } else {
                        System.out.println("Returning unauthorized");
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                    }
                }).defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
            }).onErrorReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());


 */

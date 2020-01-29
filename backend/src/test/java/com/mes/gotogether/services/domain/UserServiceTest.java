package com.mes.gotogether.services.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.bson.codecs.ObjectIdGenerator;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.mes.gotogether.domains.Address;
import com.mes.gotogether.domains.Role;
import com.mes.gotogether.domains.User;
import com.mes.gotogether.repositories.domain.UserRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private User user;
    private UserServiceImpl userServiceImpl;
    private User existingUser;
    private User newUser;
    private User retrievedUser1;
    private User unchangedExistingUser;

    @BeforeEach
    public void setUp() {

        System.out.println("@BeforeEach is called!");
        MockitoAnnotations.initMocks(this);
        userServiceImpl = new UserServiceImpl(userRepository);

        // Create Existing Account
        existingUser = new User();
        existingUser.setEmail("abc@gmail.com");
        existingUser.setFirstName("First");
        existingUser.setMiddleName("Middle");
        existingUser.setLastName("Last");
        existingUser.setActive(true);
        existingUser.setMobileNumber("021234234");
        existingUser.setVerified(false);
        existingUser.setPermalink("abcgmailcom");
        existingUser.setId((ObjectId) new ObjectIdGenerator().generate());
        existingUser.setRoles(Arrays.asList(new Role[] {Role.ADMIN, Role.USER, Role.GUEST}));
        Address address1 = new Address();
        address1.setStreetName("asda");
        address1.setHouseNumber("asd");
        address1.setCity("asdasd");
        address1.setCountry("asdasd");
        address1.setZipcode("sasd123");
        existingUser.setAddress(address1);
        existingUser.setId((ObjectId) new ObjectIdGenerator().generate());
        unchangedExistingUser = new User(existingUser);

        // New User
        // Create Existing Account
        newUser = new User();
        newUser.setFirstName("NewFirst");
        newUser.setMiddleName("NewMiddle");
        newUser.setLastName("NewLast");
        newUser.setActive(true);
        newUser.setMobileNumber("0123123");
        newUser.setVerified(false);
        newUser.setOauthId("123123123");
        newUser.setId((ObjectId) new ObjectIdGenerator().generate());
        newUser.setRoles(Arrays.asList(new Role[] { Role.USER, Role.GUEST}));
        Address address2 = new Address();
        address2.setStreetName("Address2");
        address2.setHouseNumber("asda");
        address2.setCity("asdsad");
        address2.setCountry("asd");
        address2.setZipcode("asdasd");
        newUser.setAddress(address2);

        // 1. Save the existing user
        when(userRepository.findByUserId(existingUser.getUserId())).thenReturn(Mono.just(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(existingUser));
        retrievedUser1 = userServiceImpl.saveOrUpdateUser(existingUser).log().flux().next().block();
    }

    @Test
    public void testMockCreation(){

        assertNotNull(existingUser);
        assertNotNull(newUser);
        assertNotNull(user);
        assertNotNull(userRepository);
        assertNotNull(userServiceImpl);
    }

    @Test
    public void saveUser(){

        when(userRepository.findByUserId(anyString())).thenReturn(Mono.empty());
        when(userRepository.save(newUser)).thenReturn(Mono.just(newUser));
        User retrievedUser = userServiceImpl.saveOrUpdateUser(newUser).log().flux().next().block();
        assertEquals(retrievedUser.getUserId(), newUser.getUserId());
    }

    @Test
    public void saveNullUser(){

        Mono<User> retrievedUser = userServiceImpl.saveOrUpdateUser(null);
        assertEquals(Mono.empty(),retrievedUser);
    }

    @Test
    public void createNullUser(){

        Mono<User> retrievedUser = userServiceImpl.createUser(null);
        assertEquals(Mono.empty(),retrievedUser);
    }

    @Test
    public void updateUser(){

        // Change existing user name
        existingUser.setFirstName("Ergin");
        existingUser.setEmail("mesaya@gmail.com");
        when(userRepository.findByUserId(existingUser.getUserId())).thenReturn(Mono.just(existingUser));
        when(userRepository.save(existingUser)).thenReturn(Mono.just(existingUser));
        User retrievedUser2 = userServiceImpl.saveOrUpdateUser(existingUser).log().flux().next().block();

        // Check if they are the same object
        assertEquals(retrievedUser1.getId(), unchangedExistingUser.getId());
    }

    @Test
    public void findUserById(){

        // Search the user by _id
        when(userRepository.findById(existingUser.getId())).thenReturn(Mono.just(existingUser));
        User retrievedUser = userServiceImpl.findUserById(existingUser.getId()).log().flux().next().block();
        assertEquals(existingUser.getId(), retrievedUser.getId());
    }

    @Test
    public void findUserByNullId(){

        // Search the user by Null User id
        Mono<User> retrievedUser = userServiceImpl.findUserById(null);
        assertEquals(retrievedUser, Mono.empty());
    }

    @Test
    public void findUserByUserId(){

        // Search the user by _id
        when(userRepository.findByUserId(existingUser.getUserId())).thenReturn(Mono.just(existingUser));
        User retrievedUser = userServiceImpl.findByUserId(existingUser.getUserId()).log().flux().next().block();
        assertEquals(existingUser.getId(), retrievedUser.getId());
    }

    @Test
    public void findUserByNullUserId(){

        // Search the user by Null User id
        Mono<User> retrievedUser = userServiceImpl.findByUserId(null);
        assertEquals(retrievedUser, Mono.empty());
    }

    @Test
    public void findAllUsers(){

        // Search all existing users
        when(userRepository.findAll()).thenReturn(Flux.just(existingUser));
        List<User> retrievedUsers = userServiceImpl.findAllUsers().log().flux().next().block();
        assertNotNull(retrievedUsers);
        assertEquals(existingUser, retrievedUsers.get(0));
    }

    @Test
    public void deleteUserById(){

        // Delete user by _id
        when(userRepository.deleteById(any(ObjectId.class))).thenReturn(Mono.empty());
        userServiceImpl.deleteUserById(existingUser.getId()).log().flux().next().block();
        verify(userRepository, times(1)).deleteById(existingUser.getId());
    }

    @Test
    public void deleteUserByNullId(){

        // Delete user by _id
        when(userRepository.deleteById(nullable(ObjectId.class))).thenReturn(Mono.empty());
        userServiceImpl.deleteUserById(null).log().flux().next().block();
        verify(userRepository, times(0)).deleteById(nullable(ObjectId.class));
    }

    @Test
    public void deleteUserByUserId(){

        // Delete by userId
        when(userRepository.deleteByUserId(anyString())).thenReturn(Mono.empty());
        userServiceImpl.deleteByUserId(existingUser.getUserId()).log().flux().next().block();
        verify(userRepository, times(1))
                .deleteByUserId((existingUser.getUserId()));
    }

    @Test
    public void deleteUserByNullUserId(){

        // Delete the user by Null User id
        Mono<Void> retrievedUser = userServiceImpl.deleteByUserId(null);
        assertEquals(retrievedUser, Mono.empty());

        // DeleteById method should have not been called
        when(userRepository.deleteByUserId(null)).thenReturn(Mono.empty());
        userServiceImpl.deleteByUserId(null).log().flux().next().block();
        verify(userRepository, times(0))
                .deleteByUserId(existingUser.getUserId());
    }

    @Test
    public void deleteAllUsers(){

        // Verify if all users are deleted
        when(userRepository.deleteAll()).thenReturn(Mono.empty());
        userServiceImpl.deleteAll();
        verify(userRepository, times(1)).deleteAll();

        // Check if the existing user still exists
        when(userRepository.findById(any(ObjectId.class))).thenReturn(Mono.empty());
        Mono<User> retrievedUser = userServiceImpl.findUserById(existingUser.getId());
        assertEquals(Mono.empty(),retrievedUser);
    }
}

package com.mes.gotogether.services.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.HashSet;
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
import org.springframework.web.client.RestTemplate;

import com.mes.gotogether.domains.Address;
import com.mes.gotogether.domains.Group;
import com.mes.gotogether.domains.NomatimOpenStreetMapQuery;
import com.mes.gotogether.domains.Role;
import com.mes.gotogether.domains.User;
import com.mes.gotogether.repositories.domain.GroupRepository;
import com.mes.gotogether.services.externalconnections.GeoLocationService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;
    @Mock
    private GeoLocationService geoLocationService;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private Group group;
    @Mock
    private NomatimOpenStreetMapQuery nomatimOpenStreetMapQueries;
    private GroupServiceImpl groupServiceImpl;
    private Group existingGroup;
    private Group unchangedExistingGroup;
    private Group newGroup;

    @BeforeEach
    public void setUp() {

        System.out.println("@BeforeEach is called!");
        MockitoAnnotations.initMocks(this);
        groupServiceImpl = new GroupServiceImpl(groupRepository, geoLocationService);

        // Create User 1
        User user1 = new User();
        user1.setEmail("abc@gmail.com");
        user1.setFirstName("First");
        user1.setMiddleName("Middle");
        user1.setLastName("Last");
        user1.setActive(true);
        user1.setMobileNumber("021234234");
        user1.setVerified(false);
        user1.setPermalink("abcgmailcom");
        user1.setId((ObjectId) new ObjectIdGenerator().generate());
        user1.setRoles(Arrays.asList(new Role[]{Role.ADMIN, Role.USER, Role.GUEST}));
        Address address1 = new Address();
        address1.setStreetName("asda");
        address1.setHouseNumber("asd");
        address1.setCity("asdasd");
        address1.setCountry("asdasd");
        address1.setZipcode("sasd123");
        address1.setLatitude(10);
        address1.setLongitude(14);
        address1.setId((ObjectId) new ObjectIdGenerator().generate());
        user1.setAddress(address1);
        user1.setId((ObjectId) new ObjectIdGenerator().generate());

        // Create User 2
        User user2 = new User();
        user2.setEmail("abc@gmail.com");
        user2.setFirstName("First");
        user2.setMiddleName("Middle");
        user2.setLastName("Last");
        user2.setActive(true);
        user2.setMobileNumber("021234234");
        user2.setVerified(false);
        user2.setPermalink("abcgmailcom");
        user2.setId((ObjectId) new ObjectIdGenerator().generate());
        user2.setRoles(Arrays.asList(new Role[]{Role.ADMIN, Role.USER, Role.GUEST}));
        Address address2 = new Address();
        address2.setStreetName("asda");
        address2.setHouseNumber("asd");
        address2.setCity("asdasd");
        address2.setCountry("asdasd");
        address2.setZipcode("sasd123");
        address2.setLatitude(11);
        address2.setLongitude(12);
        address2.setId((ObjectId) new ObjectIdGenerator().generate());
        user2.setAddress(address2);
        user2.setId((ObjectId) new ObjectIdGenerator().generate());

        // Create Existing Address
        existingGroup = new Group();
        existingGroup.setActive(true);
        existingGroup.setName("Group Test 1");
        HashSet<User> members = new HashSet<>();
        members.addAll(Arrays.asList(user1, user2));
        existingGroup.setMembers(members);
        existingGroup.setOriginAddress(address1);
        existingGroup.setOriginSearchRadius(3);
        existingGroup.setDestinationAddress(address2);
        existingGroup.setDestinationSearchRadius(3);
        HashSet<User> owners = new HashSet<>();
        owners.addAll(Arrays.asList(user1));
        existingGroup.setOwners(owners);
        existingGroup.setId((ObjectId) new ObjectIdGenerator().generate());

        unchangedExistingGroup = new Group(existingGroup);

        // New Group
        User user3 = new User();
        user3.setEmail("abcde@gmail.com");
        user3.setFirstName("Firsts");
        user3.setMiddleName("Middles");
        user3.setLastName("Lasts");
        user3.setActive(true);
        user3.setMobileNumber("0a21234234");
        user3.setVerified(false);
        user3.setPermalink("abcdegmailcom");
        user3.setId((ObjectId) new ObjectIdGenerator().generate());
        user3.setRoles(Arrays.asList(new Role[]{Role.ADMIN, Role.USER, Role.GUEST}));
        Address address3 = new Address();
        address3.setStreetName("asda");
        address3.setHouseNumber("asd");
        address3.setCity("asdasd");
        address3.setCountry("asdasd");
        address3.setZipcode("sasd123");
        address3.setLatitude(10);
        address3.setLongitude(14);
        address3.setId((ObjectId) new ObjectIdGenerator().generate());
        user3.setAddress(address3);
        user3.setId((ObjectId) new ObjectIdGenerator().generate());

        // Create User 2
        User user4 = new User();
        user4.setEmail("abcmn@gmail.com");
        user4.setFirstName("Firstm");
        user4.setMiddleName("Middlem");
        user4.setLastName("Lastm");
        user4.setActive(true);
        user4.setMobileNumber("021234234asda");
        user4.setVerified(false);
        user4.setPermalink("abcmngmailcom");
        user4.setId((ObjectId) new ObjectIdGenerator().generate());
        user4.setRoles(Arrays.asList(new Role[]{Role.ADMIN, Role.USER, Role.GUEST}));
        Address address4 = new Address();
        address4.setStreetName("asda");
        address4.setHouseNumber("asd");
        address4.setCity("asdasd");
        address4.setCountry("asdasd");
        address4.setZipcode("sasd123");
        address4.setLatitude(11);
        address4.setLongitude(12);
        address4.setId((ObjectId) new ObjectIdGenerator().generate());
        user4.setAddress(address2);
        user4.setId((ObjectId) new ObjectIdGenerator().generate());

        // Create New Group
        newGroup = new Group();
        newGroup.setActive(true);
        newGroup.setName("Group Test 1");
        HashSet<User> members2 = new HashSet<>();
        members2.addAll(Arrays.asList(user3, user4));
        newGroup.setMembers(members);
        newGroup.setOriginAddress(address1);
        newGroup.setOriginSearchRadius(3);
        newGroup.setDestinationAddress(address2);
        newGroup.setDestinationSearchRadius(3);
        HashSet<User> owners2 = new HashSet<>();
        owners2.addAll(Arrays.asList(user3));
        newGroup.setOwners(owners);
        newGroup.setId((ObjectId) new ObjectIdGenerator().generate());

        // 1. Save the existing Address
        when(groupRepository.findById(existingGroup.getId())).thenReturn(Mono.just(existingGroup));
        when(groupRepository.save(existingGroup)).thenReturn(Mono.just(existingGroup));
        retrievedGroup1 = groupServiceImpl.saveOrUpdate(existingGroup).log().flux().next().block();
    }

    @Test
    public void testMockCreation() {

        assertNotNull(existingGroup);
        assertNotNull(newGroup);
        assertNotNull(group);
        assertNotNull(groupRepository);
        assertNotNull(groupServiceImpl);
    }


    @Test
    public void saveGroup() {

        // 1. Save the existing Address
        when(groupRepository.findById(newGroup.getId())).thenReturn(Mono.empty());
        when(groupRepository.save(newGroup)).thenReturn(Mono.just(newGroup));
        Group retrievedGroup = groupServiceImpl.saveOrUpdate(newGroup).log().flux().next().block();
        assertEquals(newGroup.getId(), retrievedGroup.getId());
    }

    @Test
    public void saveNullGroup(){

        // 1. Save the null Address
        Mono<Group> retrievedGroup = groupServiceImpl.saveOrUpdate(null);
        assertEquals(Mono.empty(), retrievedGroup);
    }

    @Test
    public void updateGroup(){

        // Change existing Address
        existingGroup.setName("Group Test changed Name");
        existingGroup.setActive(false);

        // 1. Save the existing group
        when(groupRepository.findById(existingGroup.getId())).thenReturn(Mono.just(existingGroup));
        when(groupRepository.save(existingGroup)).thenReturn(Mono.just(existingGroup));
        Group retrievedGroup = groupServiceImpl.saveOrUpdate(existingGroup).log().flux().next().block();

        // Check if they are the same object
        assertEquals(unchangedExistingGroup.getId(), retrievedGroup.getId());
    }

    @Test
    public void findGroupById(){

        // Search the group by _id
        Group retrievedGroup = groupServiceImpl.findById(existingGroup.getId()).log().flux().next().block();
        assertEquals(existingGroup, retrievedGroup);
    }


    @Test
    public void findGroupByNullId(){

        // Search the group by Null Address id
        Group retrievedGroup = groupServiceImpl.findById(null).log().flux().next().block();
        assertEquals(null, retrievedGroup);
    }

    @Test
    public void findGroupByOriginAddress(){

        // Search by origin address
        when(groupRepository.findGroupsByOriginAddress(existingGroup.getOriginAddress()))
                .thenReturn(Flux.just(existingGroup));

        Group retrievedGroup = groupServiceImpl
                .findGroupsByOriginAddress(existingGroup.getOriginAddress()).log().next().block();
        assertEquals(existingGroup, retrievedGroup);
    }


    @Test
    public void findGroupByNullOriginAddress(){

        Flux<Group> retrievedGroup = groupServiceImpl
                .findGroupsByOriginAddress(null);

        assertEquals(Flux.empty(), retrievedGroup);
    }

    @Test
    public void findGroupByDestinationAddress(){

        // Search by origin address
        when(groupRepository.findGroupsByDestinationAddress(existingGroup.getDestinationAddress()))
                .thenReturn(Flux.just(existingGroup));

        Group retrievedGroup = groupServiceImpl
                .findGroupsByDestinationAddress(existingGroup.getDestinationAddress())
                .log().next().block();
        assertEquals(existingGroup, retrievedGroup);
    }


    @Test
    public void findGroupByNullDestinationAddress(){

        Flux<Group> retrievedGroup = groupServiceImpl
                .findGroupsByDestinationAddress(null);

        assertEquals(Flux.empty(), retrievedGroup);
    }

    @Test
    public void findGroupByOriginAndDestinationAddress(){

        // Search by origin address
        when(groupRepository
                .findGroupsByOriginAddressAndDestinationAddress(
                        existingGroup.getOriginAddress(),
                        existingGroup.getDestinationAddress()))
                .thenReturn(Flux.just(existingGroup));

        Group retrievedGroup = groupServiceImpl
                .findGroupsByOriginAndDestinationAddress(
                        existingGroup.getOriginAddress(),
                        existingGroup.getDestinationAddress())
                .log().next().block();

        assertEquals(existingGroup, retrievedGroup);
    }


    @Test
    public void findGroupByNullOriginAndDestinationAddress(){

        Flux<Group> retrievedGroup = groupServiceImpl
                .findGroupsByOriginAndDestinationAddress(
                        null,
                        null);

        assertEquals(Flux.empty(), retrievedGroup);
    }


    @Test
    public void findGroupByOriginAndDestinationGeoLocation(){

        // Search by origin address
        when(groupRepository
                .findGroupsByOriginAndDestinationGeoLocationDetails(
                        existingGroup.getOriginAddress().getLatitude(),
                        existingGroup.getOriginAddress().getLongitude(),
                        existingGroup.getDestinationAddress().getLatitude(),
                        existingGroup.getDestinationAddress().getLongitude()))
                .thenReturn(Flux.just(existingGroup));

        Group retrievedGroup = groupServiceImpl
                .findGroupsByOriginAndDestinationGeoLocationDetails(
                        existingGroup.getOriginAddress().getLatitude(),
                        existingGroup.getOriginAddress().getLongitude(),
                        existingGroup.getDestinationAddress().getLatitude(),
                        existingGroup.getDestinationAddress().getLongitude())
                .log().next().block();

        assertEquals(existingGroup, retrievedGroup);
    }

    @Test
    public void findGroupByNullOriginAndDestinationGeoLocation(){

        Flux<Group> retrievedGroup = groupServiceImpl
                .findGroupsByOriginAndDestinationGeoLocationDetails(
                        null, null,
                        null, null);

        assertEquals(Flux.empty(), retrievedGroup);
    }

    @Test
    public void findGroupByOriginWithinThresholds(){

        // Search by origin address
        when(groupRepository
                .findGroupsByOriginWithinThresholds(
                        existingGroup.getOriginAddress().getLatitude()-2,
                        existingGroup.getOriginAddress().getLatitude()+2,
                        existingGroup.getOriginAddress().getLongitude()-2,
                        existingGroup.getOriginAddress().getLongitude()+2))
                .thenReturn(Flux.fromIterable(Arrays.asList(existingGroup, newGroup)));

        List<Group> retrievedGroup = groupServiceImpl
                .findGroupsByOriginWithinThresholds(
                        existingGroup.getOriginAddress().getLatitude()-2,
                        existingGroup.getOriginAddress().getLatitude()+2,
                        existingGroup.getOriginAddress().getLongitude()-2,
                        existingGroup.getOriginAddress().getLongitude()+2)
                .collectList().log().block();

        assertEquals(newGroup, retrievedGroup.get(1));
    }

    @Test
    public void findGroupByNullOriginWithinThreshold(){

        Flux<Group> retrievedGroup = groupServiceImpl
                .findGroupsByOriginWithinThresholds(
                        null, null,
                        null, null);

        assertEquals(Flux.empty(), retrievedGroup);
    }

    @Test
    public void findGroupByDestinationWithinThresholds(){

        // Search by origin address
        when(groupRepository
                .findGroupsByDestinationWithinThresholds(
                        existingGroup.getDestinationAddress().getLatitude()-2,
                        existingGroup.getDestinationAddress().getLatitude()+2,
                        existingGroup.getDestinationAddress().getLongitude()-2,
                        existingGroup.getDestinationAddress().getLongitude()+2))
                .thenReturn(Flux.fromIterable(Arrays.asList(existingGroup, newGroup)));

        List<Group> retrievedGroup = groupServiceImpl
                .findGroupsByDestinationWithinThresholds(
                        existingGroup.getDestinationAddress().getLatitude()-2,
                        existingGroup.getDestinationAddress().getLatitude()+2,
                        existingGroup.getDestinationAddress().getLongitude()-2,
                        existingGroup.getDestinationAddress().getLongitude()+2)
                .collectList().log().block();

        assertEquals(newGroup, retrievedGroup.get(1));
    }

    @Test
    public void findGroupByNullDestinationWithinThresholds(){

        Flux<Group> retrievedGroup = groupServiceImpl
                .findGroupsByDestinationWithinThresholds(
                        null, null,
                        null, null);

        assertEquals(Flux.empty(), retrievedGroup);
    }

    @Test
    public void findGroupByOriginAndDestinationWithinThresholds(){

        // Search by origin address
        when(groupRepository
                .findGroupsByOriginAndDestinationWithinThresholds(
                        existingGroup.getOriginAddress().getLatitude()-2,
                        existingGroup.getOriginAddress().getLatitude()+2,
                        existingGroup.getOriginAddress().getLongitude()-2,
                        existingGroup.getOriginAddress().getLongitude()+2,
                        existingGroup.getDestinationAddress().getLatitude()-2,
                        existingGroup.getDestinationAddress().getLatitude()+2,
                        existingGroup.getDestinationAddress().getLongitude()-2,
                        existingGroup.getDestinationAddress().getLongitude()+2))
                .thenReturn(Flux.fromIterable(Arrays.asList(existingGroup, newGroup)));

        List<Group> retrievedGroup = groupServiceImpl
                .findGroupsByOriginAndDestinationWithinThresholds(
                        existingGroup.getOriginAddress().getLatitude()-2,
                        existingGroup.getOriginAddress().getLatitude()+2,
                        existingGroup.getOriginAddress().getLongitude()-2,
                        existingGroup.getOriginAddress().getLongitude()+2,
                        existingGroup.getDestinationAddress().getLatitude()-2,
                        existingGroup.getDestinationAddress().getLatitude()+2,
                        existingGroup.getDestinationAddress().getLongitude()-2,
                        existingGroup.getDestinationAddress().getLongitude()+2)
                .collectList().log().block();

        assertEquals(newGroup, retrievedGroup.get(1));
    }

    @Test
    public void findGroupByNullOriginAndDestinationWithinThresholds(){

        Flux<Group> retrievedGroup = groupServiceImpl
                .findGroupsByOriginAndDestinationWithinThresholds(
                        null, null,
                        null, null,
                        null, null,
                        null, null);

        assertEquals(Flux.empty(), retrievedGroup);
    }

    @Test
    public void findGroupsByOriginWithinRadius(){

        // Search by origin address
        when(groupRepository
                .findGroupsByOriginWithinThresholds(
                        anyDouble(),
                        anyDouble(),
                        anyDouble(),
                        anyDouble()))
                .thenReturn(Flux.fromIterable(Arrays.asList(existingGroup, newGroup)));

        List<Group> retrievedGroups = groupServiceImpl.findGroupsByOriginWithinRadius(
                existingGroup.getOriginAddress().getLatitude(),
                existingGroup.getOriginAddress().getLongitude(),
                10.0).collectList().log().block();

        assertEquals(newGroup, retrievedGroups.get(1));
    }

    @Test
    public void findGroupsByDestinationWithinRadius(){

        // Search by origin address
        when(groupRepository
                .findGroupsByDestinationWithinThresholds(
                        anyDouble(),
                        anyDouble(),
                        anyDouble(),
                        anyDouble()))
                .thenReturn(Flux.fromIterable(Arrays.asList(existingGroup, newGroup)));

        List<Group> retrievedGroups = groupServiceImpl.findGroupsByDestinationWithinRadius(
                existingGroup.getDestinationAddress().getLatitude(),
                existingGroup.getDestinationAddress().getLongitude(),
                10.0).collectList().log().block();

        assertEquals(newGroup, retrievedGroups.get(1));
    }

    @Test
    public void findGroupsByOriginAndDestinationWithinRadius(){

        // Search by origin address
        when(groupRepository
                .findGroupsByOriginAndDestinationWithinThresholds(
                        anyDouble(), anyDouble(),
                        anyDouble(), anyDouble(),
                        anyDouble(), anyDouble(),
                        anyDouble(), anyDouble()))
                .thenReturn(Flux.fromIterable(Arrays.asList(existingGroup, newGroup)));

        List<Group> retrievedGroups = groupServiceImpl.findGroupsByOriginAndDestinationWithinRadius(
                existingGroup.getOriginAddress().getLatitude(),
                existingGroup.getOriginAddress().getLongitude(), 10.0,
                existingGroup.getDestinationAddress().getLatitude(),
                existingGroup.getDestinationAddress().getLongitude(), 10.0)
                .collectList().log().block();

        assertEquals(newGroup, retrievedGroups.get(1));
    }

    @Test
    public void findAll(){

        when(groupRepository.findAll()).thenReturn(Flux.fromIterable(Arrays.asList(existingGroup, newGroup)));

        List<Group> retrievedGroup = groupServiceImpl.findAll().collectList().block();

        assertEquals(existingGroup, retrievedGroup.get(0));
        assertEquals(2, retrievedGroup.size());
    }

    @Test
    public void deleteGroupById(){

        when(groupRepository.deleteById(existingGroup.getId())).thenReturn(Mono.empty());

        groupServiceImpl.deleteById(existingGroup.getId());

        verify(groupRepository, times(1)).deleteById(existingGroup.getId());
    }

    @Test
    public void deleteGroupByNullId(){

        Mono<Void> retrievedGroup = groupServiceImpl.deleteById(null);

        assertEquals(Mono.empty(), retrievedGroup);
    }

    @Test
    public void deleteGroupAll(){

        when(groupRepository.deleteAll()).thenReturn(Mono.empty());

        groupServiceImpl.deleteAll();

        verify(groupRepository, times(1)).deleteAll();
    }
}


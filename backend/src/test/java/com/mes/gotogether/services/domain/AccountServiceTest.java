package com.mes.gotogether.services.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

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
import org.springframework.data.util.Optionals;
import org.springframework.web.client.RestTemplate;

import com.mes.gotogether.domains.Address;
import com.mes.gotogether.domains.NomatimOpenStreetMapQuery;
import com.mes.gotogether.repositories.domain.AddressRepository;
import com.mes.gotogether.services.externalconnections.GeoLocationService;

import io.netty.handler.ssl.OptionalSslHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AccountServiceTest {

    @Mock
    private AddressRepository addressRepository;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private Address address;
    @Mock
    private NomatimOpenStreetMapQuery nomatimOpenStreetMapQueries;
    @Mock
    private GeoLocationService geoLocationService;
    private AddressServiceImpl addressServiceImpl;
    private Address existingAddress;
    private Address unchangedExistingAddress;
    private Address newAddress;
    private Address retrievedAddress1;
    private NomatimOpenStreetMapQuery[] existingAddressMapQuery;
    private NomatimOpenStreetMapQuery[] newAddressMapQuery;
    private NomatimOpenStreetMapQuery mapQuery1;
    private NomatimOpenStreetMapQuery mapQuery2;

    @BeforeEach
    public void setUp() {

        System.out.println("@BeforeEach is called!");
        MockitoAnnotations.initMocks(this);
        addressServiceImpl = new AddressServiceImpl(addressRepository, restTemplate, geoLocationService);

        // Create Existing Address
        existingAddress = new Address();
        existingAddress.setStreetName("Martin Volderstraat");
        existingAddress.setHouseNumber("12");
        existingAddress.setCity("Utrecht");
        existingAddress.setCountry("The Netherlands");
        existingAddress.setZipcode("1234 EZ");
        existingAddress.setState("Utrecht");
        existingAddress.setId((ObjectId) new ObjectIdGenerator().generate());
        unchangedExistingAddress = new Address(existingAddress);

        mapQuery1 = new NomatimOpenStreetMapQuery();
        mapQuery1.setLatitude(11);
        mapQuery1.setLongitude(13);
        existingAddressMapQuery = new NomatimOpenStreetMapQuery[1];
        existingAddressMapQuery[0] = mapQuery1;

        // Create New Address
        newAddress = new Address();
        newAddress.setStreetName("Jan Janneman");
        newAddress.setHouseNumber("145");
        newAddress.setCity("Alkmaar");
        newAddress.setCountry("The Netherlands");
        newAddress.setZipcode("5421 AZ");
        newAddress.setState("Noord Holland");
        newAddress.setId((ObjectId) new ObjectIdGenerator().generate());

        mapQuery2 = new NomatimOpenStreetMapQuery();
        mapQuery2.setLatitude(11);
        mapQuery2.setLongitude(13);
        newAddressMapQuery = new NomatimOpenStreetMapQuery[1];
        newAddressMapQuery[0] = mapQuery2;

        // 1. Save the existing Address
        when(addressRepository.findById(existingAddress.getId())).thenReturn(Mono.just(existingAddress));
        when(restTemplate.getForObject(anyString(),eq(NomatimOpenStreetMapQuery[].class))).thenReturn(existingAddressMapQuery);
        when(addressRepository.save(existingAddress)).thenReturn(Mono.just(existingAddress));
        when(geoLocationService.getAddressLongitudeAndLatitude(existingAddress)).thenReturn(Optional.of(new Double[] {43.0359862, -7.658727}));
        retrievedAddress1 = addressServiceImpl.saveOrUpdateAddress(existingAddress).log().flux().next().block();
    }

    @Test
    public void testMockCreation(){

        assertNotNull(existingAddress);
        assertNotNull(newAddress);
        assertNotNull(address);
        assertNotNull(addressRepository);
        assertNotNull(addressServiceImpl);
    }

    @Test
    public void saveAddress(){

        // 1. Save the existing Address
        when(addressRepository.findById(newAddress.getId())).thenReturn(Mono.empty());
        when(restTemplate.getForObject(anyString(),eq(NomatimOpenStreetMapQuery[].class))).thenReturn(newAddressMapQuery);
        when(addressRepository.save(newAddress)).thenReturn(Mono.just(newAddress));
        when(geoLocationService.getAddressLongitudeAndLatitude(newAddress)).thenReturn(Optional.of(new Double[] {51.9721063, 5.3446671}));
        Address retrievedAddress = addressServiceImpl.saveOrUpdateAddress(newAddress).log().flux().next().block();
        assertEquals(newAddress.getId(), retrievedAddress.getId());
    }

    @Test
    public void saveNullAddress(){

        // 1. Save the null Address
        Mono<Address> retrievedAddress = addressServiceImpl.saveOrUpdateAddress(null);
        assertEquals(Mono.empty(), retrievedAddress);
    }

    @Test
    public void updateAddress(){

        // Change existing Address
        existingAddress.setZipcode("4123 EJ");
        existingAddress.setStreetName("Van Pallandtlaan");

        // 1. Save the existing Address
        when(addressRepository.findById(existingAddress.getId())).thenReturn(Mono.just(existingAddress));
        when(restTemplate.getForObject(anyString(),eq(NomatimOpenStreetMapQuery[].class))).thenReturn(existingAddressMapQuery);
        when(addressRepository.save(existingAddress)).thenReturn(Mono.just(existingAddress));
        when(geoLocationService.getAddressLongitudeAndLatitude(existingAddress)).thenReturn(Optional.of(new Double[] {43.0359862, -7.658727}));
        Address retrievedAddress = addressServiceImpl.saveOrUpdateAddress(existingAddress).log().flux().next().block();

        // Check if they are the same object
        assertEquals(unchangedExistingAddress.getId(), retrievedAddress.getId());
    }

    @Test
    public void findAddressById(){

        // Search the Address by _id
        Address retrievedAddress = addressServiceImpl.findAddressById(existingAddress.getId()).log().flux().next().block();
        assertEquals(existingAddress, retrievedAddress);
    }


    @Test
    public void findAddressByNullId(){

        // Search the Address by Null Address id
        Address retrievedAddress = addressServiceImpl.findAddressById(null).log().flux().next().block();
        assertEquals(null, retrievedAddress);
    }

    @Test
    public void findAllAddress(){

        // Search all existing address details
        when(addressRepository.findAll()).thenReturn(Flux.just(existingAddress));
        List<Address> retrievedAddress = addressServiceImpl.findAll().collectList().log().flux().next().block();
        assertNotNull(retrievedAddress);
        assertEquals(existingAddress, retrievedAddress.get(0));
    }

    @Test
    public void findAddressByAddressDetails(){

        // Find the existing address
        when(addressRepository
                .findFirstAddressByStreetNameAndHouseNumberAndCityAndCountryOrderByLastModifiedDesc(
                anyString(),anyString(), anyString(), anyString()
        )).thenReturn(Mono.just(existingAddress));
        Address retrievedAddress = addressServiceImpl
                .findAddressByStreetNameAndHouseNumberAndCityAndCountry(
                existingAddress.getStreetName(),
                existingAddress.getHouseNumber(),
                existingAddress.getCity(),
                existingAddress.getCountry()
        ).log().flux().next().block();
        assertNotNull(retrievedAddress);
        assertEquals(existingAddress, retrievedAddress);
    }

    @Test
    public void findAddressByNullAddressDetails(){

        // Find the existing address
        when(addressRepository
                .findFirstAddressByStreetNameAndHouseNumberAndCityAndCountryOrderByLastModifiedDesc(
                null, null, null, null
        )).thenReturn(Mono.empty());
        Address retrievedAddress = addressServiceImpl
                .findAddressByStreetNameAndHouseNumberAndCityAndCountry(
                        null,
                        null,
                        null,
                        null
                ).log().flux().next().block();
        verify(addressRepository, times(0))
                .findFirstAddressByStreetNameAndHouseNumberAndCityAndCountryOrderByLastModifiedDesc(
                null, null, null, null
        );
    }

    @Test
    public void findAddressByLatitudeAndLongitude(){

        // Find the existing address
        when(addressRepository.findFirst10AddressByLatitudeAndLongitudeOrderByLastModifiedDesc(
                anyDouble(),anyDouble()
        )).thenReturn(Flux.just(existingAddress));
        List<Address> retrievedAddress = addressServiceImpl
                .findAddressByLatitudeAndLongitudeAnd(
                        existingAddress.getLatitude(),
                        existingAddress.getLongitude()
                ).collectList().log().flux().next().block();
        assertNotNull(retrievedAddress);
        assertEquals(existingAddress, retrievedAddress.get(0));
    }

    @Test
    public void findAddressByNullLatitudeAndLongitude(){

        // Find the existing address
        when(addressRepository
                .findFirst10AddressByLatitudeAndLongitudeOrderByLastModifiedDesc(
                null, null
        )).thenReturn(Flux.empty());
        List<Address> retrievedAddress = addressServiceImpl
                .findAddressByLatitudeAndLongitudeAnd(
                        null, null
                ).collectList().log().flux().next().block();
        verify(addressRepository, times(0))
                .findFirst10AddressByLatitudeAndLongitudeOrderByLastModifiedDesc(
                        null, null
        );
    }

    @Test
    public void deleteAddressById(){

        // Delete address by _id
        when(addressRepository.deleteById(any(ObjectId.class))).thenReturn(Mono.empty());
        addressServiceImpl.deleteAddressById(existingAddress.getId()).log().flux().next().block();
        verify(addressRepository, times(1)).deleteById(existingAddress.getId());
    }

    @Test
    public void deleteAddressByNullId(){

        // Delete address by _id
        // when(addressRepository.deleteById(null).thenReturn(Mono.empty());
        Mono<Void> retrievedAddress = addressServiceImpl.deleteAddressById(null);
        assertEquals(Mono.empty(), retrievedAddress);
    }


    @Test
    public void deleteAddressByAddressDetails(){

        // Delete address by address details
        when(addressRepository.deleteAddressByStreetNameAndHouseNumberAndCityAndCountry(
                anyString(),
                anyString(),
                anyString(),
                anyString())).thenReturn(Mono.empty());
        addressServiceImpl.deleteAddressByStreetNameAndHouseNumberAndCityAndCountryAnd(
                existingAddress.getStreetName(),
                existingAddress.getHouseNumber(),
                existingAddress.getCity(),
                existingAddress.getCountry())
                .log().flux().next().block();
        verify(addressRepository, times(1)).deleteAddressByStreetNameAndHouseNumberAndCityAndCountry(
                existingAddress.getStreetName(),
                existingAddress.getHouseNumber(),
                existingAddress.getCity(),
                existingAddress.getCountry());
    }


    @Test
    public void deleteAddressByNullAddressDetails(){

        // Delete address by address details
        when(addressRepository.deleteAddressByStreetNameAndHouseNumberAndCityAndCountry(
                null,
                null,
                null,
                null)).thenReturn(Mono.empty());
        addressServiceImpl.deleteAddressByStreetNameAndHouseNumberAndCityAndCountryAnd(
                null,
                null,
                null,
                null)
                .log().flux().next().block();
        verify(addressRepository, times(0)).deleteAddressByStreetNameAndHouseNumberAndCityAndCountry(
                null,
                null,
                null,
                null);
    }


    @Test
    public void deleteAddressByLatitudeAndLongitude(){

        // Delete address by address details
        when(addressRepository.deleteAddressByLatitudeAndLongitude(
                anyDouble(),
                anyDouble())).thenReturn(Mono.empty());
        addressServiceImpl.deleteAddressByLatitudeAndLongitude(
                existingAddress.getLongitude(),
                existingAddress.getLatitude())
                .log().flux().next().block();
        verify(addressRepository, times(1)).deleteAddressByLatitudeAndLongitude(
                existingAddress.getLongitude(),
                existingAddress.getLatitude());
    }

    @Test
    public void deleteAddressByNullLatitudeAndLongitude(){

        // Delete address by address details
        when(addressRepository.deleteAddressByLatitudeAndLongitude(
                null,
                null)).thenReturn(Mono.empty());
        addressServiceImpl.deleteAddressByLatitudeAndLongitude(
                null,
                null)
                .log().flux().next().block();
        verify(addressRepository, times(0))
                .deleteAddressByLatitudeAndLongitude(null,null);
    }

    @Test
    public void deleteAll(){

        // Verify if all address are deleted
        when(addressRepository.deleteAll()).thenReturn(Mono.empty());
        addressServiceImpl.deleteAll();
        verify(addressRepository, times(1)).deleteAll();

        // Check if the existing address still exists
        when(addressRepository.findById(any(ObjectId.class))).thenReturn(Mono.empty());
        Mono<Address> retrievedAddress = addressServiceImpl.findAddressById(existingAddress.getId());
        assertEquals(Mono.empty(),retrievedAddress);
    }
}

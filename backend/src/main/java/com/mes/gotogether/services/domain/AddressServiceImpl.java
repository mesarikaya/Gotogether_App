package com.mes.gotogether.services.domain;

import com.mes.gotogether.domains.Address;
import com.mes.gotogether.repositories.domain.AddressRepository;
import com.mes.gotogether.services.externalconnections.GeoLocationService;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.util.ObjectUtils.*;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@Transactional
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final RestTemplate restTemplate;
    private final GeoLocationService geoLocationService;

    public AddressServiceImpl(
    		AddressRepository addressRepository, 
    		RestTemplate restTemplate,
    		GeoLocationService geoLocationService) {
        this.addressRepository = addressRepository;
        this.restTemplate = restTemplate;
        this.geoLocationService = geoLocationService;
    }

    // FIND METHODS
    @Override
    public Mono<Address> findAddressById(ObjectId id) {

        if (Objects.isNull(id)) {
            return Mono.empty();
        }

        return addressRepository.findById(id);
    }

    @Override
    public Mono<Address> findAddressByStreetNameAndHouseNumberAndCityAndCountry(String streetName,
                                                                                String houseNumber,
                                                                                String city,
                                                                                String country) {

        // If null, do nothing
        if (isEmpty(streetName)
                || isEmpty(houseNumber)
                || isEmpty(city)
                || isEmpty(country)) {
            return Mono.empty();
        }

        return addressRepository.findFirstAddressByStreetNameAndHouseNumberAndCityAndCountryOrderByLastModifiedDesc(
                streetName,
                houseNumber,
                city,
                country);
    }

    @Override
    public Flux<Address> findAddressByLatitudeAndLongitudeAnd(double latitude, double longitude) {

        // If null, do nothing
        if (Double.isNaN(latitude) || Double.isNaN(longitude)) {
            return Flux.empty();
        }

        return addressRepository.findFirst10AddressByLatitudeAndLongitudeOrderByLastModifiedDesc(latitude, longitude);
    }

    @Override
    public Flux<Address> findAll() {
        return addressRepository.findAll();
    }

    // SAVE OR UPDATE METHODS
    @Override
    public Mono<Address> saveOrUpdateAddress(Address address) {

        if (!Objects.isNull(address)){
            return this.findAddressById(address.getId())
                    .flatMap(addressResult -> {
                            addressResult.setId(address.getId());
                            return this.setAddressLatitudeAndLongitude(addressResult)
                                             .flatMap(returnedAddress->addressRepository.save(returnedAddress))
                                             .switchIfEmpty(Mono.defer(()-> Mono.empty()));
                    })
                    .switchIfEmpty(Mono.defer(() -> {
                        log.debug("Creating a new Address");
                        return this.setAddressLatitudeAndLongitude(address)
                                .flatMap(returnedAddress->addressRepository.save(returnedAddress))
                                .switchIfEmpty(Mono.defer(()-> Mono.empty()));
                    }));
            // TODO: ADD ERROR OR SUCCESS HANDLERS*/
        }else{
            // TODO: CREATE ERROR HANDLERS
            log.info("A Null address data is entered. Do not process!");
            return Mono.empty();
        }
    }

    @Override
    public Mono<Address> saveFakeAddress(Address address) {
        return addressRepository.save(address);
    }

    // DELETE METHODS
    @Override
    public Mono<Void> deleteAddressById(ObjectId id) {

        if (Objects.isNull(id)) {
            return Mono.empty();
        }

        return addressRepository.deleteById(id);
    }

    @Override
    public Mono<Void> deleteAddressByStreetNameAndHouseNumberAndCityAndCountryAnd(String streetName, String houseNumber, String city, String country) {

        // If any is null, do nothing
        if (isEmpty(streetName)
                || isEmpty(houseNumber)
                || isEmpty(city)
                || isEmpty(country)) {
            return Mono.empty();
        }

        return addressRepository.deleteAddressByStreetNameAndHouseNumberAndCityAndCountry(
                streetName,
                houseNumber,
                city,
                country);
    }

    @Override
    public Mono<Void> deleteAddressByLatitudeAndLongitude(double latitude, double longitude) {

        // If any is null, do nothing
        if (Double.isNaN(latitude) || Double.isNaN(longitude)) {
            return Mono.empty();
        }

        return addressRepository.deleteAddressByLatitudeAndLongitude(latitude, longitude);
    }

    @Override
    public Mono<Void> deleteAll() {
        return addressRepository.deleteAll();
    }

    @Override
    public Mono<Address> setAddressLatitudeAndLongitude(Address address) {

        // If null, do nothing
        if (Objects.isNull(address)) {
            return Mono.empty();
        }
       
        Optional<Double[]> latitudeAndLongitude = geoLocationService.getAddressLongitudeAndLatitude(address);
        
        if (latitudeAndLongitude.isEmpty()) {
        	return Mono.empty();
        }else {
        	Double[] queryResult = latitudeAndLongitude.get();
        	address.setLatitude(queryResult[0]);
            address.setLongitude(queryResult[1]);
        }

        return Mono.just(address);
    }
    
    
}

package com.mes.gotogether.services.externalconnections;

import com.mes.gotogether.domains.Address;
import com.mes.gotogether.domains.NomatimOpenStreetMapQuery;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public final class NomatimMapLocation implements GeoLocationService{

	private final RestTemplate restTemplate;
	private static final String baseUrl = "https://nominatim.openstreetmap.org/search/";
	private static final String jsonResultFormat = "?format=json&addressdetails=1&limit=1";  
	
	public NomatimMapLocation(RestTemplate restTemplate) {
                        this.restTemplate = restTemplate;
	}
	
	@Override
	public <T> Optional<Double[]> getAddressLongitudeAndLatitude(T searchAddress) {
		
		String urlParameter = "";
		if (searchAddress instanceof Address) {
			Address address = (Address) searchAddress;
			if (ObjectUtils.isEmpty(address)) return Optional.empty();
			urlParameter = address.getStreetName() + " " + address.getHouseNumber() + 
					", " + address.getCity() +", " + address.getZipcode() + ", " + address.getCountry();
		}else if(searchAddress instanceof String) {
			String address = (String) searchAddress;
			if (ObjectUtils.isEmpty(address) || ObjectUtils.isEmpty(address.trim())) return Optional.empty();
			urlParameter = (String) address.trim();
		}else {
			// TODO: Create an exception for this case
			log.debug("An input type different than Address or string"
					+ " class is provided with urlParameter: " +  urlParameter 
					+ " and searchAddress: " + searchAddress);
			return Optional.empty();
		}
		
		String url = setGeoLocationSearchAddress(baseUrl, urlParameter, jsonResultFormat);
		NomatimOpenStreetMapQuery[] queryResult = getQueryResult(url);
                                        log.info("**** QUERY ARRAY LENGTH: " + queryResult.length);
                                        if (queryResult.length>0) {
                                                log.info("******QUERY RESULT IS: " + queryResult[0].toString());
                                                return returnResultAsOptional(queryResult);
                                        }else {
                                                return Optional.empty();
                                        }
	}

	private NomatimOpenStreetMapQuery[] getQueryResult(String searchAddress){

                            NomatimOpenStreetMapQuery[] defaultResult = {};
                            NomatimOpenStreetMapQuery[] queryResult = Optional.ofNullable(restTemplate.getForObject(
                                                                                                                                                                      searchAddress,
                                                                                                                                                                      NomatimOpenStreetMapQuery[].class))
                                                                                                                                                                      .orElse(defaultResult);

                            return queryResult;
	}
	
	private String setGeoLocationSearchAddress(String... params) {
		
                        return Arrays.stream(params)
                                              .filter(p -> p!=null)
                                             .collect(Collectors.joining());
                    }
	
	private Optional<Double[]> returnResultAsOptional(NomatimOpenStreetMapQuery[] queryResult) {
		
                        log.info("Nomatiom query result is successful");
                        Objects.requireNonNull(queryResult);
                        if (queryResult.length>0){
                            return Optional.of(new Double[]{queryResult[0].getLatitude(), queryResult[0].getLongitude()});
                         }
                        else{
                            log.info("Nomatim query result is empty. Therefore returning empty optional");
                            return Optional.empty();
                        }
	}
}

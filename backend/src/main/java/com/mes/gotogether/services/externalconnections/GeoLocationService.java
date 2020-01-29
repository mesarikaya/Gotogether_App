package com.mes.gotogether.services.externalconnections;

import java.util.Optional;

public interface GeoLocationService {
	
    <T> Optional<Double[]> getAddressLongitudeAndLatitude(T searchAddress);	
}

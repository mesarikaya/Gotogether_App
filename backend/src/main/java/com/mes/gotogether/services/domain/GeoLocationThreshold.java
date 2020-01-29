package com.mes.gotogether.services.domain;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class GeoLocationThreshold {

    private final double latMin;
    private final double latMax;
    private final double longMin;
    private final double longMax;
    private final double radius;
    private final double latitude;
    private final double longitude;

    GeoLocationThreshold(double latitude, double longitude, double radius) {

        // All distance is assumed to be in km
        // Earth radius is in km ~= 6371
        double earthRadius = 6371;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;

       // Ref: http://janmatuschek.de/LatitudeLongitudeBoundingCoordinates
        double latT = Math.asin(Math.sin(Math.toRadians(latitude))/Math.cos(radius/earthRadius));
        double deltaLon = Math.acos( ( Math.cos(radius*0.5/earthRadius) - Math.sin(latT)*Math.sin(Math.toRadians(latitude)) ) / (Math.cos(latT)*Math.cos(Math.toRadians(latitude))));
        double lonMin  = Math.toRadians(longitude) - deltaLon;
        double lonMax = Math.toRadians(longitude) + deltaLon;
        double laMin = Math.toRadians(latitude) - (radius*0.5/earthRadius);
        double laMax = Math.toRadians(latitude) + (radius*0.5/earthRadius);
         this.longMax = Math.toDegrees(lonMax);
         this.longMin = Math.toDegrees(lonMin);
         this.latMax = Math.toDegrees(laMax);
         this.latMin = Math.toDegrees(laMin);
    }
}

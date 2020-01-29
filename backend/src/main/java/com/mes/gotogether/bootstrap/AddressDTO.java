package com.mes.gotogether.bootstrap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"StreetName", "HouseNumber", "City", "ZipCode", "StateFull", "Country", "Latitude", "Longitude"})
public class AddressDTO {

    @JsonProperty("StreetName")
    private String streetName;
    @JsonProperty("HouseNumber")
    private String houseNumber;
    @JsonSetter("City")
    private String city;
    @JsonProperty("ZipCode")
    private String zipcode;
    @JsonProperty("StateFull")
    private String state;
    @JsonProperty("Country")
    private String country;
    @JsonProperty("Latitude")
    private double latitude;
    @JsonProperty("Longitude")
    private double longitude;

    @Override
    public String toString() {
        return "Generated AddressDTO{" +
                "streetName='" + streetName + '\'' +
                ", houseNumber='" + houseNumber + '\'' +
                ", city='" + city + '\'' +
                ", zipcode='" + zipcode + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}

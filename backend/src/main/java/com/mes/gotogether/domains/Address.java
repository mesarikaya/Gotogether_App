package com.mes.gotogether.domains;

import java.util.Date;
import java.util.Objects;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Getter
@Setter
@Document
public class Address {

    @Id
    private ObjectId id;
    private String streetName;
    private String houseNumber;
    private String city;
    private String zipcode;
    private String state;
    private String country;
    private double latitude;
    private double longitude;
    private Date lastModified;

    public Address(Address address){
        this(
                address.getId(),
                address.getStreetName(),
                address.getHouseNumber(),
                address.getCity(),
                address.getZipcode(),
                address.getState(),
                address.getCountry(),
                address.getLatitude(),
                address.getLongitude(),
                address.getLastModified()
        );
    }

    @PersistenceConstructor
    public Address(ObjectId id, String streetName, String houseNumber, String city, String zipcode,
                   String state, String country, double latitude, double longitude, Date lastModified) {
        this.id = id;
        this.streetName = streetName;
        this.houseNumber = houseNumber;
        this.city = city;
        this.zipcode = zipcode;
        this.state = state;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.lastModified = lastModified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return streetName.equals(address.streetName) &&
                houseNumber.equals(address.houseNumber) &&
                city.equals(address.city) &&
                Objects.equals(zipcode, address.zipcode) &&
                Objects.equals(state, address.state) &&
                country.equals(address.country) &&
                Objects.equals(latitude, address.latitude) &&
                Objects.equals(longitude, address.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(streetName, houseNumber, city, zipcode, state, country, latitude, longitude);
    }
    
    @Override
    public String toString() {
        return streetName + " " + houseNumber + ", " +  city + ", " + zipcode + ", "  + state + ", " + country; 
    }
}

package com.mes.gotogether.bootstrap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"FirstName", "MiddleInitial", "Surname", "EmailAddress", "Password"})
public class UserDTO {

    @JsonProperty("FirstName")
    private String firstName;
    @JsonProperty("MiddleInitial")
    private String middleName;
    @JsonProperty("Surname")
    private String lastName;
    @JsonProperty("EmailAddress")
    private String email;
    @JsonProperty("Password")
    private String password;
    
    @Override
    public String toString() {
        return "Generated UserDTO{" +
                "firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}

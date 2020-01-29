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
@JsonPropertyOrder({"GroupName"})
public class GroupDTO {

    @JsonProperty("GroupName")
    private String name;

    @Override
    public String toString() {
        return "Generated GroupDTO{" +
                "name='" + name + '\'' +
                '}';
    }
}

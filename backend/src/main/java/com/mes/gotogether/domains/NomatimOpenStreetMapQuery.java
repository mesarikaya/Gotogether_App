package com.mes.gotogether.domains;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public final class NomatimOpenStreetMapQuery {

    @JsonProperty("place_id")
    private Long placeId;
    @JsonProperty("osm_id")
    private Long osmId;
    @JsonProperty("lat")
    private double latitude;
    @JsonProperty("lon")
    private double longitude;
    @JsonProperty("display_name")
    private String displayName;

    @Override
    public String toString() {
        return "NomatimOpenStreetMapQuery{" +
                "placeId=" + placeId +
                ", osmId=" + osmId +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}

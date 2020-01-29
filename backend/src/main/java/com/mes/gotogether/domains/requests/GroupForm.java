/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mes.gotogether.domains.requests;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GroupForm {
    @NotNull
    String userId;
    @NotNull
    String groupName;
    @NotNull
    String originStreetName;
    @NotNull
    String originHouseNumber;
    @NotNull
    String originCity;
    @NotNull
    Double originSearchRadius;
    @NotNull
    String originZipcode;
    @NotNull
    String originState;
    @NotNull
    String originCountry;
    @NotNull
    String destinationStreetName;
    @NotNull
    String destinationHouseNumber;
    @NotNull
    String destinationCity;
    @NotNull
    Double destinationSearchRadius;
    @NotNull
    String destinationZipcode;
    @NotNull
    String destinationState;
    @NotNull
    String destinationCountry;
}

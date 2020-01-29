/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mes.gotogether.domains.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@Getter
@ToString
public class HttpResponse {
    
    private final HttpStatus httpStatus;
    private final ResponseType type;
    private final String message;
   
    public enum ResponseType {
        SUCCESS, FAILURE;
    }
}

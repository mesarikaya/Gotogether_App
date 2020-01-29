package com.mes.gotogether.validators;

import java.util.Map;

import com.mes.gotogether.security.domain.AuthRequest;

import lombok.Data;

@Data
public class UserLoginResponse {

    private AuthRequest user;
    private boolean validated;
    private Map<String, String> errorMessages;

}
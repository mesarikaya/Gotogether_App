package com.mes.gotogether.security.domain;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AuthResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String token;
    private String username;
}

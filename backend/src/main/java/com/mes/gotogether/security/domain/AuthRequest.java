package com.mes.gotogether.security.domain;

import com.mes.gotogether.validators.ExtendedEmailValidator;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AuthRequest {

    @NotNull
    @ExtendedEmailValidator
    private String userName;
    @NotNull
    @Min(8)
    private String password;
}

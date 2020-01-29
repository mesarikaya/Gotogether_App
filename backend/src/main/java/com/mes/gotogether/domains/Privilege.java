package com.mes.gotogether.domains;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Document
@AllArgsConstructor
public class Privilege {

    @Id
    private String id;
    private String[] privilegeSet;
}

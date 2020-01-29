package com.mes.gotogether.security.domain;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bson.types.ObjectId;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberDeleteRequest {

    @NotNull
    private ObjectId groupId;
    @NotNull
    private String userId;
}

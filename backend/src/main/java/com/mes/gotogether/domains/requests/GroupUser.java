package com.mes.gotogether.domains.requests;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public final class GroupUser {

    @NotNull
    private String groupId;
    @NotNull
    private String userId;
}

package com.mes.gotogether.domains.responses;

import com.mes.gotogether.domains.User;
import java.util.Objects;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Getter
@ToString
public final class UserSearchResponse {
    
            private final String firstName;
            private final String middleName;
            private final String surname;
            private final String userName;
            private final String address;
            
            public UserSearchResponse(User user){
                            Objects.requireNonNull(user);
                            this.firstName = user.getFirstName();
                            this.middleName = user.getMiddleName();
                            this.surname = user.getLastName();
                            this.userName = user.getUserId();
                            this.address = user.getAddress().toString();
            }
}

package com.mes.gotogether.domains;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Users")
@Data
@NoArgsConstructor
@Getter
@Setter
@CompoundIndexes({
        @CompoundIndex(name = "user_idx", def = "{'userId' : 1}")
})
public class User {

    @Id
    private ObjectId id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String oauthId;
    @JsonIgnore
    @Setter(AccessLevel.NONE)
    private String userId;
    private LoginType loginType;
    private String password;
    @ToString.Exclude
    private Address address;
    private String permalink;
    private String mobileNumber;
    private String lastLogin;
    private boolean isVerified;
    private String verificationToken;
    private LocalDateTime verificationExpiresAt;
    private boolean isActive;
    private List<Role> roles;
    
    public User(User user){
        this(
            user.getId(),
            user.getEmail(),
            user.getOauthId(),
            user.getPassword(),
            user.getRoles()
        );
    }

    @PersistenceConstructor
    public User(ObjectId id, String email, String oauthId, String password, List<Role> roles) {
        this.id = id;
        this.email = email;
        this.oauthId = oauthId;
        this.userId = email.split("@")[0] + oauthId;
        this.password = password;
        this.roles = roles;
        this.verificationToken = UUID.randomUUID().toString();
        this.verificationExpiresAt = LocalDateTime.now().plusMinutes(10);
    }

    public void setEmail(String email) {
        this.email = email;
        this.setUserId();
    }

    public void setOauthId(String oauthId) {
        this.oauthId = oauthId;
        this.setUserId();
    }

    private void setUserId() {
        this.userId = this.email.split("@")[0] + this.oauthId;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id) &&
                userId.equals(user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId);
    }
}

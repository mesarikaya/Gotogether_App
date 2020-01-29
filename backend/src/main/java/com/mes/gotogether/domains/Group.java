package com.mes.gotogether.domains;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@Getter
@Setter
public class Group {

    @Id
    private ObjectId id;
    @NotNull
    private String name;
    private Set<User> members;
    private Set<User> owners;
    private Set<User> membershipRequests;
    private Set<User> invites;
    private Address originAddress;
    private double originSearchRadius;
    private Address destinationAddress;
    private double destinationSearchRadius;
    private boolean isActive;

    public Group(){
        members = new HashSet<>();
        owners = new HashSet<>();
        membershipRequests = new HashSet<>();
        invites = new HashSet<>();
    }
    
    public Group(Group group){
        this(
                group.getId(),
                group.getName(),
                group.getMembers(),
                group.getOwners(),
                group.getMembershipRequests(),
                group.getOriginAddress(),
                group.getOriginSearchRadius(),
                group.getDestinationAddress(),
                group.getDestinationSearchRadius(),
                group.isActive()
        );
    }

    @PersistenceConstructor
    public Group(ObjectId id, String name, Set<User> members,
                            Set<User> owners, Set<User> membershipRequests,
                            Address originAddress,
                            double originSearchRadius, Address destinationAddress,
                            double destinationSearchRadius, boolean isActive) {
        this.id = id;
        this.name = name;
        this.members = members;
        this.owners = owners;
        this.membershipRequests = membershipRequests;
        this.originAddress = originAddress;
        this.originSearchRadius = originSearchRadius;
        this.destinationAddress = destinationAddress;
        this.destinationSearchRadius = destinationSearchRadius;
        this.isActive = isActive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        
        return id.equals(group.id) &&name.equals(group.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}

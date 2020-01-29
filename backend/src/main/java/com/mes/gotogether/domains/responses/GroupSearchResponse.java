package com.mes.gotogether.domains.responses;

import com.mes.gotogether.domains.Group;
import java.util.HashSet;
import java.util.Objects;
import static java.util.stream.Collectors.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;

@Slf4j
@Data
@Getter
@ToString
public final class GroupSearchResponse {
	
	private final String id;
	private final String name;
	private final GroupDetails groupDetails;
	private final Members members;
                    private final WaitingList waitingList;
                    private final InvitationList invitationList;
	
	public GroupSearchResponse(Group group) {
                            Objects.requireNonNull(group);
                            Objects.requireNonNull(group.getOriginAddress());
                            Objects.requireNonNull(group.getDestinationAddress());
                            this.id = group.getId().toHexString();
                            this.name = group.getName();
                            this.groupDetails = new GroupDetails(group.getOriginAddress().getCity(),group.getOriginAddress().getZipcode(),
                                                       group.getOriginSearchRadius(), group.getDestinationAddress().getCity(),
                                                       group.getDestinationAddress().getZipcode(),group.getDestinationSearchRadius());
                            this.members = new Members(group.getMembers().stream()
                                                                                                                  .map(member -> new User(member.getId(),
                                                                                                                            member.getFirstName() + " " + member.getLastName(), 
                                                                                                                            group.getOwners().contains(member),
                                                                                                                            member.getUserId(),
                                                                                                                            member.getAddress().toString()))
                                                                                                                  .collect(toCollection(HashSet::new)));
                            this.waitingList = new WaitingList(group.getMembershipRequests().stream()
                                                                                                                                           .map(member -> new User(member.getId(),
                                                                                                                                                    member.getFirstName() + " " + member.getLastName(), 
                                                                                                                                                    group.getOwners().contains(member),
                                                                                                                                                    member.getUserId(),
                                                                                                                                                    member.getAddress().toString()))
                                                                                                                                           .collect(toCollection(HashSet::new)));
                            this.invitationList = new InvitationList(group.getInvites().stream()
                                                                                                                        .map( member -> new User(member.getId(), 
                                                                                                                                  member.getFirstName() + " " + member.getLastName(), 
                                                                                                                                group.getOwners().contains(member), 
                                                                                                                                member.getUserId(),
                                                                                                                                member.getAddress().toString()))
                                                                                                                        .collect(toCollection(HashSet::new)));
	}

	@Getter
                    @AllArgsConstructor
	@ToString
	private static class GroupDetails{
                            private final String originCity;
                            private final String originZipCode;
                            private final double originRange;
                            private final String destinationCity;
                            private final String destinationZipCode;
                            private final double destinationRange;
	}
	
	@Getter
                    @AllArgsConstructor
	@ToString
	private static class Members {
                            private final HashSet<User> users;
	}
        
        	@Getter
                    @AllArgsConstructor
	@ToString
	private static class WaitingList {
                                private final HashSet<User> users;
	}
                
                    @Getter
                    @AllArgsConstructor
	@ToString
	private static class InvitationList {
                                private final HashSet<User> users;
	}
	
	@Getter
                    @AllArgsConstructor
	@ToString
	private static class User {
                                private ObjectId id;
                                private final String userName;
                                private final boolean isOwner;
                                private final String userId;
                                private final String  address;
	}
}


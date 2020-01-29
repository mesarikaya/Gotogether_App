import { GroupSearchResult } from './groupSearchResult';

export interface UserDetailsResult {
    subscribedGroups: GroupSearchResult[],
    invitationList: GroupSearchResult[]
}
import { SecurityState } from './system/securityState';
import { GroupSearchResult } from './userInterface/groupSearchResult';
import { GroupSearchFormFields } from './userInterface/groupSearchFormFields';
import { UserSearchResult } from './userInterface/userSearchResult';
import { GroupUser } from './userInterface/groupUser';
import { UserDetailsResult } from './userInterface/userDetailsResult';
import ResponseStatus from './userInterface/responseStatus';

export interface StoreState {
    system: SecurityState;
    userAccount: UserDetailsResult;
    groupSearchResults: {
        groups: GroupSearchResult[],
        page: number
    };
    userSearchResults: {
        users: UserSearchResult[],
        page: number
    };
    groupSearchFormFields: GroupSearchFormFields;
    selectedGroup: GroupSearchResult;
    currentSelectedMembers: {
        users: GroupUser[]
    };
    currentWaitingList: {
        users: GroupUser[]
    };
    responseStatus: ResponseStatus;
}
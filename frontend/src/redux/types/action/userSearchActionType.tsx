import { UserSearchResult } from '../userInterface/userSearchResult';

export const SEARCH_USER_REQUEST = 'SEARCH_USER_REQUEST';

interface SearchUserRequest {
    type: typeof SEARCH_USER_REQUEST
    payload: {
        users: UserSearchResult[],
        page: number
    }
}

export type UserSearchActionType = SearchUserRequest;
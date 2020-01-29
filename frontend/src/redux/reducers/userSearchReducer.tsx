import { UserSearchActionType, SEARCH_USER_REQUEST } from '../types/action/userSearchActionType';
import { UserSearchResult } from '../types/userInterface/userSearchResult';

// Set initial state
const initialState: {users: UserSearchResult[], page: number} = {
    users: [],
    page: 0
};
export function userSearchReducer(
    state = initialState,
    action: UserSearchActionType
    ): {users: UserSearchResult[], page:number} {

    switch (action.type) {
        case SEARCH_USER_REQUEST:
            return Object.assign({}, 
                                 state, 
                                 {
                                    users: {...action.payload.users},
                                    page: action.payload.page
                                 });
        default:
            return state;
    }
};
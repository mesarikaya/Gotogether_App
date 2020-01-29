import { GroupSearchResult } from '../../types/userInterface/groupSearchResult';
import { UPDATE_USER_ACCOUNT, UpdateUserAccountActionType } from '../../types/action/updateUserAccountActionType';
import { UserDetailsResult } from 'src/redux/types/userInterface/userDetailsResult';

// Set initial state
const initialState: {subscribedGroups: GroupSearchResult[], invitationList: GroupSearchResult[]} = {
    subscribedGroups: [],
    invitationList: []
};
export function updateUserAccountReducer(
    state = initialState,
    action: UpdateUserAccountActionType
    ): UserDetailsResult {

    switch (action.type) {

        case UPDATE_USER_ACCOUNT:
            return Object.assign({}, 
                                 state, 
                                 {
                                    subscribedGroups: [...action.payload.subscribedGroups],
                                    invitationList: [...action.payload.invitationList]
                                 });
        default:
            return state;
    }
};
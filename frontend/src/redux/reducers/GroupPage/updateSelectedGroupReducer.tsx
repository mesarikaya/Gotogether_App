import { UpdateSelectedGroupActionType, UPDATE_SELECTED_GROUP_REQUEST } from '../../../../src/redux/types/action/GroupPage/updateSelectedGroupActionType';
import { GroupSearchResult } from 'src/redux/types/userInterface/groupSearchResult';

// Set initial state
const initialState: GroupSearchResult = {
    id: '',
    name: '',
    groupDetails: {
        originCity: '',
        originRange: 2,
        originZipCode: '',
        destinationCity: '',
        destinationRange: 2,
        destinationZipCode: ''
    },
    members:{
        users:[]
    },
    waitingList: {
        users: []
    },
    invitationList: {
        users: []
    }
};
export function updateSelectedGroupReducer(
    state = initialState,
    action: UpdateSelectedGroupActionType): GroupSearchResult {

    switch (action.type) {
        case UPDATE_SELECTED_GROUP_REQUEST:
            return Object.assign({}, 
                state, 
                {
                    ...state,
                    id: action.payload.id,
                    name: action.payload.name,
                    groupDetails: action.payload.groupDetails,
                    members: {
                        users: [...action.payload.members.users]
                    },
                    waitingList: {
                        users: [...action.payload.waitingList.users]
                    },
                    invitationList: {
                        users: [...action.payload.invitationList.users]
                    }
                }
            );
        default:
            return state;
    }
};
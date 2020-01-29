import { combineReducers } from 'redux';
import { loginReducer } from './jwtAuthReducer';
import { userSearchReducer } from './userSearchReducer';
import { updateUserAccountReducer } from './UserPage/updateUserAccountReducer';
import { updateSelectedGroupReducer } from './GroupPage/updateSelectedGroupReducer';
import { updateGroupResultsReducer } from './updateGroupResultsReducer';
import { updateResponseStatusReducer } from './updateResponseStatusReducer';

export const rootReducer = combineReducers({
    system: loginReducer,
    userAccount: updateUserAccountReducer,
    groupSearchResults: updateGroupResultsReducer,
    userSearchResults: userSearchReducer,
    selectedGroup: updateSelectedGroupReducer,
    responseStatus: updateResponseStatusReducer
});

export type AppState = ReturnType<typeof rootReducer>


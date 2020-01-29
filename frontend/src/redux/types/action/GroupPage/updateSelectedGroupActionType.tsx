import { GroupSearchResult } from '../../userInterface/groupSearchResult';

export const UPDATE_SELECTED_GROUP_REQUEST = 'UPDATE_SELECTED_GROUP_REQUEST';

interface UpdateSelectedGroupRequest {
    type: typeof UPDATE_SELECTED_GROUP_REQUEST
    payload: GroupSearchResult
}

export type UpdateSelectedGroupActionType = UpdateSelectedGroupRequest;
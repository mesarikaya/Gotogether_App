import { GroupSearchResult } from '../userInterface/groupSearchResult';

export const UPDATE_GROUP_RESULTS = 'UPDATE_GROUP_RESULTS';

interface UpdateGroupResultsRequest {
    type: typeof UPDATE_GROUP_RESULTS
    payload: {
        groups: GroupSearchResult[],
        page: number
    }
}

export type GroupSearchActionType = UpdateGroupResultsRequest;
import { GroupSearchResult } from '../../userInterface/groupSearchResult';

export const DELETE_GROUP = 'DELETE_GROUP';

interface DeleteGroup {
    type: typeof DELETE_GROUP
    payload: {
        groups: GroupSearchResult[],
        page: number
    }
}

export const UPDATE_GROUP = 'UPDATE_GROUP';

interface UpdateGroup {
    type: typeof UPDATE_GROUP
    payload: {
        groups: GroupSearchResult[],
        page: number
    }
}
export type UpdateGroupActionType = DeleteGroup&UpdateGroup;
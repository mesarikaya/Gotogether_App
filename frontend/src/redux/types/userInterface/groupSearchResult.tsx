import { GroupUser } from './groupUser';

export interface GroupSearchResult {
    id: string,
    name: string,
    groupDetails: {
        originCity: string,
        originZipCode: string,
        originRange: number,
        destinationCity: string,
        destinationZipCode: string,
        destinationRange: number
    },
    members: {
        users: GroupUser[]
    },
    waitingList: {
        users: GroupUser[]
    },
    invitationList: {
        users: GroupUser[]
    }
}
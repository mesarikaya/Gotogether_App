import { UserDetailsResult } from '../userInterface/userDetailsResult';

export const UPDATE_USER_ACCOUNT = 'UPDATE_USER_ACCOUNT';

interface UpdateUserAccount {
    type: typeof UPDATE_USER_ACCOUNT
    payload: UserDetailsResult
}

export type UpdateUserAccountActionType = UpdateUserAccount;
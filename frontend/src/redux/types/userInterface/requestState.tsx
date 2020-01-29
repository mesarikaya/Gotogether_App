import ResponseStatus from './responseStatus';

export interface RequestState {
    isLoading: boolean;
    responseStatus: ResponseStatus;
}

export default RequestState;
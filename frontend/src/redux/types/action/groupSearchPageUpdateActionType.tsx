export const PAGE_NUMBER_UPDATE_REQUEST = 'PAGE_NUMBER_UPDATE_REQUEST';

interface PageNumberUpdateRequest {
    type: typeof PAGE_NUMBER_UPDATE_REQUEST
    page: number
}

export type GroupSearchPageUpdateActionType= PageNumberUpdateRequest;
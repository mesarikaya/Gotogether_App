import { UPDATE_RESPONSE_STATUS_REQUEST, UpdateResponseStatusActionType } from '../types/action/updateResponseStatusActionType';
import ResponseStatus from '../types/userInterface/responseStatus';

// Set initial state
const initialState: ResponseStatus = {
    type: "success",
    message: "Response has been successfully completed!"
};
export function updateResponseStatusReducer(
    state = initialState,
    action: UpdateResponseStatusActionType
    ): ResponseStatus {

    switch (action.type) {

        case UPDATE_RESPONSE_STATUS_REQUEST:
            return Object.assign({}, 
                                 state, 
                                 {
                                    type: action.payloads.type,
                                    message: action.payloads.message
                                 });
        default:
            return state;
    }
};
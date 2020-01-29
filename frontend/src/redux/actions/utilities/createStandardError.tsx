import { UpdateResponseStatusActionType } from 'src/redux/types/action/updateResponseStatusActionType';
import { Dispatch } from 'react';

export async function createStandardError(error: any, dispatch: Dispatch<UpdateResponseStatusActionType>): Promise<void> {

    let payloads = {
        type: "FAILURE",
        message: "Server error"
    };

    if(typeof error.response !== "undefined" && typeof error.response.status !== "undefined"){
        if (error.response.status === 401) {
            payloads = {
                type: "FAILURE",
                message: "Error: Wrong credentials or Unauthorized Action!"
            };
        }else if( typeof error.response.statusText!=="undefined"){
            payloads = {
                type: "FAILURE",
                message: error.response.statusText
            };
        }
    }

    return dispatch({ type: 'UPDATE_RESPONSE_STATUS_REQUEST', payloads });  
}

export default createStandardError;

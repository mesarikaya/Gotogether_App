import axios from "axios";
axios.defaults.withCredentials = true;
import { Dispatch } from "redux";
import { UpdateResponseStatusActionType } from '../types/action/updateResponseStatusActionType';
import ResponseStatus from '../types/userInterface/responseStatus';
import createStandardError from './utilities/createStandardError';

// Set the API url for back end calls
const url = process.env.NODE_ENV === 'production' ? "/api/auth/" : "http://localhost:8080/api/auth/";

/**
 * Validate verification token
 * @param e HTML Form Event
 * @param userName user id to verify
 * @param token verification token
 */
export function validateVerificationToken(params: any) {
    
    // tslint:disable-next-line: no-console
    console.log("Dispatching verification token with params:", params);
    return ((dispatch: Dispatch<UpdateResponseStatusActionType>) => {
        return (axios.get(`${url}verify/validate`, { 
            headers: {
                Accept: 'application/json',
                'Content-Type': 'application/json',
                Cache: "no-cache"
              },
              params,
              withCredentials: true
        }).then((response:any) => {
            
            let payloads: ResponseStatus = {
                type: "SUCCESS",
                message: "Request is in progress!"
            };

            // Depending on response status, allow or not for login
            if (response.status === 200) {

                const responseData = response.data;
                const responseMessage = responseData.message;
                const responseType = responseData.type;
                
                payloads = {
                    type: responseType,
                    message: responseMessage
                };

                return dispatch({ type: 'UPDATE_RESPONSE_STATUS_REQUEST', payloads });        
            }
            else {
                payloads = {
                    type: "FAILURE",
                    message: response.statusText
                };                

                return dispatch({ type: 'UPDATE_RESPONSE_STATUS_REQUEST', payloads });  
            }
        })
        .catch((error: any) => {
            createStandardError(error, dispatch);
        }));
    });
};
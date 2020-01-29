import axios from "axios";
axios.defaults.withCredentials = true;
import { Dispatch } from "redux";
import { RegistrationFormFields } from '../types/userInterface/registrationFormFields';;
import { UpdateResponseStatusActionType } from '../types/action/updateResponseStatusActionType';
import ResponseStatus from '../types/userInterface/responseStatus';
import createStandardError from './utilities/createStandardError';

// Set the API url for back end calls
const url = process.env.REACT_APP_NODE_ENV === 'production' ? "/api/auth/" : "http://localhost:8080/api/auth/";

/**
 * Make account registration request
 * @param e HTML Form Event
 * @param formFields Registration Form input data
 */
export function registerAccount(event: React.FormEvent<HTMLFormElement>, formFields: RegistrationFormFields) {
    
    if (event !== null) { event.preventDefault(); }

    // Set data to send with Post request
    const data = formFields;
    return ((dispatch: Dispatch<UpdateResponseStatusActionType>) => {
        return (axios.post(`${url}register`, data, { 
            headers: {
                Accept: 'application/json',
                'Content-Type': 'application/json',
                Cache: "no-cache"
              },
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
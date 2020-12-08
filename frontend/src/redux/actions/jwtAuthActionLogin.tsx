import axios from "axios";
axios.defaults.withCredentials = true;
import { Dispatch } from "redux";
import { JwtAuthActionTypes }from '../types/action/jwtAuthActionType';
import { LoginFormFields } from 'src/redux/types/userInterface/loginFormFields';
import { UpdateResponseStatusActionType } from '../types/action/updateResponseStatusActionType';
import { createStandardError } from './utilities/createStandardError';

// Set the API url for back end calls
const url = process.env.REACT_APP_NODE_ENV === 'production' ? "/api/auth/" : "/api/auth/";

/**
 * Make GET request and dipatch the image data to be shown via redux  
 * @param e HTML Form Event
 * @param formFields Login form input data
 */
export function UpdateAuth(event: React.FormEvent<HTMLFormElement>, formFields: LoginFormFields) {
   
    // tslint:disable-next-line: no-console
    console.log("Environment is: ", process.env.REACT_APP_NODE_ENV);
    if (event !== null) { event.preventDefault(); }

    // Set data to send with Post request
    const data = formFields;
    return ((dispatch: Dispatch<JwtAuthActionTypes|UpdateResponseStatusActionType>) => {
        return (axios.post(`${url}login`, data, { 
            headers: {
                Accept: 'application/json',
                'Content-Type': 'application/json',
                Cache: "no-cache"
              },
              withCredentials: true
            // TODO: Change the response type from any to a proper one soon
        }).then((response:any) => {
            
            let payload = { 
                cookie: "test cookies",
                loggedIn: false,
                userName: "guest",
                token: ''
             };
            // tslint:disable-next-line: no-console
            console.log("Status is:" + response.status);
            // Depending on response status, allow or not for login
            if (response.status === 200) {
                payload = {
                    cookie: "authenticated cookie",
                    loggedIn: true,
                    userName: response.data.username,
                    token: response.data.token
                };
                
                dispatch({ type: 'AUTH_REQUEST', payload });
                const payloads = {
                    type: "SUCCESS",
                    message: "Log in is successfull"
                };

                dispatch({ type: 'UPDATE_RESPONSE_STATUS_REQUEST', payloads });   
            }
            else {

                const payloads = {
                    type: "FAILURE",
                    message: response.statusText
                };                

                dispatch({ type: 'UPDATE_RESPONSE_STATUS_REQUEST', payloads });  
            }
        }).catch((error: any) => {
            createStandardError(error, dispatch); 
        }));
    });
};
import axios from "axios";
axios.defaults.withCredentials = true; // to make use of jwt
import { Dispatch } from "redux";
import { GroupSearchResult } from '../../types/userInterface/groupSearchResult';
import { UpdateUserAccountActionType } from '../../types/action/updateUserAccountActionType';
import { UserDetailsResult } from 'src/redux/types/userInterface/userDetailsResult';
import { GroupCreationFormFields } from 'src/redux/types/userInterface/groupCreationFormFields';
import createStandardError from '../utilities/createStandardError';
import { UpdateResponseStatusActionType } from 'src/redux/types/action/updateResponseStatusActionType';

// Set the API url for back end calls
const url = process.env.NODE_ENV === 'production' ? "/api/v1/" : "http://localhost:8080/api/auth/";

/**
 * Create Group for the user as owner
 * @param event Form submit event
 * @param formFields group creation fields
 * @param currentUserDetailsResult existing user account details
 * @param userId current userId
 * @param token token for security
 */
export function createGroup(event: React.FormEvent<HTMLFormElement>, 
                            formFields: GroupCreationFormFields,
                            currentUserDetailsResult: UserDetailsResult,
                            userId: string,
                            token: string) {
    
    if (event !== null) { 
        event.preventDefault();
    }
    
    // Set data to send with Post request
    const data = formFields;
    // tslint:disable-next-line: no-string-literal
    data["userId"] = userId;
    return ((dispatch: Dispatch<UpdateUserAccountActionType|UpdateResponseStatusActionType>) => {
        return (axios.post(`${url}user/group`, data, {
            headers: {
                Authorization: "Bearer " + token,
                Accept: 'application/json',
                'Content-Type': 'application/json',
                Cache: "no-cache"
            },
            withCredentials: true
        }).then((response) => {
            
            const initialState: UserDetailsResult = currentUserDetailsResult;
            let payload = initialState;
            if (response.status === 200) {
                if(Array.isArray(response.data) && response.data.length){
                    const newResponseData:GroupSearchResult[] = response.data;
                    Object.keys(newResponseData)
                          .map((key) => {
                                Object.keys(newResponseData[key].members.users).map((key2) => {
                                    const obj = newResponseData[key].members.users[key2];
                                    if (obj && !(Object.keys(obj).length === 0 && obj.constructor === Object)){
                                        if(obj.userId===userId){
                                            payload.subscribedGroups.push(JSON.parse(JSON.stringify(newResponseData[key])))
                                        }
                                    }
                                });
                                return true;
                            });
                }else{
                    payload = initialState;
                }
                
                dispatch({ type: 'UPDATE_USER_ACCOUNT', payload });        
            }else {
                // TODO: CREATE ERROR HANDLERS
                // tslint:disable-next-line:no-console
                console.log("Error in axios");                
                dispatch({ type: 'UPDATE_USER_ACCOUNT', payload });
            }
            // TODO: PUT THE RIGHT type for error inside the catch
        }).catch((error: any) => {
            createStandardError(error, dispatch);
        }));   
    });
};
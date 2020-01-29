import axios from "axios";
axios.defaults.withCredentials = true; // to make use of jwt
import { Dispatch } from "redux";
import { GroupSearchResult } from '../../types/userInterface/groupSearchResult';
import { UpdateUserAccountActionType } from '../../types/action/updateUserAccountActionType';
import { UserDetailsResult } from 'src/redux/types/userInterface/userDetailsResult';
import createStandardError from '../utilities/createStandardError';
import { UpdateResponseStatusActionType } from 'src/redux/types/action/updateResponseStatusActionType';

// Set the API url for back end calls
const url = process.env.REACT_APP_NODE_ENV === 'production' ? "/api/v1/" : "http://localhost:8080/api/v1/";

/**
 * Make GET request and dipatch the image data to be shown via redux  
 * @param e HTML Form Event
 * @param formFields Login form input data
 */
export function updateUserAccount(event: React.MouseEvent<HTMLButtonElement> | null, 
                                  userId: string,
                                  token: string) {
    
    if (event !== null) { 
        event.preventDefault();
    }
    
    // Set data to send with Post request
    const params = new URLSearchParams();
    params.append('userId', userId);
    return ((dispatch: Dispatch<UpdateUserAccountActionType|UpdateResponseStatusActionType>) => {
        return (axios.get(`${url}user`, {
            headers: {
                Authorization: "Bearer " + token,
                Accept: 'application/json',
                'Content-Type': 'application/json',
                Cache: "no-cache"
            },
            params,
            withCredentials: true
        }).then((response) => {
            
            const initialState: UserDetailsResult = {
                subscribedGroups: [],
                invitationList: []
            };

            let payload = initialState;
            const subscribedGroups:GroupSearchResult[] = [];
            const invitationList:GroupSearchResult[] = [];
            if (response.status === 200) {
                if(Array.isArray(response.data) && response.data.length){
                    const newResponseData:GroupSearchResult[] = response.data;
                    Object.keys(newResponseData)
                          .map((key) => {
                                Object.keys(newResponseData[key].members.users).map((key2) => {
                                    const obj = newResponseData[key].members.users[key2];
                                    if (obj && !(Object.keys(obj).length === 0 && obj.constructor === Object)){
                                        if(obj.userId===userId){
                                            // tslint:disable-next-line: no-console
                                            console.log("add to subscription: " +  subscribedGroups + "-" + key);
                                            subscribedGroups.push(newResponseData[key]);
                                        }
                                    }
                                });

                                Object.keys(newResponseData[key].invitationList.users).map((key2) => {
                                    const obj = newResponseData[key].invitationList.users[key2];
                                    if (obj && !(Object.keys(obj).length === 0 && obj.constructor === Object)){
                                        if(obj.userId===userId){
                                            // tslint:disable-next-line: no-console
                                            console.log("add to subscription: " +  invitationList + "-" + key);
                                            invitationList.push(newResponseData[key]);
                                        }
                                    }
                                });

                                return true;
                            });

                    payload={
                        subscribedGroups: JSON.parse(JSON.stringify(subscribedGroups)),
                        invitationList: JSON.parse(JSON.stringify(invitationList))
                    };

                    // tslint:disable-next-line: no-console
                    console.log("Payload of user account is: " + payload);
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
import axios from "axios";
axios.defaults.withCredentials = true; // to make use of jwt
import { Dispatch } from "redux";
import { GroupSearchResult } from '../../types/userInterface/groupSearchResult';
import { UpdateSelectedGroupActionType } from 'src/redux/types/action/GroupPage/updateSelectedGroupActionType';
import { UpdateResponseStatusActionType } from 'src/redux/types/action/updateResponseStatusActionType';
import createStandardError from '../utilities/createStandardError';

// Set the API url for back end calls
const url = process.env.NODE_ENV === 'production' ? "/api/v1/" : "http://localhost:8080/api/auth/";

/**
 * Update group members and update the store state
 * @param event 
 * @param currentGroup 
 * @param groupId 
 * @param userId 
 * @param token 
 * @param actionType
 */
export function updateGroupMember(event: React.MouseEvent<HTMLButtonElement> | null,
                                  currentGroup: GroupSearchResult,
                                  groupId: string,
                                  userId: string,
                                  token: string,
                                  actionType: 'add'|'delete') {

    if (event !== null) { 
        event.preventDefault();
    }
    
    return ((dispatch: Dispatch<UpdateSelectedGroupActionType|UpdateResponseStatusActionType>) => { 
        if (actionType === 'add'){
            addMember(currentGroup, groupId, userId, token, dispatch);
        }else if (actionType === 'delete'){
            deleteMember(currentGroup, groupId, userId, token, dispatch);
        }
    });
};

function addMember(currentGroup: GroupSearchResult, 
                   groupId: string, 
                   userId: string, 
                   token: string, 
                   dispatch: Dispatch<UpdateSelectedGroupActionType|UpdateResponseStatusActionType>){
    
    const data = {
        groupId,
        userId
    }

    return (axios.put(`${url}groups/members`, 
                data, 
                {headers: {
                    Authorization: "Bearer " + token,
                    Accept: 'application/json',
                    'Content-Type': 'application/json',
                    Cache: "no-cache"
                },
                withCredentials: true
            }).then((response) => {
                executeResponse(currentGroup, response, dispatch);
                // TODO: PUT THE RIGHT type for error inside the catch
    })
    .catch((error: any) => {
        createStandardError(error, dispatch);
    }));  
}

function deleteMember(currentGroup: GroupSearchResult, 
                      groupId: string, 
                      userId: string, 
                      token: string, 
                      dispatch: Dispatch<UpdateSelectedGroupActionType|UpdateResponseStatusActionType>){
    
    const params = new URLSearchParams();
    params.append('groupId', groupId);
    params.append('userId', userId);

    return (axios.delete(`${url}groups/members`, {
        headers: {
            Authorization: "Bearer " + token,
            Accept: 'application/json',
            'Content-Type': 'application/json',
            Cache: "no-cache"
        },
        params,
        withCredentials: true
    }).then((response) => {
        executeResponse(currentGroup, response, dispatch);
        // TODO: PUT THE RIGHT type for error inside the catch
    })
    .catch((error: any) => {
        createStandardError(error, dispatch);
    }));  
}

function executeResponse(currentGroup: GroupSearchResult, 
                         response: any, 
                         dispatch: Dispatch<UpdateSelectedGroupActionType>){

    const initialState: GroupSearchResult = currentGroup;
    let payload = initialState;

    // Depending on response status, allow or not for login
    if (response.status === 200) {
        const responseData = response.data;
        if(responseData){ 
            payload = responseData; 
        }else{
            payload = responseData; 
        }
        
        dispatch({ type: 'UPDATE_SELECTED_GROUP_REQUEST', payload });      
    }else {
        // TODO: CREATE ERROR HANDLERS
        // tslint:disable-next-line:no-console
        console.log("Error in axios");
        dispatch({ type: 'UPDATE_SELECTED_GROUP_REQUEST', payload });
    }
}
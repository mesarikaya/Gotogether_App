import axios from "axios";
axios.defaults.withCredentials = true; // to make use of jwt
import { Dispatch } from "redux";
import { GroupSearchResult } from '../../types/userInterface/groupSearchResult';
import { UpdateSelectedGroupActionType } from 'src/redux/types/action/GroupPage/updateSelectedGroupActionType';
import { UpdateResponseStatusActionType } from 'src/redux/types/action/updateResponseStatusActionType';
import createStandardError from '../utilities/createStandardError';

// Set the API url for back end calls
const url = process.env.REACT_APP_NODE_ENV === 'production' ? "/api/v1/" : "http://localhost:8080/api/v1/";

/**
 * Add the user to the group waitingList and update the store state
 * @param event 
 * @param currentGroup 
 * @param groupId
 * @param userId
 * @param token 
 */
export function updateWaitingList(event: React.MouseEvent<HTMLButtonElement>,
                                  currentGroup: GroupSearchResult,
                                  groupId: string,
                                  userId: string,
                                  token: string,
                                  actionType: 'add'|'delete',
                                  addToMemberList: true|false) {

    if (event !== null) { 
        event.preventDefault();
    }

    return ((dispatch: Dispatch<UpdateSelectedGroupActionType|UpdateResponseStatusActionType>) => {

        if(actionType==='add'){
            addToList(currentGroup, groupId, userId, token, dispatch);
        }else if (actionType==='delete'){
            deleteFromList(currentGroup, groupId, userId, token, dispatch, addToMemberList);
        }
    });
};

function addToList(currentGroup: GroupSearchResult,
                   groupId: string,
                   userId: string,
                   token: string,
                   dispatch: Dispatch<UpdateSelectedGroupActionType|UpdateResponseStatusActionType>) {

    const data = {
        userId,
        groupId
    };
    return (axios.put(`${url}groups/waitingList`, 
                data, 
                {headers: {
                    Authorization: "Bearer " + token,
                    Accept: 'application/json',
                    'Content-Type': 'application/json',
                    Cache: "no-cache"
                },
                withCredentials: true
            }).then((response) => {
                executeResponse(currentGroup, response, dispatch, false);
            })
            .catch((error: any) => {
                createStandardError(error, dispatch);
            }));
};

function deleteFromList(currentGroup: GroupSearchResult,
                        groupId: string,
                        userId: string,
                        token: string, 
                        dispatch: Dispatch<UpdateSelectedGroupActionType|UpdateResponseStatusActionType>,
                        addToMemberList: true|false) {

    const params = new URLSearchParams();
    params.append('groupId', groupId);
    params.append('userId', userId);
    
    if(addToMemberList){
        params.append('isMember', 'true');
    }else{
        params.append('isMember', 'false');
    }
    return (axios.delete(`${url}groups/waitingList`, {
        headers: {
            Authorization: "Bearer " + token,
            Accept: 'application/json',
            'Content-Type': 'application/json',
            Cache: "no-cache"
        },
        params,
        withCredentials: true
    }).then((response) => {
        executeResponse(currentGroup, response, dispatch, addToMemberList);
    }).catch((error: any) => {
        createStandardError(error, dispatch);
    }));
};

function executeResponse(currentGroup: GroupSearchResult, 
                         response: any, 
                         dispatch: Dispatch<UpdateSelectedGroupActionType>,
                         addToMemberList: true|false){

    const initialState: GroupSearchResult = currentGroup;
    let payload = initialState;
    // Depending on response status, allow or not for login
    if (response.status === 200) {
        const responseData = response.data;
        if(responseData){ 
            payload = responseData
        }else{
            payload = responseData
        }
        dispatch({ type: 'UPDATE_SELECTED_GROUP_REQUEST', payload });    
    }else {
        // TODO: CREATE ERROR HANDLERS
        // tslint:disable-next-line:no-console
        console.log("Error in axios");
        dispatch({ type: 'UPDATE_SELECTED_GROUP_REQUEST', payload });
    }
}
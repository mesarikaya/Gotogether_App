import axios from "axios";
axios.defaults.withCredentials = true; // to make use of jwt
import { Dispatch } from "redux";
import { GroupSearchResult } from '../../types/userInterface/groupSearchResult';
import { UpdateSelectedGroupActionType } from 'src/redux/types/action/GroupPage/updateSelectedGroupActionType';
import createStandardError from '../utilities/createStandardError';
import { UpdateResponseStatusActionType } from 'src/redux/types/action/updateResponseStatusActionType';

// Set the API url for back end calls
const url = process.env.NODE_ENV === 'production' ? "/api/v1/" : "/api/v1/";


/**
 * Get selected group and update the store state
 * @param event 
 * @param currentGroup 
 * @param groupId 
 * @param token 
 */
export function updateSelectedGroup(event: React.MouseEvent<HTMLDivElement, MouseEvent> | null,
                                    currentGroup: GroupSearchResult,
                                    groupId: string,
                                    token: string) {

    if (event !== null) { 
        event.preventDefault();
    }
    
    return ((dispatch: Dispatch<UpdateSelectedGroupActionType|UpdateResponseStatusActionType>) => { 
        getGroup(currentGroup, groupId, token, dispatch);
    });
};

function getGroup(currentGroup: GroupSearchResult, 
                  groupId: string,
                  token: string, 
                  dispatch: Dispatch<UpdateSelectedGroupActionType|UpdateResponseStatusActionType>){
    
    const params = new URLSearchParams();
    params.append('groupId', groupId);
    return (axios.get(`${url}groups/id`, {
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
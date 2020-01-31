import axios from "axios";
axios.defaults.withCredentials = true; // to make use of jwt
import { Dispatch } from "redux";
import { GroupSearchResult } from '../../types/userInterface/groupSearchResult';
import { GroupSearchActionType } from '../../types/action/groupSearchActionType';
import { UpdateResponseStatusActionType } from 'src/redux/types/action/updateResponseStatusActionType';
import createStandardError from '../utilities/createStandardError';

// Set the API url for back end calls
const url = process.env.NODE_ENV === 'production' ? "/api/auth/" : "http://localhost:8080/api/auth/";

 /**
  * Make GET request and dipatch the image data to be shown via redux 
  * @param event HTML Form Event
  * @param existingGroups Login form input data
  * @param groupId relevant group id
  * @param token securiy jwt token
  */
export function updateGroup(event: React.MouseEvent<HTMLButtonElement> | null, 
                            existingGroups: GroupSearchResult[],
                            groupId: string,
                            token: string) {
    
    if (event !== null) { 
        event.preventDefault();
    }
    
    // Set data to send with Post request
    const params = new URLSearchParams();
    params.append('groupId', groupId);
    return ((dispatch: Dispatch<GroupSearchActionType|UpdateResponseStatusActionType>) => {
        return (axios.delete(`${url}groups`, {
            headers: {
                Authorization: "Bearer " + token,
                Accept: 'application/json',
                'Content-Type': 'application/json',
                Cache: "no-cache"
            },
            params,
            withCredentials: true
        }).then((response) => {
            
            const initialState: {groups: GroupSearchResult[], page:number} = {
                groups: existingGroups,
                page: 0
            };

            let payload = initialState;

            // Depending on response status, allow or not for login
            if (response.status === 200) {
                if(Array.isArray(response.data) && response.data.length){
                    const newResponseData = response.data;
                    const prevGroups:GroupSearchResult[] = [];
                    for(const key in existingGroups){
                        if (existingGroups.hasOwnProperty(key)){
                            if(existingGroups[key].id!==groupId){
                                prevGroups.push(existingGroups[key]);
                            }
                        }
                    }
                   
                    Object.keys(newResponseData)
                          .map((key) => (prevGroups.push(newResponseData[key])));
                    payload ={
                        groups: JSON.parse(JSON.stringify(prevGroups)),
                        page: 0
                    }; 
                }else{
                    payload={
                        groups: existingGroups,
                        page: 0
                    }; 
                }
                dispatch({ type: 'UPDATE_GROUP_RESULTS', payload });        
            }else {
                // TODO: CREATE ERROR HANDLERS
                // tslint:disable-next-line:no-console
                console.log("Error in axios");
                dispatch({ type: 'UPDATE_GROUP_RESULTS', payload });
            }
            // TODO: PUT THE RIGHT type for error inside the catch
        })
        .catch((error: any) => {
            createStandardError(error, dispatch);
        }));   
    });
};
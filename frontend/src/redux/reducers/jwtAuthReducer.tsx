import { JwtAuthActionTypes, AUTH_REQUEST } from "../types/action/jwtAuthActionType";
import { SecurityState } from '../types/system/securityState';


// Set initial state
const initialState: SecurityState   = {
    cookie: "none",
    loggedIn: false,
    userName: "guest",
    token: ''
};

export function loginReducer(state = initialState, action: JwtAuthActionTypes): SecurityState {

    switch (action.type) {
        case AUTH_REQUEST:
            return Object.assign({}, state, {
                cookie: action.payload.cookie,
                loggedIn: action.payload.loggedIn,
                userName: action.payload.userName,
                token: action.payload.token
            });
        default:
            return state
    }
};
import { SecurityState } from "../system/securityState";

export const AUTH_REQUEST = 'AUTH_REQUEST';

interface AuthRequest {
    type: typeof AUTH_REQUEST
    payload: SecurityState
}

export type JwtAuthActionTypes = AuthRequest;
import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps } from "react-router";
import { Route, Switch, withRouter } from 'react-router-dom';

// Import the presentational components for this container
import App from './components/Page/App';
import GroupPage from './components/Page/GroupPage/GroupPage';
import UserPage from './components/Page/UserPage/UserPage';
import VerifyUser from './components/Page/VerifyUser';

// Import store and relevant types
import {store} from "../redux/store";
import { SecurityState } from '../redux/types/system/securityState';
import { GroupSearchResult } from 'src/redux/types/userInterface/groupSearchResult';
import { UserSearchResult } from 'src/redux/types/userInterface/userSearchResult';
import { GroupUser } from 'src/redux/types/userInterface/groupUser';
import { UserDetailsResult } from 'src/redux/types/userInterface/userDetailsResult';
import ResponseStatus from 'src/redux/types/userInterface/responseStatus';


interface AppProps {
    system: SecurityState;
    userAccount: UserDetailsResult;
    groupSearchResults: {
        groups: GroupSearchResult[],
        page: number
    };
    userSearchResults: {
        users: UserSearchResult[],
        page: number
    };
    selectedGroup:{
        members:{
            users: GroupUser[]
        },
        waitingList: {
            users: GroupUser[]
        },
        invites: {
            users: GroupUser[]
        }
    };
    responseStatus: ResponseStatus;
}

// These props are provided by the router
interface PathProps {
    history: any;
    location: any;
    match: any;
}
export interface State {
    system: SecurityState;
    userAccount: UserDetailsResult,
    groupSearchResults: {
        groups: GroupSearchResult[],
        page: number
    };
    userSearchResults: {
        users: UserSearchResult[],
        page: number
    };
    selectedGroup: GroupSearchResult;
    responseStatus: ResponseStatus;
};

class Container extends React.Component<AppProps & RouteComponentProps<PathProps>, State> {
    public state: State;

    constructor(props: AppProps & RouteComponentProps<PathProps>) {
        
        super(props);
        const currentState = store.getState();
        this.state = {
            system: currentState.system,
            userAccount: currentState.userAccount,
            groupSearchResults: currentState.groupSearchResults,
            userSearchResults: currentState.userSearchResults,
            selectedGroup: currentState.selectedGroupUser,
            responseStatus: currentState.responseStatus
        };
    }

    public render() {

        return (
            <Switch>
                <Route exact={true} path="/" component={App}/>
                <Route path="/app" component={App}/>
                <Route path="/group" component={GroupPage} />
                <Route path="/user" component={UserPage} />
                <Route path="/verify/validate/:userID/:verificationToken" component={VerifyUser} />
                <Route path="/**" component={App} />
            </Switch>
        );
    }
}

const mapStateToProps = (
    state: State, 
    OwnProps: AppProps&RouteComponentProps<PathProps>
    ) => ({
        system: state.system,
        userAccount: state.userAccount,
        groupSearchResults: state.groupSearchResults,
        userSearchResults: state.userSearchResults,
        selectedGroup: state.selectedGroup,
        responseStatus: state.responseStatus
    });  

export default withRouter(connect(mapStateToProps, null)(Container));
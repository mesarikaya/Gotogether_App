import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from "react-router";

// Add styling related imports
import '../../../../stylesheets/css/cards/GroupPage.css';

// import components
import UserSubscribedGroupsList from './UserSubscribedGroupsList';
import UserInvitationsList from './UserInvitationsList';
import GroupSearchForm from '../../Forms/GroupSearchForm';
import GroupSearchList from '../GroupPage/GroupSearchList';

// Import store and types
import { store } from 'src/redux/store';
import NavigationBar from '../../Navigation/NavigationBar';
import { LoginFormFields } from 'src/redux/types/userInterface/loginFormFields';
import { GroupSearchFormFields } from 'src/redux/types/userInterface/groupSearchFormFields';
import { updateUserAccount } from 'src/redux/actions/UserPage/updateUserAccountAction';
import { GroupSearchResult } from 'src/redux/types/userInterface/groupSearchResult';
import { updateGroupMember } from 'src/redux/actions/GroupPage/updateGroupMemberAction';
import { updateInvitationsList } from 'src/redux/actions/GroupPage/updateInvitationsListAction';
import { updateWaitingList } from 'src/redux/actions/GroupPage/updateWaitingListAction';
import { CardDeck, Button, Spinner } from 'react-bootstrap';
import { SearchGroups } from 'src/redux/actions/groupSearchAction';
import { updateSelectedGroup } from 'src/redux/actions/GroupPage/updateSelectedGroupAction';
import { UserDetailsResult } from 'src/redux/types/userInterface/userDetailsResult';
import { UpdateAuth } from 'src/redux/actions/jwtAuthActionLogin';
import { RegistrationFormFields } from 'src/redux/types/userInterface/registrationFormFields';
import { registerAccount } from 'src/redux/actions/registerAccountAction';
import { AppState } from 'src/redux/reducers/rootReducer';
import { sendAccountVerification } from 'src/redux/actions/sendAccountVerificationAction';
import { onLogoutUpdateAuth } from 'src/redux/actions/jwtAuthActionLogout';
import { SecurityState } from 'src/redux/types/system/securityState';
import CreateGroupForm from '../../Forms/CreateGroupForm';
import { createGroup } from 'src/redux/actions/UserPage/createGroupAction';
import { GroupCreationFormFields } from 'src/redux/types/userInterface/groupCreationFormFields';

// Set the default Props
export interface Props{
    isLoading: boolean;
    userAccount: UserDetailsResult;
    groupSearchFormFields: GroupSearchFormFields;
    onLoginSubmit: typeof UpdateAuth;
    onRegistrationSubmit: typeof registerAccount;
    loginFormFields: LoginFormFields;
    registrationFormFields: RegistrationFormFields;
    onSubmit: typeof SearchGroups;
    onUpdateGroupMember: typeof updateGroupMember;
    onUpdateGroupInvitationsList: typeof updateInvitationsList;
    onUpdateGroupWaitingList: typeof updateWaitingList;
    onGetUserAccountDetails: typeof updateUserAccount;
    updateSelectedGroup: typeof updateSelectedGroup;
    onVerificationSubmit: typeof sendAccountVerification;
    onCreateGroupSubmit: typeof createGroup;
    onLogout: typeof onLogoutUpdateAuth;
}

export interface State{
    isLoading: boolean;
    groupSearchFormFields: GroupSearchFormFields;
    storeState: AppState;
    userInfo: UserDetailsResult;
}

// These props are provided by the router
interface PathProps {
    history: any;
    location: any;
    match: any;
}

class UserPage extends React.Component<Props&RouteComponentProps<PathProps>, State>{

    public state: State;

    constructor(props:Props&RouteComponentProps<PathProps>){

        super(props);
        const currAppState:AppState = store.getState();
        this.state = {
            isLoading: false,
            groupSearchFormFields: {
                origin: '',
                originRange: 2,
                destination: '',
                destinationRange: 2
            },
            storeState: currAppState,
            userInfo: currAppState.userAccount
        };

        this.loadMore = this.loadMore.bind(this);
        this.handleGroupSearchFormUpdate = this.handleGroupSearchFormUpdate.bind(this);
    }

    public componentDidMount = async() => {
        const currAppState = store.getState();
        this.setState({isLoading: true});
        await Promise.all([this.props.onGetUserAccountDetails(null,
                                           currAppState.system.userName,
                                           currAppState.system.token)]);
        window.setTimeout(() =>{
            this.setState({isLoading: false});
        }, 500);
    }

    public componentDidUpdate(oldProps:Props&RouteComponentProps<PathProps>) {

        const currAppState = store.getState();
        if(this.state.storeState !== currAppState){
            this.setState({ 
                storeState: currAppState
            });
        }
    }

    public loadMore = async (event: any): Promise<void> => {
        this.isLoading(true);
        this.props.onSubmit(null, 
                            this.state.groupSearchFormFields, 
                            this.state.storeState.groupSearchResults.groups,
                            this.state.storeState.groupSearchResults.page,
                            this.state.storeState.system.token);
        this.isLoading(false);
    }

    public handleGroupSearchFormUpdate = (formFields: GroupSearchFormFields): void => {
        this.setState({groupSearchFormFields: formFields});
    }

    public isLoading(status: boolean) {
        this.setState({ isLoading: status });
    }

    public loadTable = (groupSearchResult: GroupSearchResult[]) => {
        if (this.state.isLoading===true){
            return (
                <div className="row justify-content-center">
                    <Spinner
                        as="span"
                        animation="grow" 
                        variant="warning"
                        size="sm"
                        role="status"
                        aria-hidden="true"
                    />
                <p>Processing</p>
            </div>);
        }else{
            if (Object.keys(this.state.storeState.groupSearchResults).length>0){
                    return (
                        <div>
                            <div className="row justify-content-center">
                                <CardDeck className="justify-content-center">
                                  <GroupSearchList key={this.state.storeState.system.userName + "_searchedgroups"} 
                                                   groupSearchResult={groupSearchResult}
                                                   userName={this.state.storeState.system.userName}
                                                   token={this.state.storeState.system.token}
                                                   onApplyGroup={this.props.onUpdateGroupWaitingList}
                                                   updateSelectedGroup={this.props.updateSelectedGroup}
                                   />
                                </CardDeck>
                            </div>
                            <div className="row justify-content-center">
                                {this.state.storeState.groupSearchResults.page!==0 ? 
                                  <Button className="pull-right mr-2" type="button" onClick={this.loadMore}> Load More... </Button>:
                                  null}
                            </div>
                        </div>
                    );
            }else{
                return <div className="row justify-content-center">No Records found</div>;
            }
        }
    }

    public render() {
        const groupSearchResult = this.state.storeState.groupSearchResults.groups;
        return (
            <div className="UserPage">
                <NavigationBar storeState={this.state.storeState}
                               loginFormFields={this.props.loginFormFields}
                               registrationFormFields={this.props.registrationFormFields} 
                               onLoginSubmit={this.props.onLoginSubmit}
                               onRegistrationSubmit={this.props.onRegistrationSubmit}
                               onGetUserAccountDetails={this.props.onGetUserAccountDetails}
                               onVerificationSubmit={this.props.onVerificationSubmit}
                               onLogout={this.props.onLogout}
                />
                <div className="container px-0 mx-auto pageContainer">
                    {this.state.isLoading ? 
                        <CardDeck key={this.state.storeState.system.userName}>
                                <UserSubscribedGroupsList key={this.state.storeState.system.userName+"_subs"}
                                                    subscribedGroups={this.state.storeState.userAccount.subscribedGroups}
                                                    userName={this.state.storeState.system.userName}
                                                    token={this.state.storeState.system.token}
                                                    onUpdateMember={this.props.onUpdateGroupMember}
                                                    updateSelectedGroup={this.props.updateSelectedGroup}
                                                    onGetUserAccountDetails={this.props.onGetUserAccountDetails}/>
                                <UserInvitationsList key={this.state.storeState.system.userName+"_invites"} 
                                                invitationsList={this.state.storeState.userAccount.invitationList}
                                                userName={this.state.storeState.system.userName}
                                                token={this.state.storeState.system.token}
                                                onUpdateMember={this.props.onUpdateGroupMember}
                                                onUpdateInvitationList={this.props.onUpdateGroupInvitationsList}
                                                updateSelectedGroup={this.props.updateSelectedGroup}
                                                onGetUserAccountDetails={this.props.onGetUserAccountDetails}/>
                        </CardDeck>:
                        <CardDeck className="justify-content-center">
                            <Spinner
                                as="span"
                                animation="grow" 
                                variant="warning"
                                size="sm"
                                role="status"
                                aria-hidden="true"
                            />
                            <p>Processing</p>
                        </CardDeck>
                    }
                </div>
                    
                <div className="container mx-auto my-auto align-items-center">
                    <div className="row justify-content-center">
                        <CreateGroupForm storeState={this.state.storeState} 
                                         onCreateGroupSubmit={this.props.onCreateGroupSubmit}
                                         onGetUserAccountDetails={this.props.onGetUserAccountDetails}
                        />
                    </div>  
                    <div className="row justify-content-center">
                        <strong>OR</strong>
                    </div>
                    <div className="row justify-content-center">
                        <div className="searchForm">                                  
                            <h1 className="joinAGroupText text-center">Join a group?</h1>
                            <p className="joinAGroupSubText text-center">
                                Search with ease based on the origin and destination radius
                            </p>
                            <GroupSearchForm 
                                formFields={this.state.groupSearchFormFields}
                                page={this.state.storeState.groupSearchResults.page}
                                token={this.state.storeState.system.token} 
                                updateSearchFormFields={this.handleGroupSearchFormUpdate}
                                onSubmit={this.props.onSubmit}
                                // tslint:disable-next-line: jsx-no-lambda
                                isLoading={(status:boolean) => this.isLoading(status)}
                            />
                        </div>
                    </div>
                </div>

                <br/>

                <div className="container mx-auto my-auto p-2">
                    {this.loadTable(groupSearchResult)}
                </div>

            </div>
        );
    }
}

// Create mapToState and mapDispatch for Redux
const mapStateToProps = (
    state: State, 
    OwnProps: Props&RouteComponentProps<PathProps>
    ) => {
    return {
        storeState: state.storeState
    }
}

const mapDispatchToProps = (dispatch: any) => {
    return {
        onLoginSubmit: (
            e: React.FormEvent<HTMLFormElement>, 
            formFields: LoginFormFields
        ) => dispatch(UpdateAuth(e, formFields)),
        onRegistrationSubmit: (
            e: React.FormEvent<HTMLFormElement>, 
            formFields: RegistrationFormFields
        ) => dispatch(registerAccount(e, formFields)),
        onSubmit: (
            e: React.FormEvent<HTMLFormElement>, 
            formFields: GroupSearchFormFields,
            existingUsers: GroupSearchResult[],
            page: number,
            token: string,
        ) => dispatch(SearchGroups(e, formFields, existingUsers, page, token)),
        onGetUserAccountDetails: (
            event: React.MouseEvent<HTMLButtonElement> | null,
            userId: string,
            token: string
        ) => dispatch(updateUserAccount(event, userId, token)),
        onUpdateGroupMember: (
            e: React.MouseEvent<HTMLButtonElement>,
            currentGroup: GroupSearchResult,
            groupId: string,
            userId: string,
            token: string,
            actionType: 'add'|'delete'
        ) => dispatch(updateGroupMember(e, currentGroup, groupId, userId, token, actionType)),
        onUpdateGroupInvitationsList: (
            e: React.MouseEvent<HTMLButtonElement>,
            currentGroup: GroupSearchResult,
            groupId: string,
            userId: string,
            token: string,
            actionType: 'add'|'delete'
        ) => dispatch(updateInvitationsList(e, currentGroup, groupId, userId, token, actionType)),
        onUpdateGroupWaitingList: (
            e: React.MouseEvent<HTMLButtonElement>,
            currentGroup: GroupSearchResult,
            groupId: string,
            userId: string,
            token: string,
            actionType: 'add'|'delete',
            addToMembers: true|false
        ) => dispatch(updateWaitingList(e, currentGroup, groupId, userId, token, actionType, addToMembers)),
        updateSelectedGroup: (
            event: React.MouseEvent<HTMLTableCellElement, MouseEvent>,
            currentGroup: GroupSearchResult,
            groupId: string,
            token: string) => dispatch(updateSelectedGroup(event, currentGroup, groupId, token)),
        onVerificationSubmit: (
            e: React.FormEvent<HTMLFormElement>, 
            formFields: {userName: string}
        ) => dispatch(sendAccountVerification(e, formFields)),
        onLogout: (
            event: React.MouseEvent<HTMLButtonElement, MouseEvent>,
            currentSecurityState: SecurityState) => dispatch(onLogoutUpdateAuth(event, currentSecurityState)),
        onCreateGroupSubmit: (
            event: React.FormEvent<HTMLFormElement>, 
            formFields: GroupCreationFormFields,
            currentUserDetailsResult: UserDetailsResult,
            userId: string,
            token: string) => dispatch(createGroup(event, formFields, currentUserDetailsResult, userId, token))
    }
}

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(UserPage));

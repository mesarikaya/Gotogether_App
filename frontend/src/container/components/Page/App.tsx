import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from "react-router";

// Add styling related imports
import '../../../stylesheets/css/App.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'font-awesome/css/font-awesome.min.css';
import carouselPart1 from '../../../stylesheets/images/carouselImagePart1.png';
import carouselPart2 from '../../../stylesheets/images/carouselImagePart2.png';
import carouselPart3 from '../../../stylesheets/images/carouselImagePart3.png';
import { Carousel, Button, CardDeck, Spinner} from 'react-bootstrap';

// import child component
import GroupSearchForm from '../Forms/GroupSearchForm';
import NavigationBar from '../Navigation/NavigationBar';
import { LoginFormFields } from '../../../redux/types/userInterface/loginFormFields';
import GroupCard from './GroupPage/GroupCard';

// import types
import { GroupSearchFormFields } from '../../../redux/types/userInterface/groupSearchFormFields';
import { store } from '../../../redux/store';
import { SecurityState } from '../../../redux/types/system/securityState';
import { SearchGroups } from '../../../redux/actions/groupSearchAction';
import { GroupSearchResult } from '../../../redux/types/userInterface/groupSearchResult';
import { updateSelectedGroup } from '../../../redux/actions/GroupPage/updateSelectedGroupAction';
import { updateUserAccount } from 'src/redux/actions/UserPage/updateUserAccountAction';
import { UpdateAuth } from 'src/redux/actions/jwtAuthActionLogin';
import { RegistrationFormFields } from 'src/redux/types/userInterface/registrationFormFields';
import { registerAccount } from 'src/redux/actions/registerAccountAction';
import { AppState } from 'src/redux/reducers/rootReducer';
import { sendAccountVerification } from '../../../redux/actions/sendAccountVerificationAction';
import { onLogoutUpdateAuth } from 'src/redux/actions/jwtAuthActionLogout';

export interface Props {
    isLoading: boolean;
    storeState: AppState;
    system: SecurityState;
    loginFormFields: LoginFormFields;
    registrationFormFields: RegistrationFormFields;
    groupSearchFormFields: GroupSearchFormFields;
    groupSearchResults: {
        groups: GroupSearchResult[],
        page: number
    };
    onLoginSubmit: typeof UpdateAuth;
    onRegistrationSubmit: typeof registerAccount;
    onSubmit: typeof SearchGroups;
    onVerificationSubmit: typeof sendAccountVerification;
    onGetUserAccountDetails: typeof updateUserAccount;
    updateSelectedGroup: typeof updateSelectedGroup;
    updateGroupSearchFormFields: (formFields: GroupSearchFormFields) => void;
    onLogout: typeof onLogoutUpdateAuth;
};

export interface State {
    isLoading: boolean;
    storeState: AppState;
    groupSearchResults: {
        groups: GroupSearchResult[],
        page: number
    };
    groupSearchFormFields: GroupSearchFormFields;
};

// These props are provided by the router
interface PathProps {
    history: any;
    location: any;
    match: any;
}

class App extends React.Component<Props & RouteComponentProps<PathProps>, State> {
    
    public state: State;
    constructor(props: Props & RouteComponentProps<PathProps>) {
        
        super(props);
        const currentState:AppState = store.getState();
        this.state = {
            isLoading: false,
            storeState: currentState,
            groupSearchResults: {groups:[], page:0},
            groupSearchFormFields: {
              origin: '',
              originRange: 2,
              destination: '',
              destinationRange: 2
            }
        };

        this.loadMore = this.loadMore.bind(this);
        this.handleGroupSearchFormUpdate = this.handleGroupSearchFormUpdate.bind(this);
    }

    public loadMore = async (event: any): Promise<void> => { 
        this.isLoading(true); 
        this.props.onSubmit(null, 
                            this.state.groupSearchFormFields, 
                            this.state.groupSearchResults.groups,
                            this.state.groupSearchResults.page,
                            this.state.storeState.system.token);
        this.isLoading(false);                           
    }

    public handleGroupSearchFormUpdate = (formFields: GroupSearchFormFields): void => {
        this.setState({
            groupSearchFormFields: formFields
        });
    }

    public componentDidUpdate(oldProps: Props) {
        const newProps = this.props;
        const currAppState = store.getState();
        if(oldProps.system !== newProps.system 
            || oldProps.groupSearchResults !== newProps.groupSearchResults
            || this.state.storeState !== currAppState) {
            this.setState({ 
                storeState:currAppState,
                groupSearchResults:this.props.groupSearchResults
            });
        }
    }

    public isLoading(status: boolean) {
        this.setState({ isLoading: status });
    }

    public loadTable = (groupSearchResult: GroupSearchResult[]) => {
        if (this.state.isLoading===true){
            return (
                <div>
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
            if (Object.keys(this.state.groupSearchResults).length>0){
                    return (
                        <div>
                            <div className="row justify-content-center">
                            <CardDeck key="GroupSearchCardDeck">
                                {Object.keys(groupSearchResult).map((key) => (
                                    <GroupCard 
                                        key={groupSearchResult[key].id} 
                                        groupIndex={key}
                                        name={groupSearchResult[key].name} 
                                        groupDetails={groupSearchResult[key].groupDetails}
                                        members={groupSearchResult[key].members}
                                        group={groupSearchResult[key]}
                                        token={this.state.storeState.system.token}
                                        updateSelectedGroup={this.props.updateSelectedGroup}
                                    />
                                ))}
                            </CardDeck>
                            </div>
                            <div className="row justify-content-end">
                                {this.state.groupSearchResults.page!==0 ? 
                                <Button className="pull-right mr-2" type="button" onClick={this.loadMore}> Load More... </Button>:
                                null}
                            </div>
                    </div>);
            }else{
                return <div className="row justify-content-center">No Records found</div>;
            }
        }
    }

    public render() {

        const groupSearchResult = this.state.groupSearchResults.groups;
        return (
          <div className="App">
            <NavigationBar storeState={this.state.storeState}
                           loginFormFields={this.props.loginFormFields} 
                           registrationFormFields={this.props.registrationFormFields}
                           onLoginSubmit={this.props.onLoginSubmit}
                           onRegistrationSubmit={this.props.onRegistrationSubmit}
                           onGetUserAccountDetails={this.props.onGetUserAccountDetails}
                           onVerificationSubmit={this.props.onVerificationSubmit}
                           onLogout={this.props.onLogout}
            />
            <div className="container pageContainer">
                <div className="row align-items-center">
                    <div className="col-xs-12 col-sm-12 col-md-12 col-lg-8 col-xl-7 my-auto">  
                        <div className="mx-auto my-auto">
                            <Carousel className="openingCarousel"
                                interval={5000}
                                nextIcon={<span aria-hidden="true"/>}
                                prevIcon={<span aria-hidden="true"/>}
                                fade={true}>
                                <Carousel.Item>
                                    <img
                                    className="carouselImage d-block w-100"
                                    src={carouselPart1}
                                    alt="First slide"
                                    />
                                </Carousel.Item>
                                <Carousel.Item>
                                    <img
                                    className="carouselImage d-block w-100"
                                    src={carouselPart2}
                                    alt="Third slide"
                                    />
                                </Carousel.Item>
                                <Carousel.Item>
                                    <img
                                    className="carouselImage d-block w-100"
                                    src={carouselPart3}
                                    alt="Third slide"
                                    />
                                </Carousel.Item>
                            </Carousel>
                        </div>
                    </div>

                    <div className="col-xs-12 col-sm-12 col-md-12 col-lg-4 col-xl-5 my-auto"> 
                        <div className="container mx-auto my-auto">
                            <div className="searchFormRow mx-auto my-auto">
                                <div className="searchForm">                                  
                                    <h1 className="joinAGroupText text-center">Join a group?</h1>
                                    <p className="joinAGroupSubText text-center">Search with ease based on the origin and destination radius</p>
                                
                                    <GroupSearchForm 
                                        formFields={this.state.groupSearchFormFields}
                                        page={this.props.groupSearchResults.page}
                                        token={this.state.storeState.system.token} 
                                        updateSearchFormFields={this.handleGroupSearchFormUpdate}
                                        onSubmit={this.props.onSubmit}
                                        // tslint:disable-next-line: jsx-no-lambda
                                        isLoading={(status:boolean) => this.isLoading(status)}
                                    />
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <br />
                
                <div className="container"> 
                    {this.loadTable(groupSearchResult)}
                </div>
            </div>
        </div>);
  }
}

// Create mapToState and mapDispatch for Redux
const mapStateToProps = (
    state: State, 
    OwnProps: Props & RouteComponentProps<PathProps>
    ) => {
    return {
        storeState: state.storeState,
        groupSearchResults: state.groupSearchResults
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
        onVerificationSubmit: (
            e: React.FormEvent<HTMLFormElement>, 
            formFields: {userName: string}
        ) => dispatch(sendAccountVerification(e, formFields)),
        onSubmit: (
            e: React.FormEvent<HTMLFormElement>, 
            formFields: GroupSearchFormFields,
            existingGroups: GroupSearchResult[],
            page: number,
            token: string) => dispatch(SearchGroups(e, formFields, existingGroups, page, token)),
        onGetUserAccountDetails: (
            event: React.MouseEvent<HTMLButtonElement> | null,
            userId: string,
            token: string
        ) => dispatch(updateUserAccount(event, userId, token)),
        updateSelectedGroup: (
            event: React.MouseEvent<HTMLDivElement, MouseEvent>,
            currentGroup: GroupSearchResult,
            groupId: string,
            token: string) => dispatch(updateSelectedGroup(event, currentGroup, groupId, token)),
        onLogout: (
            event: React.MouseEvent<HTMLButtonElement, MouseEvent>,
            currentSecurityState: SecurityState) => dispatch(onLogoutUpdateAuth(event, currentSecurityState))
        }
}

export  default withRouter(connect(mapStateToProps, mapDispatchToProps)(App));

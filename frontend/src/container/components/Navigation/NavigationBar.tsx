import * as React from 'react';
import { withRouter } from 'react-router-dom';
import { Button } from 'react-bootstrap';
import Logo from '../../../../src/stylesheets/images/logo.svg';

// import components
import LogoutButton from '../Authentication/LogoutButton';
import LoginOrRegister from '../Authentication/LoginOrRegister';

// Add styling related imports
import '../../../stylesheets/css/App.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'font-awesome/css/font-awesome.min.css';
import '../../../stylesheets/css/NavBar.css';

// Add types
import { LoginFormFields } from '../../../redux/types/userInterface/loginFormFields';
import { updateUserAccount } from '../../../redux/actions/UserPage/updateUserAccountAction';
import { UpdateAuth } from '../../../redux/actions/jwtAuthActionLogin';
import { RegistrationFormFields } from 'src/redux/types/userInterface/registrationFormFields';
import { registerAccount } from 'src/redux/actions/registerAccountAction';
import { AppState } from 'src/redux/reducers/rootReducer';
import { sendAccountVerification } from 'src/redux/actions/sendAccountVerificationAction';
import { onLogoutUpdateAuth } from 'src/redux/actions/jwtAuthActionLogout';

export interface Props {
    storeState: AppState
    loginFormFields: LoginFormFields;
    registrationFormFields: RegistrationFormFields;
    onGetUserAccountDetails: typeof updateUserAccount;
    onLoginSubmit: typeof UpdateAuth;
    onRegistrationSubmit: typeof registerAccount;
    onVerificationSubmit: typeof sendAccountVerification;
    onLogout: typeof onLogoutUpdateAuth;
};

// These props are provided by the router
interface PathProps {
    history: any;
    location: any;
    match: any;
}

class NavigationBar extends React.Component<Props&PathProps> {
    
    constructor(props: Props&PathProps) {
        super(props);
        this.handleGoToUserPageClick = this.handleGoToUserPageClick.bind(this);
    }

    public handleGoToUserPageClick = async (event: React.MouseEvent<HTMLButtonElement, MouseEvent>): Promise<void> =>{
        event.preventDefault();
        this.props.history.push('/user');
    }

    public renderButton = () => {

        const isLoggedIn = this.props.storeState.system.loggedIn;
        if (!isLoggedIn) {
            return  <LoginOrRegister storeState={this.props.storeState}
                                     loginFormFields={this.props.loginFormFields}
                                     registrationFormFields={this.props.registrationFormFields}
                                     onLoginSubmit={this.props.onLoginSubmit}
                                     onRegistrationSubmit={this.props.onRegistrationSubmit}
                                     onGetUserAccountDetails={this.props.onGetUserAccountDetails}
                                     onVerificationSubmit={this.props.onVerificationSubmit}
                    />
        } else{
            return <LogoutButton storeState={this.props.storeState} 
                                 onLogout={this.props.onLogout}/>
        }; 
    }

    public render() {

        return (
             <React.Fragment>
                {/* <!- Navigation Bar --> */}
                <nav className="navbar fixed-top">
                    <div className="container">
                        {/* <!- Search Form --> */}
                        <div className="navRow row box">
                            <div className="col-12 col-sm-5 branding">
                                <a href="/" className="navbar-brand" >
                                    <img className="img-fluid rounded-circle img_logo App-logo"
                                    src={Logo} alt="" style={{ maxWidth: '3rem', height: '3rem' }} />
                                    <strong style={{fontSize: '1.4rem'}}>C</strong>ommute
                                    <strong style={{fontSize: '1.4rem'}}>W</strong>ith
                                    <strong style={{fontSize: '1.4rem'}}>M</strong>ore
                                </a>
                            </div>
                            
                            <div className="col-12 col-sm-7 navButtonGroup">
                                {this.renderButton()}
                                <Button className="navButton aboutButton" 
                                        variant="link" 
                                        onClick={this.handleGoToUserPageClick}>
                                    <i className="fas fa-user">
                                        <strong id="icons"> My Account</strong>
                                    </i>
                                </Button>
                                <Button className="navButton aboutButton" variant="link">
                                    <i className="fas fa-info">
                                        <strong id="icons"> About</strong>
                                    </i>
                                </Button>
                            </div>
                        </div>
                    </div>
                </nav>
            </React.Fragment>
        );
  }
}

export default withRouter(NavigationBar);

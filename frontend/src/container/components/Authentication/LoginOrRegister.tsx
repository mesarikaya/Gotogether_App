import * as React from 'react';

// import components
import VerificationForm from './VerificationForm';
import LoginForm from './LoginForm';
import RegistrationForm from './RegistrationForm';

// Add styling related imports
import '../../../stylesheets/css/App.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'font-awesome/css/font-awesome.min.css';
import '../../../stylesheets/css/login.css';

// Add types
import { UpdateAuth } from '../../../redux/actions/jwtAuthActionLogin';
import { updateUserAccount } from '../../../redux/actions/UserPage/updateUserAccountAction';
import { LoginFormFields } from '../../../redux/types/userInterface/loginFormFields';
import { Tabs, Tab, Button, Modal } from 'react-bootstrap';
import { RegistrationFormFields } from 'src/redux/types/userInterface/registrationFormFields';
import { registerAccount } from 'src/redux/actions/registerAccountAction';
import { AppState } from 'src/redux/reducers/rootReducer';
import { sendAccountVerification } from 'src/redux/actions/sendAccountVerificationAction';


export interface Props {   
    storeState: AppState;
    loginFormFields: LoginFormFields;
    registrationFormFields: RegistrationFormFields;
    onLoginSubmit: typeof UpdateAuth;
    onRegistrationSubmit: typeof registerAccount;
    onGetUserAccountDetails: typeof updateUserAccount;
    onVerificationSubmit: typeof sendAccountVerification;
};

export interface State {
    isLoading: boolean;
    loginFormFields: LoginFormFields;
    show: boolean;
};

class LoginOrRegister extends React.Component<Props, State> {
    
    public state: State;

    constructor(props: Props) {
        super(props);

        this.state = {
            isLoading: false,
            loginFormFields: {
                userName: '',
                password: '',
                rememberMe: false,
                validated: false
            },
            show: false
        };

        this.handleModalShow = this.handleModalShow.bind(this);
        this.handleModalClose = this.handleModalClose.bind(this);
        this.handleRememberMeChange = this.handleRememberMeChange.bind(this);
        this.handleChange = this.handleChange.bind(this);
    }

    public handleModalClose() {
        this.setState({ show: false });
    }

    public setModalClose(status: boolean) {
        this.setState({ show: status });
    }

    public handleModalShow() {
        this.setState({ show: true });
    }

    public handleRememberMeChange(checked: boolean) {
        this.setState({ 
            loginFormFields: {
                ...this.state.loginFormFields,
                rememberMe: checked
            }
        });
    }

    // TODO: Need to FIND A WAY TO USE THE RIGHT TYPE WITHOUT ERROR React.FormEvent<HTMLInputElement>
    public handleChange = async (event: any): Promise<void> => {

        // read the form input fields
        const loginFormFields = { ...this.state.loginFormFields };
        loginFormFields[event.currentTarget.name] = event.currentTarget.value;
        this.setState({
            loginFormFields
        });
    }

    public render() {

        return (
          <React.Fragment>
            <Button className="navButton signInButton" variant="link" onClick={this.handleModalShow} >
                <i className="fas fa-sign-in-alt">
                    <strong id="icons"> Log in/Register</strong>
                </i>
            </Button>
            
            <Modal dialogClassName={"modalDialog"} 
                   show={this.state.show} 
                   onHide={this.handleModalClose}>
                  <Modal.Header className={"modalHeader"} closeButton={true} />
                  <Modal.Body>
                      <div className="container mb-2 modalContainer">
                          <div className="row-fluid">
                            <Tabs defaultActiveKey="login" transition={false} id="loginOrRegisterTab" className="mx-auto">
                                <Tab eventKey="login" title="Login" id="loginTab">
                                    <LoginForm isLoading={false}
                                               storeState={this.props.storeState}
                                               loginFormFields={this.props.loginFormFields}
                                               onLoginSubmit={this.props.onLoginSubmit}
                                               onGetUserAccountDetails={this.props.onGetUserAccountDetails}
                                               // tslint:disable-next-line: jsx-no-lambda
                                               onModalStateSet={(status:boolean) => this.setModalClose(status)}
                                    />
                                </Tab>
                                <Tab eventKey="register" title="Register" id="registerTab">
                                    <RegistrationForm isLoading={false}
                                                      storeState={this.props.storeState}
                                                      registrationFormFields={this.props.registrationFormFields}
                                                      onRegistrationSubmit={this.props.onRegistrationSubmit}
                                    />                    
                                </Tab>
                                <Tab eventKey="contact" title="Contact" id="contactTab">
                                    <VerificationForm isLoading={false}
                                                      storeState={this.props.storeState}
                                                      onVerificationSubmit={this.props.onVerificationSubmit}
                                                      // tslint:disable-next-line: jsx-no-lambda
                                                      onModalStateSet={(status:boolean) => this.setModalClose(status)}
                                    />
                                </Tab>
                            </Tabs>
                        </div>
                    </div>
                </Modal.Body>
            </Modal>
          </React.Fragment>
        );
  }
}

export default LoginOrRegister;

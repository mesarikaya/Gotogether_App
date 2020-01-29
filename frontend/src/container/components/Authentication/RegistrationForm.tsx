import * as React from 'react';

// Add styling related imports
import '../../../stylesheets/css/App.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'font-awesome/css/font-awesome.min.css';
import '../../../stylesheets/css/login.css';
import { Form, InputGroup, Col, Button, Spinner } from 'react-bootstrap';

// Add types
import {RegistrationFormFields} from '../../../redux/types/userInterface/registrationFormFields'
import { registerAccount } from 'src/redux/actions/registerAccountAction';
import { AppState } from 'src/redux/reducers/rootReducer';

export interface Props{
    isLoading: boolean;
    storeState: AppState;
    registrationFormFields: RegistrationFormFields;
    onRegistrationSubmit: typeof registerAccount;
}

export interface State{
    isLoading: boolean;
    showResponseStatus: boolean;
    registrationFormFields: RegistrationFormFields; 
}

class RegistrationForm extends React.Component<Props, State> {

    public state: State;

    constructor(props: Props){
        
        super(props);

        this.state={
            isLoading: false,
            showResponseStatus: false,
            registrationFormFields:{
                firstName: '',
                middleName: '',
                lastName: '',
                email: '',
                password: '',
                confirmPassword: '',
                streetName: '',
                houseNumber: '',
                city: '',
                zipcode: '',
                state: '',
                country: '',
                validated: false,
                hasPasswordMatch: false,
            }
        };
    }

    public handleChange = async (event: any): Promise<void> => {

        // read the form input fields
        const registrationFormFields = { ...this.state.registrationFormFields };
        registrationFormFields[event.currentTarget.name] = event.currentTarget.value;
        const password = registrationFormFields.password;
        const confirmPassword = registrationFormFields.confirmPassword;
        registrationFormFields.hasPasswordMatch = password.localeCompare(confirmPassword)===0 && password !== "" && confirmPassword !== ""
        
        this.setState({
            registrationFormFields
        });
    }

    public handleSubmit = async (event: React.FormEvent<HTMLFormElement>): Promise<void> => {

        // TODO: Add button disable
        const form = event.currentTarget;
        if (form.checkValidity() === false) {
            event.preventDefault();
            event.stopPropagation();
        }

        this.setState({ 
            registrationFormFields: {
                    ...this.state.registrationFormFields,
                    validated: true
                },
            isLoading: true,
            showResponseStatus: false
        }); 

        if(this.state.registrationFormFields.hasPasswordMatch){
            this.setState({ registrationFormFields: {
                ...this.state.registrationFormFields,
                validated: true,
            }});
            this.props.onRegistrationSubmit(event, this.state.registrationFormFields);
        }

        window.setTimeout(() =>{
            this.setState({
                isLoading: false,
            });
        }, 3000);

        this.setState({
            showResponseStatus: true
        });

        window.setTimeout(() =>{
            this.setState({
                showResponseStatus: false
            });
        }, 5000);
    };

    public render(){

        const validated = this.state.registrationFormFields.validated;
        const isLoading = this.state.isLoading;
        const responseStatus = this.props.storeState.responseStatus;
        const hasPasswordMatch = this.state.registrationFormFields.hasPasswordMatch;
        return (
            <React.Fragment>
                <div className = "container mb-2 modalContainer mx-auto registrationFormContainer">
                    <div className = "row-fluid">
                        {responseStatus.type==="SUCCESS" && this.state.showResponseStatus ? 
                            <p className="responseSuccess mt-1 mb-0"> {responseStatus.message} </p> : null}
                            
                        {responseStatus.type==="FAILURE" && this.state.showResponseStatus ? 
                            <p className="responseFailure mt-1 mb-0"> {responseStatus.message} </p> : null}

                        <Form name = "registrationForm"
                              className = "needsLoginFormValidation"
                              noValidate = {true}
                              validated = {validated}
                              onSubmit = {this.handleSubmit}>

                            <Form.Row className="mt-2">
                                <Col>
                                    <InputGroup className="LoginFormInputGroup">
                                        <Form.Control
                                            required={true}
                                            type="email"
                                            id={"registrationEmail"}
                                            name={"email"}
                                            placeholder="email address"
                                            onChange={this.handleChange}
                                        />
                                        <InputGroup.Append>
                                            <InputGroup.Text>
                                                <i className="fas fa-user-shield" />
                                            </InputGroup.Text>
                                        </InputGroup.Append>
                                        <Form.Control.Feedback type="invalid"
                                            className="invalid-feedback usernameError text-center">
                                            Invalid username!
                                        </Form.Control.Feedback>
                                    </InputGroup>
                                </Col>
                            </Form.Row>

                            <Form.Row className="mt-1">
                                <Col>
                                    <InputGroup  >
                                        <Form.Control
                                            required={true}
                                            type="password"
                                            id={"registrationPassword"}
                                            name={"password"}
                                            placeholder="password"
                                            onChange={this.handleChange}
                                        />
                                        <InputGroup.Append>
                                            <div className="input-group-text">
                                                <i className="far fa-eye" />
                                            </div>
                                        </InputGroup.Append>
                                        <Form.Control.Feedback type="invalid"
                                            className="invalid-feedback passwordError text-center">
                                            Invalid password!
                                        </Form.Control.Feedback>
                                    </InputGroup>

                                    <InputGroup  className="mt-2">
                                        <Form.Control
                                            required={true}
                                            type="password"
                                            id={"confirmPassword"}
                                            name={"confirmPassword"}
                                            placeholder="confirm Password"
                                            onChange={this.handleChange}
                                        />
                                        <InputGroup.Append>
                                            <div className="input-group-text">
                                                <i className="far fa-eye" />
                                            </div>
                                        </InputGroup.Append>
                                        <Form.Control.Feedback type="invalid"
                                            className="invalid-feedback passwordError text-center">
                                            Invalid password!
                                        </Form.Control.Feedback>
                                        {this.state.registrationFormFields.hasPasswordMatch===false ?
                                            <Form.Control.Feedback type="invalid"
                                                className="invalid-feedback passwordError text-center">
                                                Passwords do not match!
                                            </Form.Control.Feedback>: null}
                                    </InputGroup>
                                </Col>
                            </Form.Row>

                            <Form.Row className="mt-3">
                                <Col>
                                    <InputGroup className="LoginFormInputGroup">
                                        <Form.Control
                                            required={true}
                                            type="name"
                                            id={"firstName"}
                                            name={"firstName"}
                                            placeholder="First Name"
                                            onChange={this.handleChange}
                                        />
                                    </InputGroup>
                                </Col>
                                <Col>
                                    <InputGroup className="LoginFormInputGroup">
                                        <Form.Control
                                            required={true}
                                            type="name"
                                            id={"middleName"}
                                            name={"middleName"}
                                            placeholder="Middle Name"
                                            onChange={this.handleChange}
                                        />
                                    </InputGroup>
                                </Col>
                            </Form.Row>

                            <Form.Row className="mt-1">
                                <Col>
                                    <InputGroup className="LoginFormInputGroup">
                                        <Form.Control
                                            required={true}
                                            type="name"
                                            id={"lastName"}
                                            name={"lastName"}
                                            placeholder="Last Name"
                                            onChange={this.handleChange}
                                        />
                                    </InputGroup>
                                </Col>
                                <Col>
                                    <InputGroup className="LoginFormInputGroup">
                                        <Form.Control
                                            required={true}
                                            type="addressDetails"
                                            id={"streetName"}
                                            name={"streetName"}
                                            placeholder="Street Name"
                                            onChange={this.handleChange}
                                        />
                                    </InputGroup>
                                </Col>
                            </Form.Row>

                            <Form.Row className="mt-1">
                                <Col>
                                    <InputGroup className="LoginFormInputGroup">
                                        <Form.Control
                                            required={true}
                                            type="addressDetails"
                                            id={"houseNumber"}
                                            name={"houseNumber"}
                                            placeholder="House Number"
                                            onChange={this.handleChange}
                                        />
                                    </InputGroup>
                                </Col>
                                <Col>
                                    <InputGroup className="LoginFormInputGroup">
                                        <Form.Control
                                            required={true}
                                            type="addressDetails"
                                            id={"city"}
                                            name={"city"}
                                            placeholder="City"
                                            onChange={this.handleChange}
                                        />
                                    </InputGroup>
                                </Col>
                            </Form.Row>

                            <Form.Row className="mt-1">
                                <Col>
                                    <InputGroup className="LoginFormInputGroup">
                                        <Form.Control
                                            required={true}
                                            type="addressDetails"
                                            id={"zipcode"}
                                            name={"zipcode"}
                                            placeholder="Zip Code"
                                            onChange={this.handleChange}
                                        />
                                    </InputGroup>
                                </Col>
                                <Col>
                                    <InputGroup className="LoginFormInputGroup">
                                        <Form.Control
                                            required={true}
                                            type="addressDetails"
                                            id={"state"}
                                            name={"state"}
                                            placeholder="State"
                                            onChange={this.handleChange}
                                        />
                                    </InputGroup>
                                </Col>
                            </Form.Row>
                            <Form.Row className="mt-1">
                                <Col>
                                    <InputGroup className="LoginFormInputGroup">
                                        <Form.Control
                                            required={true}
                                            type="addressDetails"
                                            id={"country"}
                                            name={"country"}
                                            placeholder="Country"
                                            onChange={this.handleChange}
                                        />
                                    </InputGroup>
                                </Col>
                            </Form.Row>          

                            <div className="col text-center my-auto">
                                {isLoading===true ? 
                                    <Button className="btn btn-primary disabledSubmitButton"
                                            type="submit"
                                            variant="primary"
                                            disabled={true}>
                                            <Spinner
                                                as="span"
                                                animation="grow" 
                                                variant="warning"
                                                size="sm"
                                                role="status"
                                                aria-hidden="true"
                                            /> 
                                            Processing...
                                    </Button> :
                                    <Button className="btn btn-primary submitButton"
                                            type="submit"
                                            variant="primary"
                                            disabled={!hasPasswordMatch 
                                                        || isLoading
                                                        || this.state.registrationFormFields.email.trim() === ''}>
                                        Submit
                                    </Button> 
                                }
                            </div>  
                        </Form>
                    </div>
                </div>
            </React.Fragment>
        );
    }
}

export default RegistrationForm;

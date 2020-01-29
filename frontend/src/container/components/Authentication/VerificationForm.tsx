import * as React from 'react';

// Add styling related imports
import '../../../stylesheets/css/App.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'font-awesome/css/font-awesome.min.css';
import '../../../stylesheets/css/login.css';
import { Form, InputGroup, Button, Spinner, Col } from 'react-bootstrap';

// Add types
import { AppState } from 'src/redux/reducers/rootReducer';
import { sendAccountVerification } from 'src/redux/actions/sendAccountVerificationAction';

export interface Props{
    isLoading: boolean;
    storeState: AppState;
    onVerificationSubmit: typeof sendAccountVerification;
    onModalStateSet: (status:boolean)=>void;
}

export interface State{
    isLoading: boolean;
    showResponseStatus: boolean;
    sendVerificationFormFields: {userName:string}; 
    validated: boolean;
}

class VerificationForm extends React.Component<Props, State> {

    public state: State;

    constructor(props: Props){
        
        super(props);
        this.state={
            isLoading: false,
            showResponseStatus: false,
            sendVerificationFormFields: {userName:''},
            validated: false
        };
    }

    public handleChange = async (event: any): Promise<void> => {

        // read the form input fields
        const sendVerificationFormFields = { ...this.state.sendVerificationFormFields };
        sendVerificationFormFields[event.currentTarget.name] = event.currentTarget.value;
        this.setState({
            sendVerificationFormFields
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
            sendVerificationFormFields: this.state.sendVerificationFormFields, 
            validated: true,
            isLoading: true,
            showResponseStatus: false
        }); 
        
        this.props.onVerificationSubmit(event, this.state.sendVerificationFormFields);

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
            if (this.props.storeState.responseStatus.type.valueOf() === 'SUCCESS'){
                this.props.onModalStateSet(false);
            }
        }, 3000);
    };

    public render(){

        const validated = this.state.validated;
        const isLoading = this.state.isLoading;
        const responseStatus = this.props.storeState.responseStatus;
        return (
            <React.Fragment>
                <div className = "container mb-2 modalContainer mx-auto registrationFormContainer">
                    <div className = "row-fluid">
                        {responseStatus.type==="SUCCESS" && this.state.showResponseStatus ? 
                            <p className="responseSuccess mt-1 mb-0"> {responseStatus.message} </p> : null}
                            
                        {responseStatus.type==="FAILURE" && this.state.showResponseStatus ? 
                            <p className="responseFailure mt-1 mb-0"> {responseStatus.message} </p> : null}

                        <Form name = "sendVerificationnForm"
                              className = "mx-auto needsVerificationFormValidation"
                              noValidate = {true}
                              validated = {validated}
                              onSubmit = {this.handleSubmit}>

                            <Form.Row className="mt-4">
                                <Col>
                                    <InputGroup className="SendVerificationFormInputGroup">
                                        <Form.Control
                                            required={true}
                                            type="userName"
                                            id={"sendVerificationUserName"}
                                            name={"userName"}
                                            placeholder="username"
                                            onChange={this.handleChange}
                                        />
                                        <Form.Control.Feedback type="invalid"
                                            className="invalid-feedback usernameError text-center">
                                            Invalid username!
                                        </Form.Control.Feedback>
                                    </InputGroup>
                                </Col>
                                <Col>
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
                                                    disabled={isLoading || this.state.sendVerificationFormFields.userName.trim() === ''}>
                                                Submit
                                            </Button> 
                                        }
                                    </div>  
                                </Col>
                            </Form.Row>
                        </Form>
                    </div>
                </div>
            </React.Fragment>
        );
    }
}

export default VerificationForm;

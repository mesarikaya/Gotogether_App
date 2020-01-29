import * as React from 'react';

// Add styling related imports
import '../../../stylesheets/css/App.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'font-awesome/css/font-awesome.min.css';
import '../../../stylesheets/css/login.css';

// Add types
import { GroupCreationFormFields } from 'src/redux/types/userInterface/groupCreationFormFields';
import { AppState } from 'src/redux/reducers/rootReducer';
import { Button, Modal, Col, InputGroup, Form, Spinner } from 'react-bootstrap';
import { createGroup } from 'src/redux/actions/UserPage/createGroupAction';
import { updateUserAccount } from 'src/redux/actions/UserPage/updateUserAccountAction';

export interface Props {   
    storeState: AppState;
    onCreateGroupSubmit: typeof createGroup;
    onGetUserAccountDetails: typeof updateUserAccount;
};

export interface State {
    isLoading: boolean;
    showResponseStatus: boolean;
    formFields: GroupCreationFormFields;
    show: boolean;
    validated: boolean;
};

class CreateGroupForm extends React.Component<Props, State> {
    
    public state: State;

    constructor(props: Props) {
        super(props);

        this.state = {
            isLoading: false,
            showResponseStatus: false,
            formFields: {
                groupName: '',
                originStreetName: '',
                originHouseNumber: '',
                originCity: '',
                originSearchRadius: 2,
                originZipcode: '',
                originState: '',
                originCountry: '',
                destinationStreetName: '',
                destinationHouseNumber: '',
                destinationCity: '',
                destinationSearchRadius: 2,
                destinationZipcode: '',
                destinationState: '',
                destinationCountry: '',
            },
            show: false,
            validated: false
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
            formFields: {
                ...this.state.formFields
            }
        });
    }

    public handleChange = async (event: any): Promise<void> => {
        // read the form input fields
        const formFields = { 
            ...this.state.formFields
        };

        if (event.currentTarget.name==="originSearchRadius" || event.currentTarget.name==="destinationSearchRadius"){
            formFields[event.currentTarget.name] = Math.max(0, Number(event.currentTarget.value));
        }else{
            formFields[event.currentTarget.name] = event.currentTarget.value;
        }
        
        this.setState({
            formFields
        });
    }

    public isAllFormFieldsFilledIn(formFields: GroupCreationFormFields){

        const values:any = [];
        Object.keys(formFields)
              .map((key) => {
                if (typeof formFields[key] === 'string'){
                    values.push(formFields[key].trim());
                }else{
                    values.push(formFields[key]);
                }
              });
        const hasAnyEmpty = values.some((obj:any) => {
            if(typeof obj==='string'){
                return obj==='';
            } else{
                return false;
            }}
        );
        
        return !hasAnyEmpty;
    }

    public handleSubmit = async (event: React.FormEvent<HTMLFormElement>): Promise<void> => {

        const currAppState = this.props.storeState;

        const form = event.currentTarget;
        if (form.checkValidity() === false && this.isAllFormFieldsFilledIn(this.state.formFields)) {
            event.preventDefault();
            event.stopPropagation();
        }

        this.setState({ 
            formFields: {...this.state.formFields},
            validated: true,
            isLoading: true
        }); 

        if(this.isAllFormFieldsFilledIn(this.state.formFields)){
            this.setState({ 
                formFields: {...this.state.formFields},
                validated: true
            });
            this.props.onCreateGroupSubmit(event, 
                                           this.state.formFields, 
                                           currAppState.userAccount,
                                           currAppState.system.userName,
                                           currAppState.system.token);
        }

        const userId = this.props.storeState.system.userName;
        if(userId){
            await Promise.all([this.props.onGetUserAccountDetails(null, currAppState.system.userName,currAppState.system.token)]);
            await this.props.onGetUserAccountDetails(null, 
                                                     currAppState.system.userName,
                                                     currAppState.system.token);               
        }

        window.setTimeout(() =>{
            this.setState({
                isLoading: false,
                show: false
            });
        }, 3000);
    };

    public render() {
        
        const validated = this.state.validated;
        const isLoading = this.state.isLoading;
        const isAllFormFieldsFilledIn = this.isAllFormFieldsFilledIn(this.state.formFields);
        return (
          <React.Fragment>
            <Button variant="success" onClick={this.handleModalShow} >
                Create Group
            </Button>
            <Modal dialogClassName={"modalDialog"} 
                   show={this.state.show} 
                   onHide={this.handleModalClose}>
                  <Modal.Header className={"modalHeader"} closeButton={true} />
                  <Modal.Title className="text-center createGroupModalTitleText"> Create Group </Modal.Title>
                  <Modal.Body>
                      <div className="container mb-2 modalContainer">
                          <div className="row-fluid">
                            <div className = "container mb-2 modalContainer mx-auto registrationFormContainer">
                                <div className = "row-fluid">
                                    <Form name = "createGroupForm"
                                        className = "needsCreateGroupValidation"
                                        noValidate = {true}
                                        validated = {validated}
                                        onSubmit = {this.handleSubmit}>

                                        <Form.Row className="mt-2">
                                            <Col>
                                                <InputGroup className="LoginFormInputGroup">
                                                    <Form.Control
                                                        required={true}
                                                        type="name"
                                                        id={"groupName"}
                                                        name={"groupName"}
                                                        placeholder="Group Name"
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
                                                        id={"originStreetName"}
                                                        name={"originStreetName"}
                                                        placeholder="Origin Street Name"
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
                                                        id={"originHouseNumber"}
                                                        name={"originHouseNumber"}
                                                        placeholder="Origin House Number"
                                                        onChange={this.handleChange}
                                                    />
                                                </InputGroup>
                                            </Col>
                                            <Col>
                                                <InputGroup className="LoginFormInputGroup">
                                                    <Form.Control
                                                        required={true}
                                                        type="addressDetails"
                                                        id={"originCity"}
                                                        name={"originCity"}
                                                        placeholder="Origin City"
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
                                                        id={"originZipcode"}
                                                        name={"originZipcode"}
                                                        placeholder="Origin Zipcode"
                                                        onChange={this.handleChange}
                                                    />
                                                </InputGroup>
                                            </Col>
                                            <Col>
                                                <InputGroup className="LoginFormInputGroup">
                                                    <Form.Control
                                                        required={true}
                                                        type="addressDetails"
                                                        id={"originState"}
                                                        name={"originState"}
                                                        placeholder="Origin State"
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
                                                        id={"originCountry"}
                                                        name={"originCountry"}
                                                        placeholder="The Netherlands"
                                                        onChange={this.handleChange}
                                                    />
                                                </InputGroup>
                                            </Col>
                                            <Col>
                                                <InputGroup className="LoginFormInputGroup">
                                                    <Form.Control
                                                        required={false}
                                                        type="addressDetails"
                                                        id={"originSearchRadius"}
                                                        name={"originSearchRadius"}
                                                        placeholder="2"
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
                                                        id={"destinationStreetName"}
                                                        name={"destinationStreetName"}
                                                        placeholder="Destination Street Name"
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
                                                        id={"destinationHouseNumber"}
                                                        name={"destinationHouseNumber"}
                                                        placeholder="Destination House Number"
                                                        onChange={this.handleChange}
                                                    />
                                                </InputGroup>
                                            </Col>
                                            <Col>
                                                <InputGroup className="LoginFormInputGroup">
                                                    <Form.Control
                                                        required={true}
                                                        type="addressDetails"
                                                        id={"destinationCity"}
                                                        name={"destinationCity"}
                                                        placeholder="Destination City"
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
                                                        id={"destinationZipcode"}
                                                        name={"destinationZipcode"}
                                                        placeholder="Destination Zipcode"
                                                        onChange={this.handleChange}
                                                    />
                                                </InputGroup>
                                            </Col>
                                            <Col>
                                                <InputGroup className="LoginFormInputGroup">
                                                    <Form.Control
                                                        required={true}
                                                        type="addressDetails"
                                                        id={"destinationState"}
                                                        name={"destinationState"}
                                                        placeholder="Destination State"
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
                                                        id={"destinationCountry"}
                                                        name={"destinationCountry"}
                                                        placeholder="The Netherlands"
                                                        onChange={this.handleChange}
                                                    />
                                                </InputGroup>
                                            </Col>
                                            <Col>
                                                <InputGroup className="LoginFormInputGroup">
                                                    <Form.Control
                                                        required={false}
                                                        type="addressDetails"
                                                        id={"destinationSearchRadius"}
                                                        name={"destinationSearchRadius"}
                                                        placeholder="2"
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
                                                        disabled={isLoading
                                                                  || !isAllFormFieldsFilledIn}>
                                                    Submit
                                                </Button> 
                                            }
                                        </div>  
                                    </Form>
                                </div>
                            </div>
                        </div>
                    </div>
                </Modal.Body>
            </Modal>
          </React.Fragment>
        );
  }
}

export default CreateGroupForm;

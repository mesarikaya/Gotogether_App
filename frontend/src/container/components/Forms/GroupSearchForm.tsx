import * as React from 'react';
import { Form, InputGroup, FormLabel, Button } from 'react-bootstrap';

// Add styling related imports
import '../../../stylesheets/css/App.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'font-awesome/css/font-awesome.min.css';
import '../../../stylesheets/css/GroupSearch.css';

import { GroupSearchFormFields } from '../../../redux/types/userInterface/groupSearchFormFields';
import InputRange from '../Buttons/InputRange';
import { SearchGroups } from '../../../redux/actions/groupSearchAction';
import { submitForm } from '../../utilities/submitForm';
import { SearchUsers } from 'src/redux/actions/userSearchAction';

export interface Props {
    formFields: GroupSearchFormFields;
    page: number;
    token: string;
    onSubmit: typeof SearchGroups | typeof SearchUsers;
    updateSearchFormFields: (formFields: GroupSearchFormFields) => void;
    isLoading: (status:boolean)=>void;
};

export interface State {
    validated: boolean;
    isLoading: boolean;
};

const MAX_RANGES=1000;
const MIN_RANGES=0;

class GroupSearchForm extends React.Component<Props, State> {
    
    public state: State;

    constructor(props: Props) {
        super(props);

        this.state = { 
            validated: false,
            isLoading: false
        };

        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.handleInputRangeChange = this.handleInputRangeChange.bind(this);
        this.increase = this.increase.bind(this);
        this.decrease = this.decrease.bind(this);
    }

    // TODO: Need to FIND A WAY TO USE THE RIGHT TYPE WITHOUT ERROR React.ChangeEvent<HTMLInputElement>
    public handleChange = async (event: any): Promise<void> => {
        // read the form input fields
        const formFields = { ...this.props.formFields };
        formFields[event.currentTarget.name] = event.currentTarget.value;
        this.props.updateSearchFormFields(formFields);
    }

    public handleSubmit = async (event: React.FormEvent<HTMLFormElement>): Promise<void> => {

        // TODO: Add button disable
        submitForm(event);
        this.setState({ 
            validated: true,
            isLoading: true
        });

        this.props.isLoading(true);

        this.props.onSubmit(event, this.props.formFields, [], this.props.page, this.props.token); 

        window.setTimeout(() =>{
            this.setState({
                isLoading: false,
            });
            this.props.isLoading(false);
        }, 2000);

    };

    public decrease = (name: string) => {
    
        if (name==="originRange"){
            const rangeValue = this.setRange(this.props.formFields.originRange);
            const formFields = { 
                ...this.props.formFields, 
                originRange: Math.max(Number(rangeValue) - 1, MIN_RANGES)
            };
            this.props.updateSearchFormFields(formFields);
        }else if(name==="destinationRange"){

            const rangeValue = this.setRange(this.props.formFields.destinationRange);
            const formFields = { 
                ...this.props.formFields, 
                destinationRange: Math.max(Number(rangeValue) - 1, MIN_RANGES)
            };
            this.props.updateSearchFormFields(formFields);
        }
    }
        
    public increase = (name:string) => {
        
        if (name==="originRange"){
            const rangeValue = this.setRange(this.props.formFields.originRange);
            const formFields = { 
                ...this.props.formFields, 
                originRange: Math.min(Number(rangeValue) + 1, MAX_RANGES) 
            };
            this.props.updateSearchFormFields(formFields);
        }else if(name==="destinationRange"){

            const rangeValue = this.setRange(this.props.formFields.destinationRange);
            const formFields = { 
                ...this.props.formFields, 
                destinationRange: Math.min(Number(rangeValue) + 1, MAX_RANGES)
            };
            this.props.updateSearchFormFields(formFields);
        }
    }

    public handleInputRangeChange = async (name: string, value: number): Promise<void> => {
        
        const rangeValue = Math.max(0, Math.min(value,MAX_RANGES));
        if (name==="originRange"){
            const formFields = { 
                ...this.props.formFields, 
                originRange: Math.min(Number(rangeValue), MAX_RANGES) 
            };
            this.props.updateSearchFormFields(formFields);
        }else if (name==="destinationRange") {
            const formFields = { 
                ...this.props.formFields, 
                destinationRange: Math.min(Number(rangeValue), MAX_RANGES)
            };
            this.props.updateSearchFormFields(formFields);
        }
    };

    public render() {

        const validated = this.state.validated;
        const isLoading = this.state.isLoading;
        return (
          <React.Fragment>
            <Form name="groupSearchForm" className="groupSearchFormValidation text-center" 
            noValidate={true} validated = {validated} onSubmit = {this.handleSubmit}>
                <Form.Row> 
                    <InputGroup className="searchFormInputGroup justify-content-center">
                        {/* <!- Origin Input Group --> */}
                        <Form.Group className="originInputFormGroup">
                            {/* <!- Origin Input text --> */}
                            <Form.Label className="originLabel" >
                                <strong>Origin</strong>
                            </Form.Label>
                            <Form.Control 
                                type="input"
                                id={"origin"}
                                name={"origin"}
                                placeholder="Address/zip code" 
                                required={true}
                                onChange={this.handleChange}
                            />
                        </Form.Group>
                        {/* <!- Origin Input Range Group --> */}
                        <Form.Group controlId="originAdressRangeControl">
                            {/* <!- Origin Range --> */}
                            <FormLabel className="originRangeDropdownLabel">
                                <strong>Range</strong>
                            </FormLabel>
                            <InputRange 
                                name={"originRange"}
                                value={this.props.formFields.originRange}
                                onChangeValue={this.handleInputRangeChange}
                                onIncrease={this.increase}
                                onDecrease={this.decrease}
                            />
                        </Form.Group>
                    </InputGroup>
                
                    <InputGroup className="justify-content-center">
                        {/* <!- Destination Input Group --> */}
                        <Form.Group className="destinationInputFormGroup">
                            {/* <!- Destination Input text --> */}
                            <Form.Label className="destinationLabel">
                                <strong>Destination</strong>
                            </Form.Label>
                            <Form.Control 
                                type="input"
                                id={"destination"}
                                name={"destination"}
                                placeholder="Address/zip code" 
                                required={true}
                                onChange={this.handleChange}
                            />
                        </Form.Group>
                        {/* <!- Destination Input Range Group --> */}
                        <Form.Group controlId="destinationAdressRangeControl">
                            {/* <!- Destination Range --> */}
                            <FormLabel className="destinationRangeDropdownLabel">
                                <strong>Range</strong>
                            </FormLabel>
                            <InputRange 
                                name={"destinationRange"}
                                value={this.props.formFields.destinationRange}
                                onChangeValue={this.handleInputRangeChange}
                                onIncrease={this.increase}
                                onDecrease={this.decrease}
                            />
                        </Form.Group>
                    </InputGroup>
                </Form.Row>
                {isLoading===true ? 
                    <Button className="btn btn-primary disabledSubmitButton"
                            type="submit"
                            variant="primary"
                            disabled={true}>
                            Processing...
                    </Button> :
                    <Button className="btn btn-primary submitButton"
                            type="submit"
                            variant="primary"
                            size="lg"
                            disabled={isLoading 
                                        || this.props.formFields.destination.trim() === '' 
                                        || this.props.formFields.origin.trim() === ''}>
                        Submit
                    </Button> 
                }
            </Form>
          </React.Fragment>
        );
  }

  private isValidEntry(entry: string|number){
    entry = Number(entry);
    return (isNaN(entry) || entry<0 || typeof entry !== 'undefined' || entry==='' || entry === null);
  }

  private setRange(entry: string|number){
    const rangeValue = Number(entry);
    return (this.isValidEntry(rangeValue)) ? rangeValue: 0;
  }
}

export default GroupSearchForm;

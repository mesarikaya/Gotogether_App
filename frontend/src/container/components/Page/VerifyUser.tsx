import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter, Redirect } from "react-router";

// Add styling related imports
import '../../../stylesheets/css/App.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'font-awesome/css/font-awesome.min.css';
import { validateVerificationToken } from 'src/redux/actions/validateVerificationTokenAction';
import { AppState } from 'src/redux/reducers/rootReducer';
import { store } from 'src/redux/store';


// These props are provided by the router
interface PathProps {
    history: any;
    location: any;
    match: any;
}

export interface Props {
    redirect: boolean,
    storeState: AppState,
    onValidateToken: typeof validateVerificationToken;
};

interface State {
    showResponseStatus: boolean;
    redirect: boolean;
    storeState: AppState;
};


class VerifyUser extends React.Component<Props & RouteComponentProps<PathProps>, State> {
    public state: State;

    constructor(props: Props & RouteComponentProps<PathProps>) {
        super(props);
        const currentState:AppState = store.getState();
        this.state = {
            showResponseStatus: false,
            storeState: currentState,
            redirect: false
        };
    }

    public componentDidMount() {
        
        // tslint:disable-next-line:no-console  
        console.log("Location parsing for verification: ", this.props.match.params);
        const params = JSON.parse(JSON.stringify(this.props.match.params));
        // tslint:disable-next-line:no-console  
        console.log("Location parsing for verification: ", params);

        // Send all the data on component load
        this.verifyUser(params);
    }

    public verifyUser = async (params: any): Promise<void> => {
        // tslint:disable-next-line:no-console
        console.log("GET METHOD VERIFY TOKEN:", params);
        this.props.onValidateToken(params);
        this.setState({
            showResponseStatus: true,
        });
        window.setTimeout(() =>{
            this.setState({
                showResponseStatus: false,
                redirect: false
            });
            if (this.state.storeState.responseStatus.type.valueOf() === 'SUCCESS'){
                this.setRedirect(true);
            }
        }, 3000);
    };

    public setRedirect = (value: boolean) => {
        this.setState({
            redirect: value
        })
    }

    public renderRedirect = () => {
        if (this.state.redirect) {
            return <Redirect to='/' />
        }
        return null; 
    }

    public render() {
        return (
            <div className="text-center">
                {this.state.storeState.responseStatus.message}
                {this.renderRedirect()}
            </div>
        );
    }

}

// Create mapToState and mapDispatch for Redux
const mapStateToProps = (
    state: State, 
    OwnProps: Props & RouteComponentProps<PathProps>
    ) => {
    return {
        storeState: state.storeState,
        redirect: state.redirect,
        showResponseStatus: state.showResponseStatus
    }
}

const mapDispatchToProps = (dispatch: any) => {
    return {
        onValidateToken: (params: any) => dispatch(validateVerificationToken(params))
    }
}

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(VerifyUser));
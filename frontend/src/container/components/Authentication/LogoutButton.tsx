import * as React from 'react';

// Add styling related imports
import '../../../stylesheets/css/App.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'font-awesome/css/font-awesome.min.css';
import '../../../stylesheets/css/login.css';

// Add types
import { onLogoutUpdateAuth } from 'src/redux/actions/jwtAuthActionLogout';
import { AppState } from 'src/redux/reducers/rootReducer';
import { Button } from 'react-bootstrap';

export interface Props {   
    storeState: AppState;
    onLogout: typeof onLogoutUpdateAuth;
};

export interface State {
    isLoading: boolean;
};

class LogoutButton extends React.Component<Props, State> {
    
    public state: State;
    
    constructor(props: Props) {
        super(props);

        this.state = {
            isLoading: false
        };
        this.handleOnClick = this.handleOnClick.bind(this);
    }
    
    public handleOnClick = async (event: React.MouseEvent<HTMLButtonElement, MouseEvent>): Promise<void> =>  {
        this.props.onLogout(event, this.props.storeState.system);
    }

    public render() {
        
        return (
          <React.Fragment>
            <Button className="navButton logoutButton" variant="link" onClick={this.handleOnClick} >
                <i className="fas fa-sign-out-alt">
                    <strong id="icons"> Log out</strong>
                </i>
            </Button>
          </React.Fragment>
        );
  }
}

export default LogoutButton;

import * as React from 'react';

// Import types
import { GroupUser } from '../../../../redux/types/userInterface/groupUser';
import { GroupSearchResult } from '../../../../redux/types/userInterface/groupSearchResult';
import { updateSelectedGroup } from '../../../../redux/actions/GroupPage/updateSelectedGroupAction';

// Styling imports
import { Card } from 'react-bootstrap';
import '../../../../stylesheets/css/cards/groupCard.css';
import { withRouter, RouteComponentProps } from 'react-router';


interface Props {
    key: string;
    groupIndex: string;
    name: string;
    groupDetails: any;
    members: GroupUser;
    group: GroupSearchResult;
    token: string;
    updateSelectedGroup: typeof updateSelectedGroup;
}

// These props are provided by the router
interface PathProps {
    history: any;
    location: any;
    match: any;
}

class GroupCard extends React.Component<Props & RouteComponentProps < PathProps >>{

    constructor(props: Props & RouteComponentProps < PathProps >){
        
        super(props);
        this.handleOnClick = this.handleOnClick.bind(this);
    }

    public handleOnClick = async (event: React.MouseEvent<HTMLDivElement, MouseEvent>): Promise<void> =>{

        event.preventDefault();
        this.props.updateSelectedGroup(event, this.props.group,this.props.group.id, this.props.token);
        this.props.history.push('/group');
    }

    public render(){
        return (
            <React.Fragment>
                {/* <!- Group Card design --> */}
                <Card key={this.props.name} className="groupCard" onClick={this.handleOnClick}>
                    <Card.Header>
                        <Card.Title className="card-header-text">
                            Group: {this.props.name}
                        </Card.Title>
                    </Card.Header>
                    <Card.Body>
                        <Card.Text className="card-text">
                            {this.props.groupDetails.originCity}, {this.props.groupDetails.originZipCode} (+/-{this.props.groupDetails.originRange} km)
                        </Card.Text>
                        <Card.Text className="card-text">
                            <i className="fas fa-angle-double-down fa-7x"/>
                        </Card.Text>            
                        <Card.Text className="card-text">
                            {this.props.groupDetails.destinationCity}, {this.props.groupDetails.originZipCode} (+/-{this.props.groupDetails.destinationRange} km)
                        </Card.Text>
                    </Card.Body>
                </Card>
                <br />
            </React.Fragment>
        );
    }
}

export default withRouter(GroupCard);
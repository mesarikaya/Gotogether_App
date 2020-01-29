import * as React from 'react';

// Import types
import { UserSearchResult } from '../../../../redux/types/userInterface/userSearchResult';

// Styling imports
import { Card, Button, Table } from 'react-bootstrap';
import '../../../../stylesheets/css/cards/groupCard.css';
import { withRouter, RouteComponentProps } from 'react-router';
import { GroupSearchResult } from '../../../../redux/types/userInterface/groupSearchResult';
import { updateInvitationsList } from '../../../../redux/actions/GroupPage/updateInvitationsListAction';

interface Props {
    groupInfo: GroupSearchResult;
    userList: UserSearchResult[];
    token: string;
    isUserInGroup: boolean;
    isUserOwnerInGroup :boolean;
    onInviteUser: typeof updateInvitationsList;
}

// These props are provided by the router
interface PathProps {
    history: any;
    location: any;
    match: any;
}

class UserTableList extends React.Component<Props & RouteComponentProps < PathProps >>{

    constructor(props: Props & RouteComponentProps < PathProps >){
        super(props);
        this.createTable = this.createTable.bind(this);
        this.handleOnInviteUser = this.handleOnInviteUser.bind(this);
    }

    public createTable(data: UserSearchResult[]) {

        const rows = [];
        for (const obj in data) {
            if (data.hasOwnProperty(obj)) {
                rows.push(
                    <tr key={data[obj].userName}>
                      <td className="text-center text-primary">{data[obj].firstName + " " + data[obj].middleName + "."}</td>
                      <td className="text-center">{data[obj].surname}</td>
                      <td className="text-center">{data[obj].userName}</td>
                      <td className="text-center">{data[obj].address}</td>
                      {this.props.isUserInGroup ? <td className="text-center"><Button variant="info" size="sm" name={data[obj].userName} 
                      onClick={this.handleOnInviteUser}>Invite</Button></td> : <td />}
                    </tr>
                );
            }
        }

        return rows;
    }

    public handleOnInviteUser = async (event: React.MouseEvent<HTMLButtonElement>): Promise<void> => {
        event.preventDefault();
        const userId = event.currentTarget.getAttribute('name');
        const groupId = this.props.groupInfo.id;
        if(userId && groupId){
            this.props.onInviteUser(event, this.props.groupInfo, groupId, userId, this.props.token, 'add');
        }
    }

    public render(){
        
        const tableRows = this.createTable(this.props.userList);
        return (
            <React.Fragment>
                {/* <!- USer List Card design --> */}
                <Card className="UserListCard" key="UserListCard">
                    <Card.Header>
                        <Card.Title className="card-header-text text-center">
                            Members
                        </Card.Title>
                    </Card.Header>
                    <Card.Body>
                        <Table responsive={true}>
                            <thead className="text-center tableHeader">
                                <tr>
                                    <th>Name</th>
                                    <th>Surname</th>
                                    <th>username</th>
                                    <th>Adress</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody className="text-center">
                                    {tableRows}
                            </tbody> 
                        </Table>
                    </Card.Body>
                </Card>
            </React.Fragment>
        );
    }
}

export default withRouter(UserTableList);
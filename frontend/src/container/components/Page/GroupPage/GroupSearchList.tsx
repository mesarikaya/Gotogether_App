import * as React from 'react';

// Import types
import { withRouter, RouteComponentProps } from 'react-router';
import { GroupSearchResult } from '../../../../redux/types/userInterface/groupSearchResult';
import { updateWaitingList } from 'src/redux/actions/GroupPage/updateWaitingListAction';

// Styling imports
import { Card, Button, Table } from 'react-bootstrap';
import '../../../../stylesheets/css/cards/groupCard.css';
import { updateSelectedGroup } from 'src/redux/actions/GroupPage/updateSelectedGroupAction';

interface Props {
    key: string;
    groupSearchResult: GroupSearchResult[];
    userName: string;
    token: string;
    onApplyGroup: typeof updateWaitingList;
    updateSelectedGroup: typeof updateSelectedGroup;
}

// These props are provided by the router
interface PathProps {
    history: any;
    location: any;
    match: any;
}

class GroupSearchList extends React.Component<Props & RouteComponentProps < PathProps >>{

    constructor(props: Props & RouteComponentProps < PathProps >){
        super(props);
        this.createTable = this.createTable.bind(this);
        this.handleOnApply = this.handleOnApply.bind(this);
    }

    public handleOnClick = async (event: React.MouseEvent<HTMLDivElement, MouseEvent>, 
                                  group: GroupSearchResult): Promise<void> =>{
        event.preventDefault();
        await Promise.all([this.props.updateSelectedGroup(event, group, group.id, this.props.token)]);
        window.setTimeout(() =>{
            this.props.history.push('/group');
        }, 500);
    }

    public handleOnApply = async (event: React.MouseEvent<HTMLButtonElement>, 
                                  currentGroup: GroupSearchResult): Promise<void> => {
        event.preventDefault();
        const groupId = event.currentTarget.getAttribute('id');
        const userId = this.props.userName;
        if(userId && groupId){
            this.props.onApplyGroup(event, currentGroup, groupId, userId, this.props.token, 'add', false);
        }
    }

    public createTable(data: GroupSearchResult[]) {

        const rows = [];
        for (const obj in data) {
            if (data.hasOwnProperty(obj)) {
                rows.push(
                    <tr key={data[obj].name} >
                      <td className="text-center text-primary"
                        // tslint:disable-next-line: jsx-no-lambda
                        onClick={(e: React.MouseEvent<HTMLDivElement, MouseEvent>) => this.handleOnClick(e, data[obj])}>
                            {data[obj].name}
                      </td>
                      <td className="text-center">{data[obj].groupDetails.originCity}</td>
                      <td className="text-center">{data[obj].groupDetails.originZipCode}</td>
                      <td className="text-center">{data[obj].groupDetails.destinationCity}</td>
                      <td className="text-center">{data[obj].groupDetails.destinationZipCode}</td>
                      <td className="text-center">
                        <Button variant="danger" size="sm" 
                                id={data[obj].id} 
                                // tslint:disable-next-line: jsx-no-lambda
                                onClick={(e: React.MouseEvent<HTMLButtonElement, MouseEvent>) => this.handleOnApply(e, data[obj])}>
                                Apply
                        </Button>
                      </td>
                    </tr>
                );
            }
        }

        return rows;
    }

    public render(){
        
        const tableRows = this.createTable(this.props.groupSearchResult);
        return (
            <React.Fragment>
                {/* <!- USer List Card design --> */}
                <Card className="GroupSearchListCard">
                    <Card.Header>
                        <Card.Title className="card-header-text text-center">
                            Groups
                        </Card.Title>
                    </Card.Header>
                    <Card.Body>
                        <Table responsive={true}>
                            <thead className="text-center tableHeader">
                                <tr>
                                    <th>Name</th>
                                    <th>Origin</th>
                                    <th>Zipcode</th>
                                    <th>Destination</th>
                                    <th>Zipcode</th>
                                    <th>Action</th>
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

export default withRouter(GroupSearchList);
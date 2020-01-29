import {Route, Switch} from "react-router";
import App from "../container/components/Page/App";
import * as React from "react";

// tslint:disable-next-line:no-console
console.log(process.env);

const routes = (
    <div>
        <Switch>
            <Route exact={true} path="/" component={App}/>
            <Route path="/**" component={App} />
        </Switch>
    </div>
)

export default routes;
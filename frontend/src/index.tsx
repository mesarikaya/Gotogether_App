import * as React from 'react';
import * as ReactDOM from 'react-dom';
import { Provider } from 'react-redux'
import { history, store } from './redux/store';
import Container from './container/Container';
import './stylesheets/css/index.css';
import registerServiceWorker from './registerServiceWorker';
import {Route, Router } from "react-router";
// import { unregister } from './registerServiceWorker';

ReactDOM.render(
    (<Provider store={store}>
        <Router history={history}>
            <Route path="/" component={Container}/>
        </Router>
     </Provider>),
    document.getElementById('root') as HTMLElement
);
registerServiceWorker();
// unregister();

[![CircleCI](https://circleci.com/gh/mesarikaya/restAPI.svg?style=svg)](https://circleci.com/gh/mesarikaya/restAPI) [![codecov](https://codecov.io/gh/mesarikaya/restAPI/branch/master/graph/badge.svg)](https://codecov.io/gh/mesarikaya/restAPI)

# Web App for Ride Sharing for work

## Tech. Stack:
► Back-end: Java and Spring Boot with webflux
  - Rest API based design
► Database: MongoDB
► Front-end: Typescript + ReactJS + Redux

## General Functionalities:
► Strong account security and user role management
► CRUD operations for user and ride group operations
► Account registration and verification (Email service with via SendGrid)
► Geolocation detection and search via Nomatim API

# Views
- Modular component based design is used
- Many components are reused in several page views

## Standard home page
- Container routes to the 'App' presentational view and shows the home page for guests with Login/Signup functionality
- Redux enables synchronized app store data sharing among relevant components
- Enables user login or registration
- A logged in user can go to their account details and see all memberships and requests
 
![alt text](https://github.com/mesarikaya/Gotogether_App/blob/master/readMeImages/GoTogetherAppCapture4.PNG)

## Group page
- Provides details of the clicked or requested group. 
	- It shows the group members and their privileges
  - Depending on the user privilege level, user can remove memberships or leave certain group, etc
  - Depending on user privilege level, user can accept join requests or only have overall visibility
	- User can search new members for the group and send invitations

![alt text](https://github.com/mesarikaya/Gotogether_App/blob/master/readMeImages/GoTogetherAppCapture5.PNG)

## User Page
- Provides details of logged in user memberships and pending invitations
- User can search for new groups depending on the origin and destination latitude and radius and apply for membership
- Log out reverts to home page
- User can create a new group, delete existing memberships approve/disapprove pending invitations

![alt text](https://github.com/mesarikaya/Gotogether_App/blob/master/readMeImages/GoTogetherAppCapture7.PNG)

## Navigation
- Standard navigation component with built in log in/ log out/ registration and account verification components
- Account verification is activated with Send Grid email API

![alt text](https://github.com/mesarikaya/Gotogether_App/blob/master/readMeImages/GoTogetherAppCapture2.PNG)





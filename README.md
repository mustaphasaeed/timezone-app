Introduction
============
Sample application that provide UI for users to add and remove timezones, the application has two roles for users:
- Normal Access: users with normal access are allowed to view\add\delete their time zones
- Admin Access: users with admin access can see other users' timezones and can manage others users
It has two modules, timezone-ui & timezone-backend, Tech used: Java 8, Vaadin, Spring, Maven, MySQL, Flyway, Mockito & Junit.

Timezone-backend
================
This project contains the REST APIs for the project, it use SpringMVC and it has two main controllers:
User Controller: contain several APIs to manage users account and profiles
Timezone Controller: contain several APIs to manager timezones

Steps to get timezone-backend up and running:
1- Make sure that mySql is installed and create an empty schema, ex. timezones
2- Configure mySql connection parametes in <timezone-backend-root>/src/main/resources/application.properties
3- To build the project, generate artifacts and run the test cases, run: mvn clean install
4- To run the test cases only, run: mvn surefire:test
5- To start the backend as server, run: mvn spring-boot:run

Timezone-UI
===========
This project contain the web app where users can view and manager their timezones, it use Vaadin as web framework

Steps to get timezone-ui up and running:
1- Make sure that the backend is running as mentioned above
2- Configure backend server parameters in <timezone-ui-root>/src/main/resources/application.properties
3- To build the project and generate artifacts, run: mvn clean install
4- To start the backend as server, run: mvn spring-boot:run
5- From a broswer, go to http://<hostname>:8092/
6- The default admin username / password are: admin@timezone.com / password

Known Issues
============
1- Vaadin grid component is refreshing the elements when trying to navigate by keyboard, this cause timezone display to be restored to the original view for less than 1 second, then it resume the time display




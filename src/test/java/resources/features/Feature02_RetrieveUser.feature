# Retrieve existing user from the system
@retrieveUser
Feature: User Retrieval 
Scenario: Search user details from system given a user 
	Given the Swagger definition at http://petstore.swagger.io/v2/swagger.json 
	When user sends a GET request to "http://petstore.swagger.io/v2/user/{username}" 
	And path parameter with 
		| key    | value        |
		| username  | Shreejit |
	And header with 
		| key    | value        |
		| Accept   | application/json |
	Then response status code should be 200 
	And response body contains 
		| key    | value        |
		| username  | Shreejit |
		| firstName    |        string  |
		| lastName |   string           |
		| userStatus |  0            |
	And response type should be "json"
	And response time is within 600 ms 
	And response status line is "OK" 
	
		 
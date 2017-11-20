# Login user into the system
@ignore
Feature: Log in user into the system 
Scenario: Log in a valid user into the system
	Given the Swagger definition at http://petstore.swagger.io/v2/swagger.json 
	When user sends a GET request to "http://petstore.swagger.io/v2/user/login" 
	And query parameter with 
		| key    | value        |
		| username   | string |
		| password   | string |
	Then response status code should be 200 
	And response type should be "json" 
	And response time is within 300 ms 
	And response status line is "OK"
		 
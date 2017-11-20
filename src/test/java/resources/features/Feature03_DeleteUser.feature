# Check deletion of valid / invalid user from the system
@ignore
Feature: User Deletion from system 

Scenario: Delete available user from system 
	Given the Swagger definition at http://petstore.swagger.io/v2/swagger.json 
	When user sends a DELETE request to "http://petstore.swagger.io/v2/user/{username}" 
	And path parameter with 
		| key    | value        |
		| username   | Shreejit |
	Then response status code should be 200
	And response status line is "OK"
	
	
Scenario: Delete unavailable user from system 
	Given the Swagger definition at http://petstore.swagger.io/v2/swagger.json 
	When user sends a DELETE request to "http://petstore.swagger.io/v2/user/{username}" 
	And path parameter with 
		| key    | value        |
		| username   | SomeRandomUser|
	Then response status code should be 404 
	And response time is within 300 ms 
	And response status line is "Not Found"
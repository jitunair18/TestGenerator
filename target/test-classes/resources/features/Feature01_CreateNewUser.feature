#  Create user in the system
@createUser
Feature: User Creation 
Scenario: Create new user in the system 
	Given the Swagger definition at http://petstore.swagger.io/v2/swagger.json 
	When user sends a POST request to "http://petstore.swagger.io/v2/user" 
	And content 
		"""
  {
  "id": 0,
  "username": "Shreejit",
  "firstName": "string",
  "lastName": "string",
  "email": "string",
  "password": "string",
  "phone": "string",
  "userStatus": 0
}
  """
	Then response status code should be 200 
	And response time is within 600 ms 
	And response header contains 
		| key    | value        |
		| content-type   | application/json |
	And response status line is "OK"
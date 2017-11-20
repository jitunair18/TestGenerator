#  Support for Asynchronous REST services
@createUserAsynchronously @ignore
Feature: User Creation Asynchronously 
Scenario: Create new user in the system asynchronously 
    # use only if your system supports Swagger ignore otherwise
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
    # store 'username' variable from the json response for further processing
	Then response value "username" is stored 
	
	# check if asynchronous processing is completed
	# variables tagged {global} would be resolved before execution
	# build the http request object
	When user sends asynchronous GET request to "http://petstore.swagger.io/v2/user/{username}" 
	And path parameter with 
		| key    | value        |
		| username  | {global} |
	And header with 
		| key    | value        |
		| Accept   | application/json |
	# keep actively polling till processing status field in response changes to "DONE"
	And waits on "lastName" to be "somevalue" 
	And maxTimeOut is 50 seconds 
	# start running assertions on the response
	Then response status code should be 200 
	And response header contains 
		| key    | value        |
		| content-type   | application/json |
	And response status line is "OK"
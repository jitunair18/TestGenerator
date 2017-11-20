# Update an existing item in the system
@ignore
Feature: Update pet in system 
Scenario: Update existing pet in system 
	Given the Swagger definition at http://petstore.swagger.io/v2/swagger.json 
	When user sends a PUT request to "http://petstore.swagger.io/v2/pet" 
	And content 
		"""
  {
  "id": 0,
  "category": {
    "id": 0,
    "name": "string"
  },
  "name": "doggie",
  "photoUrls": [
    "string"
  ],
  "tags": [
    {
      "id": 0,
      "name": "string"
    }
  ],
  "status": "NotAvailable"
}
  """
	Then response status code should be 200 
	And response body contains 
		| key    | value        |
		| status   | NotAvailable |
	And response type should be "json" 
	And response time is within 300 ms 
	And response status line is "OK"

# Run smoke test across the swagger specification file
@ignore
Feature: Smoke Test Feature 
Scenario: Smoke test the environment 
	Given the Swagger definition at http://petstore.swagger.io/v2/swagger.json 
	Then execute smoke test successfully
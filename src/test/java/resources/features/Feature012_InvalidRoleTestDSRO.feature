#ID: DSRO SERVICE
@InvalidRoleTest 
Feature: Role Based Tests 
Scenario: Invalid Role Handling 

	When user sends a POST request to "https://idmz-dp.ci41.lsf04.ibmwhc.net:9443/dsro/v1/erase/api" 
	And header with 
		| key    | value        |
		| tenantId   | test |
		| IBM-App-User | me |
		| IBM-DP-correlationid | 123456 |
		| X-Client-IP | 1.2.3.4 |
		| X-Forwarded-For | 4.3.2.1 |
		| Accept    | application/json |
	And content 
		"""
           { "patientid" : "123456" }
        """
    # check Forbidden Request error is returned with error code 403
	And response status code should be 403
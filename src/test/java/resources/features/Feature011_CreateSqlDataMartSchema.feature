# TCERID: 5970
# TEST CASE: ANA _ DM Creation of relational database multiple tables with schema.
# Description: The service provides the ability to create one or more relational database tables with a particular schema, 
#via the use of a create datamart REST API, in which a SQL schema is included as part of the request.
@createSqlSchemaDataMart
Feature:  DataMart SQL DDL Schema creation 
Scenario: Create new SQL DDL Schema in Datamart 

	When user sends a POST request to "https://idmz-dp.ci41.lsf04.ibmwhc.net:9443/datamart/api/v1/" 
	And header with 
		| key    | value        |
		| Accept   | application/json |
		| iv-user | hatestdb2 |
	And content 
		"""
{"mode":"private", "name":"test_export", "description":"testing", "owner":"hatestdb2", "datamartType":"db2native", "schema":"CREATE TABLE TEST2 (id bigint NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1), FIELD1 varchar(255) NOT NULL, FIELD2 bigint NOT NULL, PRIMARY KEY (id));" }  

"""
	# set variable data mart id from previous step
	Then response value "id" is stored 
	And response status code should be 201 
	
	# check if asynchronous processing is completed
	# variables tagged {global} would be resolved before execution
	# build the http request object
	When user sends asynchronous GET request to "https://idmz-dp.ci41.lsf04.ibmwhc.net:9443/datamart/api/v1/{id}" 
	And path parameter with 
		| key    | value        |
		| id  | {global} |
	And header with 
		| key    | value        |
		| Accept   | application/json |
		| iv-user | hatestdb2 |
	# keep actively polling till "status" field in response changes to "DONE"
	And waits on "status" to be "Done" 
	And maxTimeOut is 500 seconds 
	# start running assertions on the response
	Then response status code should be 200 
	And response header contains 
		| key    | value        |
		| content-type   | application/json |
	And response status line is "OK" 
	

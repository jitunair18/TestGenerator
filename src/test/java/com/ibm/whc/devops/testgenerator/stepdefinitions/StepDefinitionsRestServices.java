package com.ibm.whc.devops.testgenerator.stepdefinitions;

import org.testng.Assert;

import com.ibm.whc.devops.testgenerator.domain.RestFactory;
import com.ibm.whc.devops.testgenerator.swaggerParserUtility.SwaggerInformation;
import com.ibm.whc.devops.testgenerator.swaggerParserUtility.SwaggerUtility;

import cucumber.api.DataTable;
import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class StepDefinitionsRestServices extends SwaggerInformation {

	RestFactory rt = new RestFactory();

	@SuppressWarnings("unused")
	private SwaggerInformation swaggerInstance;

	public StepDefinitionsRestServices(SwaggerInformation swaggerInformation) {
		this.swaggerInstance = swaggerInformation;
	}
	

	@Given("^.* Swagger definition at (.*)$")
	public void getSwaggerInformation(String swaggerString) throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		SwaggerUtility swaggerUtility = new SwaggerUtility();
		swaggerInstance = swaggerUtility.getSwaggerData(swaggerString);
		
	}

	@When("^a Smoke Test request is made$")
	public void a_Smoke_Test_request_is_made() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		Assert.assertEquals(1, 1);
		
	}

	@When("^user sends a GET request to \"([^\"]*)\"$")
	public void user_sends_a_GET_request_to(String url) throws Throwable {

		// Make a get request
		rt.getRequest(url);
	}
	
	@When("^user sends a DELETE request to \"([^\"]*)\"$")
	public void user_sends_a_DELETE_request_to(String url) throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
		rt.deleteRequest(url);
	}

	@When("^path parameter with$")
	public void path_parameter_with(DataTable arg1) throws Throwable {
		rt.setRequestPathParameter(arg1);
	}
	
	@When("^query parameter with$")
	public void query_parameter_with(DataTable arg1) throws Throwable {
		rt.setRequestQueryParameter(arg1);
	}
	
	@When("^header with$")
	public void header_with(DataTable arg1) throws Throwable {
		rt.setRequestHeader(arg1);
	}

	@Then("^response status code .* (\\d+)$")
	public void response_status_code_should_be(int arg1) throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		rt.verifyStatusCode(arg1);
	}

	@Then("^response type should be \"([^\"]*)\"$")
	public void response_type_should_be(String arg1) throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		rt.verifyResponseType(arg1);
	}

	@Then("^response body contains$")
	public void response_contains(DataTable arg1) throws Throwable {
		rt.verifyResponseData(arg1);
	}
	
	@Then("^response header contains$")
	public void response_header_contains(DataTable arg1) throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
	    // For automatic transformation, change DataTable to one of
	    // List<YourType>, List<List<E>>, List<Map<K,V>> or Map<K,V>.
	    // E,K,V must be a scalar (String, Integer, Date, enum etc)
		rt.verifyResponseHeader(arg1);
	}
	
	@Then("^response cookie contains$")
	public void response_cookie_contains(DataTable arg1) throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
	    // For automatic transformation, change DataTable to one of
	    // List<YourType>, List<List<E>>, List<Map<K,V>> or Map<K,V>.
	    // E,K,V must be a scalar (String, Integer, Date, enum etc)
		rt.verifyResponseCookie(arg1);
	}
	
	
	@Then("^response time is (?:|within|under) (\\w+) (?:|ms|millisecond|milliseconds)$")
	public void response_time_is_within_s(String arg1) throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
		rt.verifyResponseTime(arg1);
		
		
	}@Then("^response status line is \"([^\"]*)\"$")
	public void response_status_line_is(String arg1) throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
	    rt.verifyResponseStatusLine(arg1);
	}
	
	
	@Then("^response value \"([^\"]*)\" is stored$")
	public void response_value_is_stored(String arg1) throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
		rt.setGlobalVariablesfromResponse(arg1);	
	}

	@When("^user sends asynchronous GET request to \"([^\"]*)\"$")
	public void user_sends_asynchronous_GET_request_to(String arg1) throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
	    rt.getAsynchronousRequest(arg1);
	}

	@When("^waits on \"([^\"]*)\" to be \"([^\"]*)\"$")
	public void waits_on_to_be(String arg1, String arg2) throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
	    rt.setAsynchronousRequestParameters(arg1,arg2);
	}

	@When("^maxTimeOut is (\\d+) seconds$")
	public void maxtimeout_is_ms(int arg1) throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
		rt.setAsynchronousRequestTimeOutParameters(arg1);
	}
	
	

	@When("^user sends a POST request to \"([^\"]*)\"$")
	public void user_sends_a_POST_request_to(String arg1) throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		rt.postRequest(arg1);
	}
	
	@When("^user sends a PUT request to \"([^\"]*)\"$")
	public void user_sends_a_PUT_request_to(String arg1) throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
		rt.putRequest(arg1);
	}

	@When("^content$")
	public void content(String arg1) throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		rt.postContent(arg1);
	}

	// SMOKE TEST SECTION
	@Then("^execute smoke test successfully$")
	public void execute_smoke_test_successfully() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}

}

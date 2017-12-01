package com.ibm.whc.devops.testgenerator.stepdefinitions;

import com.ibm.whc.devops.testgenerator.domain.utility.Log;
import com.ibm.whc.devops.testgenerator.swaggerParserUtility.SwaggerInformation;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;

public class Hooks {

	@SuppressWarnings("unused")
	private SwaggerInformation swaggerInformation;
	

	@Before
	public void before(Scenario scenario) {
		Log.info("********** START SCENARIO EXECUTION **********: " + scenario.getName());
		

	}

	@After
	public void after(Scenario scenario) {
		Log.info("TEST SCENARIO STATUS: " + scenario.getStatus().toUpperCase());
		Log.info("********** END SCENARIO EXECUTION **********: " + scenario.getName());
		


	}

}

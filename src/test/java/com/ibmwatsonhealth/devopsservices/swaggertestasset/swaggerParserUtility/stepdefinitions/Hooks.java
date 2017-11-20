package com.ibmwatsonhealth.devopsservices.swaggertestasset.swaggerParserUtility.stepdefinitions;

import com.ibmwatsonhealth.devopsservices.swaggertestasset.swaggerParserUtility.SwaggerInformation;
import com.ibmwatsonhealth.devopsservices.swaggertestasset.swaggerParserUtility.domain.utility.Log;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;

public class Hooks {

	@SuppressWarnings("unused")
	private SwaggerInformation swaggerInformation;
	

	@Before
	public void before(Scenario scenario) {
		Log.info("START SCENARIO *****: " + scenario.getName());

	}

	@After
	public void after(Scenario scenario) {
		Log.info("END SCENARIO *****: " + scenario.getName());

	}

}

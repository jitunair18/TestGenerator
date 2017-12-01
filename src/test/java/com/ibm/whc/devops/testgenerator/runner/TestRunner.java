package com.ibm.whc.devops.testgenerator.runner;

import cucumber.api.CucumberOptions;
import cucumber.api.testng.TestNGCucumberRunner;
import java.lang.reflect.Field;
import cucumber.api.testng.CucumberFeatureWrapper;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;
import org.apache.log4j.xml.DOMConfigurator;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

@CucumberOptions(features = "src/test/java/resources/features", glue = {
		"com.ibmwatsonhealth.devopsservices.swaggertestasset.swaggerParserUtility.stepdefinitions" }, tags = {
				"~@ignore" }, dryRun = false, format = { "pretty", "html:target/cucumber-reports/cucumber-pretty",
						"json:target/cucumber-reports/CucumberTestReport.json",
						"rerun:target/cucumber-reports/rerun.txt" })
public class TestRunner {
	private TestNGCucumberRunner testNGCucumberRunner;
	

	
	@Parameters ({"featurePath"})
	@BeforeClass()
	public void setUpClass(String featurePath) throws Exception {
		testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());
		DOMConfigurator.configure("log4j.xml"); // configure logging for the framework
		
		Class<?> testClass = this.getClass();
	    changeCucumberAnnotation(testClass, "features", new String [] {featurePath});       
	    testNGCucumberRunner = new TestNGCucumberRunner(testClass); 
	

	}
	
	@SuppressWarnings("unchecked")
	private static void changeCucumberAnnotation(Class<?> clazz, String key, Object newValue) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{  
	    Annotation options = clazz.getAnnotation(CucumberOptions.class);                   //get the CucumberOptions annotation  
	    InvocationHandler proxyHandler = Proxy.getInvocationHandler(options);              //setup handler so we can update Annotation using reflection. Basically creates a proxy for the Cucumber Options class
	    Field f = proxyHandler.getClass().getDeclaredField("memberValues");                //the annotaton key/values are stored in the memberValues field
	    f.setAccessible(true);                                                             //suppress any access issues when looking at f
	    Map<String, Object> memberValues = (Map<String, Object>) f.get(proxyHandler);      //get the key-value map for the proxy
	    memberValues.remove(key);                                                          //renove the key entry...don't worry, we'll add it back
	    memberValues.get(key);
	    memberValues.put(key,newValue);                                                    //add the new key-value pair. The annotation is now updated.
	}//end method

	
	@Test(groups = "cucumber", description = "Runs Cucumber Feature", dataProvider = "features")
	public void feature(CucumberFeatureWrapper cucumberFeature) {
		testNGCucumberRunner.runCucumber(cucumberFeature.getCucumberFeature());
		
		
		/*
		testNGCucumberRunner.getFeatures();
		List<CucumberFeature> featureList = testNGCucumberRunner.getFeatures();

		for (CucumberFeature feature : featureList) {
			System.out.println(feature.getPath()); // Retrieve name of the feature here

		}
		*/

	}

	@DataProvider
	public Object[][] features() {
		return testNGCucumberRunner.provideFeatures();
	}

	@AfterClass(alwaysRun = true)
	public void tearDownClass() throws Exception {
		testNGCucumberRunner.finish();
	}
}

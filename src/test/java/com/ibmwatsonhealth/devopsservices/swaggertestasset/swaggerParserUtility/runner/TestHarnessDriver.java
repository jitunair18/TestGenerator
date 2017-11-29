package com.ibmwatsonhealth.devopsservices.swaggertestasset.swaggerParserUtility.runner;

import java.util.Arrays;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

public class TestHarnessDriver {

	
	public static void main(String[] args) {
		
		/*
		TestNG testng = new TestNG();
		List<String> suites = Lists.newArrayList();
		suites.add(".//testng.xml");
		testng.setTestSuites(suites);
		*/
		TestNG testng = new TestNG();
        XmlTest xmlTest = new XmlTest();
        xmlTest.setName("Test Feature");
        System.out.println(args[0]);
        xmlTest.addParameter("featurePath", args[0]);
        xmlTest.setClasses(Arrays.asList(new XmlClass(TestRunner.class)));
        XmlSuite xmlSuite = new XmlSuite();
        xmlSuite.setName("Regression Suite");
        xmlTest.setSuite(xmlSuite);
        xmlSuite.setTests(Arrays.asList(xmlTest));
        testng.setXmlSuites(Arrays.asList(xmlSuite));
        testng.run();
        System.out.println("TESTNG Scenario Execution Status:" + testng.getStatus());
        System.exit(testng.getStatus());
		
	}
	

}

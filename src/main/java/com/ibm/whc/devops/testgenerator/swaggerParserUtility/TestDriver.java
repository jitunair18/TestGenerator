package com.ibm.whc.devops.testgenerator.swaggerParserUtility;

import java.util.List;

import org.testng.TestNG;
import org.testng.collections.Lists;

public class TestDriver {

	public static void main(String[] args) {
		TestNG testng = new TestNG();
		List<String> suites = Lists.newArrayList();
		suites.add(".//testng.xml");
		testng.setTestSuites(suites);
		testng.run();
	}

}

package com.ibm.whc.devops.testgenerator.swaggerParserUtility;

import org.testng.annotations.Test;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;

@Listeners(TestNGTestSuite.TestMethodSkipper.class)
public class TestNGTestSuite implements ITest {

	private static Map<Object, Object> map;
	protected String mTestCaseName = "";

	@SuppressWarnings("rawtypes")
	@DataProvider(name = "dbconfig")
	public Object[][] provideDbConfig(ITestContext context) {

		SwaggerUtility swaggerDocument = new SwaggerUtility();
		SwaggerInformation instance = swaggerDocument.getSwaggerData(context.getCurrentXmlTest().getParameter("swaggerPath"));
		map = instance.swaggerInformation;
		// String swaggerString = SwaggerUtility.readFile("Data.properties");
		// map = SwaggerUtility.getSwaggerData(swaggerString);
		System.out.println("*********Total endpoints available is ***************: " + map.size());

		Object[][] arr = null;
		try {
			arr = new Object[map.size()][2];
			Set entries = map.entrySet();
			Iterator entriesIterator = entries.iterator();
			int i = 0;
			while (entriesIterator.hasNext()) {

				Map.Entry mapping = (Map.Entry) entriesIterator.next();
				EndpointOperationType endpointtype = (EndpointOperationType) mapping.getKey();
				arr[i][0] = endpointtype.getEndpoint() + "," + endpointtype.getOperation();
				arr[i][1] = mapping.getValue();
				i++;
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return arr;
	}

	@Test
	public void swaggerTestServiceConfigurationStatus() {
		if (map == null) {
			Assert.fail(
					"Swagger Test service could not be executed for input swagger document. Check authorisation and access route to the Swagger Test Service");
		}

	}

	// Run tests once for every endpoint and operation combination
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test(dataProvider = "dbconfig")
	public void executeRestfulTest(String endpoint, Object obj) {

		try {
			String endpointOutput = endpoint.split(",")[0];
			String endpointOperationOutput = endpoint.split(",")[1];
			Map<Object, Object> dataMap = (Map) obj;
			dataMap.get("response");
			String url = dataMap.get("baseURI") + endpointOutput;
			System.out.println("Hit Endpoint:------->" + url);
			System.out.println("Hit EndpointOperation:------>" + endpointOperationOutput.toLowerCase());
			RequestSpecification request = null;

			switch (endpointOperationOutput.toLowerCase()) {

			case "get":
				// if (!((String)
				// dataMap.get("skip")).toLowerCase().equals("true")) {

				if (dataMap.get("hasMandatoryParameterResolved") == "true") {
					// check the type of request
					if (dataMap.get("external") == "true") {
						request = requestBuilder(dataMap, "external", "valid");
					} else {
						request = requestBuilder(dataMap, "internal", "valid");

					}

					// Parse and assert the response here
					if (request != null) {
						Response rep = request.when().get(url);

						Map<String, Object> responseMap = (Map<String, Object>) dataMap.get("response");
						if (!responseMap.isEmpty()) {
							if (responseMap.containsKey("statuscode")) {
								Assert.assertEquals(rep.getStatusCode(),
										Integer.parseInt((String) responseMap.get("statuscode")));
							} else {
								Assert.assertEquals(rep.getStatusCode(), HttpStatus.SC_OK);
							}
						} else {
							Assert.assertEquals(rep.getStatusCode(), HttpStatus.SC_OK);
						}
					} else {
						Assert.fail("Request was not successfully generated for " + endpointOutput);
					}
				} else {
					if (dataMap.containsKey("contentAsString")) {
						Assert.fail(
								"Mandatory Parameters not resolved successfully in external test data model for request: "
										+ dataMap.get("contentAsString"));
					} else {
						Assert.fail(
								"Mandatory Parameters not resolved successfully in Swagger Specification file for path: "
										+ endpointOutput + " operation: " + endpointOperationOutput);
					}
				}
				// } else {
				// if (dataMap.containsKey("contentAsString")) {
				// Assert.fail("Test case skipped for request: " +
				// dataMap.get("contentAsString"));
				// } else {
				// Assert.fail("Test case skipped for request: " +
				// endpointOutput + " operation: "
				// + endpointOperationOutput);
				// }

				// }

				break;

			case "post":

				// Start new post here
				if (dataMap.get("hasMandatoryParameterResolved").equals("true")) {
					// check the type of request
					if (dataMap.get("external") == "true") {
						request = requestBuilder(dataMap, "external", "valid");
					} else {
						request = requestBuilder(dataMap, "internal", "valid");

					}

					// Parse and assert the response here
					if (request != null) {
						Response rep = request.when().post(url);
						Map<String, Object> responseMap = (Map<String, Object>) dataMap.get("response");
						if (!responseMap.isEmpty()) {
							if (responseMap.containsKey("statuscode")) {
								Assert.assertEquals(rep.getStatusCode(), responseMap.get("statuscode"));
							} else {
								Assert.assertEquals(rep.getStatusCode(), HttpStatus.SC_OK);
							}
						} else {
							Assert.assertEquals(rep.getStatusCode(), HttpStatus.SC_OK);
						}
					} else {
						Assert.fail("Request was not successfully generated for " + endpointOutput);
					}

					// Run request for invalid data once again if internal data
					// model is present
					if (!(dataMap.get("external").equals("true"))) {

						request = requestBuilder(dataMap, "internal", "invalid");

						// Parse and assert the response here
						if (request != null) {
							Response rep = request.when().post(url);
							Map<String, Object> responseMap = (Map<String, Object>) dataMap.get("response");
							if (!responseMap.isEmpty()) {
								if (responseMap.containsKey("statuscode")) {
									Assert.assertEquals(rep.getStatusCode(), responseMap.get("statuscode"));
								} else {
									Assert.assertEquals(rep.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
								}
							} else {
								Assert.assertEquals(rep.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
							}
						} else {
							Assert.fail("Request was not successfully generated for " + endpointOutput);
						}

					}

				} else {
					if (dataMap.containsKey("contentAsString")) {
						Assert.fail(
								"Mandatory Parameters not resolved successfully in external test data model for request: "
										+ dataMap.get("contentAsString"));
					} else {
						Assert.fail(
								"Mandatory Parameters not resolved successfully in Swagger Specification file for path: "
										+ endpointOutput + " operation: " + endpointOperationOutput);
					}
				}

				// end post operation here
				break;

			case "put":

				// Start new put operation here
				if (dataMap.get("hasMandatoryParameterResolved").equals("true")) {
					// check the type of request
					if (dataMap.get("external") == "true") {
						request = requestBuilder(dataMap, "external", "valid");
					} else {
						request = requestBuilder(dataMap, "internal", "valid");

					}

					// Parse and assert the response here
					if (request != null) {
						Response rep = request.when().put(url);
						Map<String, Object> responseMap = (Map<String, Object>) dataMap.get("response");
						if (!responseMap.isEmpty()) {
							if (responseMap.containsKey("statuscode")) {
								Assert.assertEquals(rep.getStatusCode(), responseMap.get("statuscode"));
							} else {
								Assert.assertEquals(rep.getStatusCode(), HttpStatus.SC_OK);
							}
						} else {
							Assert.assertEquals(rep.getStatusCode(), HttpStatus.SC_OK);
						}
					} else {
						Assert.fail("Request was not successfully generated for " + endpointOutput);
					}

					// Run request for invalid data once again if internal data
					// model is present
					if (!(dataMap.get("external").equals("true"))) {

						request = requestBuilder(dataMap, "internal", "invalid");

						// Parse and assert the response here
						if (request != null) {
							Response rep = request.when().put(url);
							Map<String, Object> responseMap = (Map<String, Object>) dataMap.get("response");
							if (!responseMap.isEmpty()) {
								if (responseMap.containsKey("statuscode")) {
									Assert.assertEquals(rep.getStatusCode(), responseMap.get("statuscode"));
								} else {
									Assert.assertEquals(rep.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
								}
							} else {
								Assert.assertEquals(rep.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
							}
						} else {
							Assert.fail("Request was not successfully generated for " + endpointOutput);
						}

					}

				} else {
					if (dataMap.containsKey("contentAsString")) {
						Assert.fail(
								"Mandatory Parameters not resolved successfully in external test data model for request: "
										+ dataMap.get("contentAsString"));
					} else {
						Assert.fail(
								"Mandatory Parameters not resolved successfully in Swagger Specification file for path: "
										+ endpointOutput + " operation: " + endpointOperationOutput);
					}
				}

				// end post operation here

				break;

			case "delete":

				if (dataMap.get("hasMandatoryParameterResolved") == "true") {
					// check the type of request
					if (dataMap.get("external") == "true") {
						request = requestBuilder(dataMap, "external", "valid");
					} else {
						request = requestBuilder(dataMap, "internal", "valid");

					}

					// Parse and assert the response here
					if (request != null) {
						Response rep = request.when().delete(url);
						Map<String, Object> responseMap = (Map<String, Object>) dataMap.get("response");
						if (!responseMap.isEmpty()) {
							if (responseMap.containsKey("statuscode")) {
								Assert.assertEquals(rep.getStatusCode(), responseMap.get("statuscode"));
							} else {
								Assert.assertEquals(rep.getStatusCode(), HttpStatus.SC_OK);
							}
						} else {
							Assert.assertEquals(rep.getStatusCode(), HttpStatus.SC_OK);
						}
					} else {
						Assert.fail("Request was not successfully generated for " + endpointOutput);
					}
				} else {
					if (dataMap.containsKey("contentAsString")) {
						Assert.fail(
								"Mandatory Parameters not resolved successfully in external test data model for request: "
										+ dataMap.get("contentAsString"));
					} else {
						Assert.fail(
								"Mandatory Parameters not resolved successfully in Swagger Specification file for path: "
										+ endpointOutput + " operation: " + endpointOperationOutput);
					}
				}
				break;
			default:
				Assert.fail("SWAGGER SERVICE FAILURE: Current version does not support operation "
						+ endpointOperationOutput.toLowerCase());
			}

		} 
		catch(NullPointerException e) {
			e.printStackTrace();
			Assert.fail("DOCUMENTATION FAILURE: Swagger specification is missing data format consumed by request");
		}
		
		catch (Exception e) {
			e.printStackTrace();
			Assert.fail(" SWAGGER SERVICE FAILURE: Something went wrong with the test method");
		}
	}

	@BeforeMethod(alwaysRun = true)
	public void testData(Method method, Object[] testData) {

		if (testData != null && !(method.getName().toLowerCase().equals("swaggertestserviceconfigurationstatus"))) {
			this.mTestCaseName = (String) testData[0];
		} else {
			this.mTestCaseName = method.getName();
		}

	}

	@Override
	public String getTestName() {
		return this.mTestCaseName;
	}

	public static boolean hasPathParameterResolved(String endPoint) {

		if (endPoint.toLowerCase().contains("{") || endPoint.toLowerCase().contains("}")) {
			return false;
		} else {
			return true;
		}

	}

	@SuppressWarnings("unchecked")
	public static RequestSpecification requestBuilder(Map<Object, Object> dataMap, String inputDataModel,
			String requestType) {
		RequestSpecification request = null;
		request = RestAssured.given();

		// 1. check for accept of output content
		if (dataMap.containsKey("accept")) {
			request = request.accept((String) dataMap.get("accept"));
		}

		// 2. check for basic authentication
		if (dataMap.containsKey("basicauthentication")) {
			Map<String, Object> basicauthenticationMap = (Map<String, Object>) dataMap.get("queryParameters");
			if (!basicauthenticationMap.isEmpty()) {
				request = request.auth().basic((String) basicauthenticationMap.get("username"),
						(String) basicauthenticationMap.get("password"));
			}
		}

		// 3. Check for Body parameters from external data or internal data

		if (inputDataModel.equals("external")) {
			if (dataMap.containsKey("bodyParameters")) {
				Map<String, Object> bodyParameterMap = (Map<String, Object>) dataMap.get("bodyParameters");
				if (!bodyParameterMap.isEmpty()) {
					request = request.contentType((String) bodyParameterMap.get("content-type"));
					request = request.body((String) bodyParameterMap.get("request"));
				}
			}
		} else {
			if (requestType.equals("valid")) {
				if (dataMap.containsKey("validdata")) {
					Map<String, Object> bodyMap = (Map<String, Object>) dataMap.get("validdata");
					if (!bodyMap.isEmpty() && bodyMap != null) {
						String requestschema = (String) bodyMap.get("schema");
						request = request.contentType((String) bodyMap.get("schema"));
						String requestbody = (String) bodyMap.get("data");
						request = request.body((String) bodyMap.get("data"));
						System.out.println(requestschema);
						System.out.print(requestbody);
					} else {

						if (dataMap.containsKey("contentAsString")) {
							Assert.fail(
									"Swagger does not mention type of data consumed for request: "
											+ dataMap.get("contentAsString"));
						}

					}
				}
			} else {
				if (dataMap.containsKey("invaliddata")) {
					Map<String, Object> bodyMap = (Map<String, Object>) dataMap.get("invaliddata");
					if (!bodyMap.isEmpty()) {
						request = request.contentType((String) bodyMap.get("schema"));
						request = request.body((String) bodyMap.get("data"));
					}
				}
			}

		}

		// 4. Handle query parameters
		if (dataMap.containsKey("queryParameters")) {
			Map<String, Object> queryParameterMap = (Map<String, Object>) dataMap.get("queryParameters");
			if (!queryParameterMap.isEmpty()) {
				request = request.queryParams(queryParameterMap);
			}
		}

		// 5. Handle path parameters
		if (dataMap.containsKey("pathParameters")) {
			Map<String, Object> pathParameterMap = (Map<String, Object>) dataMap.get("pathParameters");
			if (!pathParameterMap.isEmpty()) {
				request = request.pathParams(pathParameterMap);
			}
		}

		// 6. Handle cookie parameters
		if (dataMap.containsKey("cookieParameters")) {
			Map<String, Object> cookieParameterMap = (Map<String, Object>) dataMap.get("cookieParameters");
			if (!cookieParameterMap.isEmpty()) {
				request = request.cookies(cookieParameterMap);
			}
		}

		// 7. Handle header parameters
		if (dataMap.containsKey("headerParameters")) {
			Map<String, Object> headerParameterMap = (Map<String, Object>) dataMap.get("headerParameters");
			if (!headerParameterMap.isEmpty()) {
				request = request.headers(headerParameterMap);
			}
		}

		return request;
	}

	public static ResponseSpecification responseBuilder(Map<Object, Object> dataMap) {
		ResponseSpecification response = null;
		return response;
	}

	// Method listener to check for method skips at runtime
	public static class TestMethodSkipper implements IInvokedMethodListener {

		@SuppressWarnings("unchecked")
		@Override
		public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
			Map<String, Object> dataMap = new HashMap<String, Object>();
			String mname = method.getTestMethod().getConstructorOrMethod().getName();
			Object[] skipName = method.getTestResult().getParameters();
			if (method.isTestMethod()) {

				if (skipName.length != 0) {
					dataMap = (Map<String, Object>) skipName[1];
					if (((String) dataMap.get("skip")).toLowerCase().equals("true")) {
						throw new SkipException(mname + " was configured to be skipped.");
					} else {
						System.out.println("This test will not be skipped");
					}
				}
			}
		}

		@Override
		public void afterInvocation(IInvokedMethod method, ITestResult testResult) {

		}

		@AfterClass
		public void tearDown() {
			map = null;
			System.gc();
		}
	}

}

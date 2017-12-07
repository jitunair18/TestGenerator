package com.ibm.whc.devops.testgenerator.domain;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.xml.sax.InputSource;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.whc.devops.testgenerator.domain.utility.Log;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.config.SSLConfig;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.response.ResponseBody;
import com.jayway.restassured.specification.RequestSpecification;
import cucumber.api.DataTable;

@SuppressWarnings("deprecation")
public class RestFactory {

	static RequestSpecification httpRequest = null;
	static Response httpResponse = null;
	static String responseString = null;
	static String requestType = null;
	static String getURL = "";
	static Map<String, Object> globalDataDictionary = new HashMap<String, Object>();
	static Map<String, Object> responseDictionary = new HashMap<String, Object>();

	/**
	 * Makes http GET request
	 * 
	 * @param url
	 *            URL for endpoint to be hit
	 * @return void
	 */
	public void getRequest(String url) throws ClientProtocolException, IOException {

		System.out.println("Entered method: getRequest()");
		Log.info("Endpoint Request url : " + url);
		getURL = url;
		// make a get request
		requestType = "GET";
		httpRequest = null;
		httpResponse = null;
		httpRequest = RestAssured.given().config(getSSLCert());

	}

	/**
	 * Verify http response status code
	 * 
	 * @param statusCode
	 *            Response status code defined in feature file
	 * @return void
	 */
	public void verifyStatusCode(int statusCode) throws ClientProtocolException, IOException {

		System.out.println("Entered method: verifyStatusCode()");
		Log.info("Expected response status code: " + statusCode);

		if (httpResponse != null) {
			assertEquals(statusCode, httpResponse.getStatusCode());

		} else {
			createResponseObject();
			assertEquals(httpResponse.getStatusCode(), statusCode);

		}
		Log.info("Actual response status code: " + httpResponse.getStatusCode());

	}

	/**
	 * Verify http expected response content type
	 * 
	 * @param type
	 *            Response content type defined in feature file
	 * @return void
	 */
	public void verifyResponseType(String type) {
		System.out.println("Entered method: verifyResponseType()");
		Log.info("Expected response type: " + type);
		String mimeType = null;

		if (httpResponse != null) {
			mimeType = httpResponse.getContentType();

		} else {
			createResponseObject();
			mimeType = httpResponse.getContentType();
		}
		Log.info("Actual response type: " + type);
		if (mimeType.toUpperCase().contains(type.toUpperCase())) {
			assertTrue(true);
		} else {
			fail("Actual response type: " + mimeType + " Expected response type: " + type);
			Log.fatal("Actual response type: " + mimeType + " Expected response type: " + type);
		}

	}

	/**
	 * Verify http expected response content
	 * 
	 * @param Datable
	 *            Datatable payload with expected keys / xpath / json path expected
	 *            in http response object defined in feature file
	 * @return void
	 */
	public void verifyResponseData(DataTable payloadTable) throws ParseException, IOException {
		System.out.println("Entered method: verifyResponseData()");
		ResponseBody<?> body = null;

		List<List<String>> payload = payloadTable.raw();

		if (httpResponse != null) {

			body = httpResponse.getBody();
			// assertTrue(body.asString().contains(responseData));
		} else {
			createResponseObject();
			body = httpResponse.getBody();
			// assertTrue(body.asString().contains(responseData));
		}
		Log.info("Actual response body generated: " + body);
		for (int i = 1; i < payload.size(); i++) {

			String key = payload.get(i).get(0);
			String value = payload.get(i).get(1);
			if (httpResponse.getContentType().contains("json")) {
				String rbody = body.asString();
				System.out.println(rbody);
				JsonPath jp = new JsonPath(rbody);
				String jsonValue = jp.getString(key);
				assertEquals(jsonValue, value);
			} else if (httpResponse.getContentType().contains("xml")) {

			}

		}

	}

	/**
	 * Verify http expected response headers
	 * 
	 * @param Datable
	 *            Datatable payload with expected response header key value pairs in
	 *            http response object defined in feature file
	 * @return void
	 */
	public void verifyResponseHeader(DataTable payloadTable) throws ParseException, IOException {
		System.out.println("Entered method: verifyResponseHeader()");

		List<List<String>> payload = payloadTable.raw();

		if (httpResponse != null) {

		} else {
			createResponseObject();

		}
		for (int i = 1; i < payload.size(); i++) {

			String sResponseHeaderKey = payload.get(i).get(0);
			String expectedResponseHeaderValue = payload.get(i).get(1);
			String actualResponseHeaderValue = httpResponse.getHeader(sResponseHeaderKey);
			assertEquals(actualResponseHeaderValue.toUpperCase(), expectedResponseHeaderValue.toUpperCase());

		}

	}

	/**
	 * Verify http expected response cookies
	 * 
	 * @param Datable
	 *            Datatable payload with expected response cookie key value pairs in
	 *            http response object defined in feature file
	 * @return void
	 */
	public void verifyResponseCookie(DataTable payloadTable) throws ParseException, IOException {
		System.out.println("Entered method: verifyResponseCookie()");

		List<List<String>> payload = payloadTable.raw();

		if (httpResponse != null) {

		} else {
			createResponseObject();
		}
		for (int i = 1; i < payload.size(); i++) {

			String sResponseCookieKey = payload.get(i).get(0);
			String expectedResponseCookieValue = payload.get(i).get(1);
			String actualResponseCookieValue = httpResponse.getCookie(sResponseCookieKey);
			assertEquals(actualResponseCookieValue.toUpperCase(), expectedResponseCookieValue.toUpperCase());

		}

	}

	/**
	 * Verify http expected response time for http Response
	 * 
	 * @param sTime
	 *            Expected response time for endpoint defined in feature file
	 * @return void
	 */
	public void verifyResponseTime(String sTime) throws ParseException, IOException {
		System.out.println("Entered method: verifyResponseCookie()");
		long actualResponseTime = 0;
		if (httpResponse != null) {
			actualResponseTime = httpResponse.getTimeIn(TimeUnit.MILLISECONDS);
			// assertTrue(body.asString().contains(responseData));
		} else {
			createResponseObject();
			actualResponseTime = httpResponse.getTimeIn(TimeUnit.MILLISECONDS);

		}
		Log.info("Actual Endpoint response time: " + actualResponseTime);
		long expectedResponseTime = Long.parseLong(sTime);
		if (actualResponseTime <= expectedResponseTime) {
			assertTrue(true);
		} else {
			fail("Actual response Time MILLISECONDS: " + actualResponseTime + " Expected response time MILLISECONDS: "
					+ expectedResponseTime);
			Log.fatal("Actual response Time MILLISECONDS: " + actualResponseTime
					+ " Expected response time MILLISECONDS: " + expectedResponseTime);

		}

	}

	/**
	 * Verify http expected response status line / message
	 * 
	 * @param sExpectedResponseStatusLine
	 *            Expected response status line for endpoint defined in feature file
	 * @return void
	 */
	public void verifyResponseStatusLine(String sExpectedResponseStatusLine) throws ParseException, IOException {
		System.out.println("Entered method: verifyResponseStatusLine()");
		Log.info("Expected response status line: " + sExpectedResponseStatusLine);
		String sActualResponseStatusLine = null;
		if (httpResponse != null) {
			sActualResponseStatusLine = httpResponse.getStatusLine();
			// assertTrue(body.asString().contains(responseData));
		} else {
			createResponseObject();
			sActualResponseStatusLine = httpResponse.getStatusLine();

		}
		Log.info("Actual response status line: " + sActualResponseStatusLine);
		if (sActualResponseStatusLine.toUpperCase().contains(sExpectedResponseStatusLine.toUpperCase())) {
			assertTrue(true);

		} else {
			fail("Actual response status line: " + sActualResponseStatusLine + "Expected response status line: "
					+ sExpectedResponseStatusLine);
			Log.fatal("Actual response status line: " + sActualResponseStatusLine + "Expected response status line: "
					+ sExpectedResponseStatusLine);
		}

	}

	/**
	 * Makes http POST request
	 * 
	 * @param url
	 *            Http url for endpoint to be hit
	 * @return void
	 */
	public void postRequest(String url) throws ClientProtocolException, IOException {
		System.out.println("Entered method: postRequest()");
		Log.info("Endpoint Request url : " + url);

		getURL = url;
		requestType = "POST";
		httpRequest = null;
		httpResponse = null;
		httpRequest = RestAssured.given().config(getSSLCert());

	}

	/**
	 * Makes http PUT request
	 * 
	 * @param url
	 *            Http url for endpoint to be hit
	 * @return void
	 */
	public void putRequest(String url) throws ClientProtocolException, IOException {
		System.out.println("Entered method: putRequest()");
		Log.info("Endpoint Request url : " + url);

		getURL = url;
		requestType = "PUT";
		httpRequest = null;
		httpResponse = null;
		httpRequest = RestAssured.given().config(getSSLCert());

	}

	/**
	 * Makes http DELETE request
	 * 
	 * @param url
	 *            Http url for endpoint to be hit
	 * @return void
	 */
	public void deleteRequest(String url) throws ClientProtocolException, IOException {
		System.out.println("Entered method: deleteRequest()");
		Log.info("Endpoint Request url : " + url);

		getURL = url;
		requestType = "DELETE";
		httpRequest = null;
		httpResponse = null;
		httpRequest = RestAssured.given().config(getSSLCert());

	}

	/**
	 * Makes http PATCH request
	 * 
	 * @param url
	 *            Http url for endpoint to be hit
	 * @return void
	 */
	public void patchRequest(String url) throws ClientProtocolException, IOException {
		System.out.println("Entered method: patchRequest()");
		Log.info("Endpoint Request url : " + url);

		getURL = url;
		requestType = "PATCH";
		httpRequest = null;
		httpResponse = null;
		httpRequest = RestAssured.given().config(getSSLCert());

	}

	/**
	 * Message content to be posted to the http endpoint
	 * 
	 * @param message
	 *            Message content body for endpoint
	 * @return void
	 */
	public void postContent(String message) {
		System.out.println("Entered method: postContent()");
		Log.info("Message content POSTED for endpoint: " + message);
		globalDataDictionary.put("requestContent", message);

		String messageType = getMsgType(message);
		switch (messageType.toUpperCase()) {

		case "JSON":
			httpRequest.contentType("application/json");
			break;
		case "XML":
			httpRequest.contentType("application/xml");
			break;
		case "PLAINTEXT":
			httpRequest.contentType("text/plain");
			break;
		case "CSVTEXT":
			httpRequest.contentType("text/csv");
			break;
		case "PDF":
			httpRequest.contentType("application/pdf");
			break;
		case "MULTIPARTFORM":
			httpRequest.contentType("multipart/form-data");
			break;
		case "MULTIPARTMIXED":
			httpRequest.contentType("multipart/mixed");
			break;
		case "MULTIPARTALT":
			httpRequest.contentType("multipart/alternative");
			break;
		case "MULTIPARTREL":
			httpRequest.contentType("multipart/related");
			break;

		default:
			fail("Invalid message type passed in scenario content");
			Log.fatal("Unsupported message type found");
			break;

		}
		httpRequest.body(message);

	}

	/**
	 * Set path parameters in http request object
	 * 
	 * @param Datatable
	 *            Datatable with path parameter payload defined in feature file
	 * @return void
	 */
	public void setRequestPathParameter(DataTable pathParameterPayload) {

		System.out.println("Entered method: setRequestPathParameter()");

		List<List<String>> payload = pathParameterPayload.raw();
		for (int i = 1; i < payload.size(); i++) {

			System.out.println(payload.get(i));
			String parameterName = payload.get(i).get(0);
			String parameterValue = payload.get(i).get(1);
			if (parameterValue.toUpperCase().contains("GLOBAL")) {
				parameterValue = resolveGlobalVariable(parameterName);

			}
			Log.info("Path parameter: " + parameterName + " Value: " + parameterValue);
			httpRequest.pathParam(parameterName, parameterValue);

		}

	}

	/**
	 * Set query paremeters in http request object
	 * 
	 * @param Datatable
	 *            Datatable with query parameter payload defined in feature file
	 * @return void
	 */
	public void setRequestQueryParameter(DataTable queryParameterPayload) {
		System.out.println("Entered method: setRequestQueryParameter()");

		List<List<String>> payload = queryParameterPayload.raw();
		for (int i = 1; i < payload.size(); i++) {

			System.out.println(payload.get(i));
			String queryParameterName = payload.get(i).get(0);
			String queryParameterValue = payload.get(i).get(1);
			httpRequest.queryParam(queryParameterName, queryParameterValue);
			Log.info("Query parameter: " + queryParameterName + " Value: " + queryParameterValue);

		}

	}

	/**
	 * Set headers in http request object
	 * 
	 * @param Datatable
	 *            Datatable with request header parameters defined in feature file
	 * @return void
	 */
	public void setRequestHeader(DataTable headerParameterPayload) {
		System.out.println("Entered method: setRequestHeader()");

		List<List<String>> payload = headerParameterPayload.raw();
		for (int i = 1; i < payload.size(); i++) {

			String headerParameterName = payload.get(i).get(0);
			String headerParameterValue = payload.get(i).get(1);
			httpRequest.header(headerParameterName, headerParameterValue);
			Log.info("Request header : " + headerParameterName + " Value: " + headerParameterValue);
		}

	}

	/**
	 * Verify content type of message body
	 * 
	 * @param message
	 *            Message content json/xml/plain text
	 * @return Output message type of given input message
	 */
	public String getMsgType(String message) {
		System.out.println("Entered method: getMsgType()");
		String returnContent = null;
		try {
			new ObjectMapper().readTree(message);
			returnContent = "JSON";
			return returnContent;
		} catch (IOException e) {
			returnContent = "INVALID";

		}

		try {
			DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(message)));

			returnContent = "XML";
		} catch (Exception e) {
			returnContent = "INVALID";

		}

		return returnContent;
	}

	/**
	 * Initialize Http response object
	 * 
	 * @param void
	 * @return void
	 * @throws InterruptedException
	 * @throws NumberFormatException
	 */
	public void createResponseObject() {
		System.out.println("Entered method: createResponseObject()");

		switch (requestType.toUpperCase()) {

		case "GET":
			httpResponse = httpRequest.when().get(getURL);
			System.out.println("Actual Response content: " + httpResponse.getBody().asString());
			Log.info("Actual Response content: " + httpResponse.getBody().asString());
			break;

		case "POST":
			httpResponse = httpRequest.when().post(getURL);
			System.out.println(" Actual Response content: " + httpResponse.getBody().asString());
			Log.info("Actual Response content: " + httpResponse.getBody().asString());
			break;

		case "PUT":
			httpResponse = httpRequest.when().put(getURL);
			System.out.println("Actual Response content: " + httpResponse.getBody().asString());
			Log.info("Actual Response content: " + httpResponse.getBody().asString());
			break;

		case "DELETE":
			httpResponse = httpRequest.when().delete(getURL);
			System.out.println("Actual Response content: " + httpResponse.getBody().asString());
			Log.info("Actual Response content: " + httpResponse.getBody().asString());
			break;

		case "PATCH":
			httpResponse = httpRequest.when().patch(getURL);
			System.out.println("Actual Response content: " + httpResponse.getBody().asString());
			Log.info("Actual Response content: " + httpResponse.getBody().asString());
			break;

		case "ASYNCHRONOUSGET":
			try {
				String responseParameter = (String) globalDataDictionary.get("responseWaitParameterName");
				String responseParameterValue = (String) globalDataDictionary.get("responseWaitParameterValue");
				int responseWaitTime = ((int) globalDataDictionary.get("responseWaitTime"));
				httpResponse = waitForApiResponse(responseParameter, responseParameterValue, responseWaitTime);
				Log.info("Final Response content after processing: " + httpResponse.getBody().asString());
			} catch (NumberFormatException e) {
				Log.error("Number format exception thrown when processing asynchronously:", e);

			} catch (InterruptedException e) {

				e.printStackTrace();
				Log.error("Asynchronous execution interrupted:", e);
			} catch (Exception e) {
				e.printStackTrace();
				Log.error("Asynchronous execution raised exception:", e);
			}

			break;

		}
	}

	// custom active polling to an API to support Asynchronous behavior
	public Response waitForApiResponse(String searchXpath, String expectedValue, int timeout)
			throws InterruptedException {
		System.out.println("Entered method: waitForApiResponse()");
		Response result = null;
		long maxTimeOut = timeout * 1000;
		long lStartTime = System.currentTimeMillis();
		Log.info("Expected value to poll: " + expectedValue);
		while (System.currentTimeMillis() - lStartTime < maxTimeOut) {
			result = httpRequest.when().get(getURL);
			System.out.println(result.getBody().asString());
			System.out.println("Expected value to poll: " + expectedValue);
			Log.info("Processing: " + result.getBody().asString());
			if (result.jsonPath().getString(searchXpath).contains(expectedValue)) {
				return result;
			} else {
				TimeUnit.SECONDS.sleep(RestConstants.ASYNCPOLLINGINTERVAL);
			}
		}
		fail("Timed out after waiting for " + timeout + " seconds" + " Endpoint: " + getURL);
		Log.fatal("Timed out after waiting for " + timeout + " seconds" + " Endpoint: " + getURL);
		return result;
	}

	// set global variables for the test scenarios
	public void setGlobalVariablesfromResponse(String inputData) {

		System.out.println("Entered method: setGlobalVariablesfromResponse()");
		String variableValue = null;
		if (httpResponse != null) {

		} else {
			createResponseObject();

		}

		// get username value from response object to store in memory
		// String variableValue =
		// JsonPath.from(httpResponse.getBody().asString()).getString(inputData);
		try {
			variableValue = JsonPath.from(httpResponse.getBody().asString()).getString(inputData);
		} catch (Exception e) {
			Log.error("Message not found in response: " + inputData, e);
			fail("Message not found in response: " + inputData + " Http Response:" + httpResponse.getBody().asString());
		}
		System.out.print("Set global key: " + inputData);
		System.out.print("Set global value: " + variableValue);
		globalDataDictionary.put(inputData.toUpperCase(), variableValue);

	}

	// resolve global variable value
	public String resolveGlobalVariable(String inputVariable) {

		System.out.println("Entered method: resolveGlobalVariable()");
		System.out.println("Global variable: " + inputVariable + "Resolved: "
				+ (String) globalDataDictionary.get(inputVariable.toUpperCase()));
		return (String) globalDataDictionary.get(inputVariable.toUpperCase());
	}

	// set up asynchronous request type
	public void getAsynchronousRequest(String url) {

		System.out.println("Entered method: getAsynchronousRequest()");
		Log.info("Endpoint Request url : " + url);
		getURL = url;
		// make a get request
		requestType = "ASYNCHRONOUSGET";
		httpRequest = null;
		httpResponse = null;
		httpRequest = RestAssured.given().config(getSSLCert());

	}

	// set asynchronous request parameters
	public void setAsynchronousRequestParameters(String waitParameterName, String waitParameterDesiredValue) {
		System.out.println("Entered method: setAsynchronousRequestParameters()");
		globalDataDictionary.put("responseWaitParameterName", waitParameterName);
		globalDataDictionary.put("responseWaitParameterValue", waitParameterDesiredValue);

	}

	public void setAsynchronousRequestTimeOutParameters(int timeout) {
		System.out.println("Entered method: setAsynchronousRequestTimeOutParameters()");
		globalDataDictionary.put("responseWaitTime", timeout);

	}

	public RestAssuredConfig getSSLCert() {
		System.out.println("Entered method: getSSLCert()");
		KeyStore keyStore = null;
		KeyStore truststore = null;
		SSLConfig config = null;
		RestAssuredConfig restConfig = null;
		SSLContext context = null;

		SSLSocketFactory clientAuthFactory = null;

		try {

			keyStore = KeyStore.getInstance("jks");
			keyStore.load(new FileInputStream("all/jks/keystore.jks"), "ZjA0YTU1MT".toCharArray());
			truststore = KeyStore.getInstance("jks");
			truststore.load(new FileInputStream(new File("all/jks/truststore.jks")), "ZjA0YTU1MT".toCharArray());
			KeyStore keystore = KeyStore.getInstance("jks");
			keystore.load(new FileInputStream(new File("all/jks/keystore.jks")), "ZjA0YTU1MT".toCharArray());
			SSLContextBuilder builder = new SSLContextBuilder();
			builder.loadKeyMaterial(keystore, "ZjA0YTU1MT".toCharArray());
			builder.loadTrustMaterial(truststore, new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			});
			context = builder.build();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// set the config in rest assured
		clientAuthFactory = new SSLSocketFactory(context, new String[] { "TLSv1", "TLSv1.1", "TLSv1.2" }, null, null);
		config = new SSLConfig().with().sslSocketFactory(clientAuthFactory).and().allowAllHostnames();

		System.out.println("SSL config  set>>>>>>>>>");
		restConfig = RestAssured.config().sslConfig(config);
		System.out.println("SSL config returned to caller>>>>>>>>>");
		// }
		return restConfig;
	}
}

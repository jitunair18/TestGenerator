package com.ibmwatsonhealth.devopsservices.swaggertestasset.swaggerParserUtility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import io.swagger.inflector.examples.ExampleBuilder;
import io.swagger.inflector.examples.XmlExampleSerializer;
import io.swagger.inflector.examples.models.Example;
import io.swagger.inflector.processors.JsonNodeExampleSerializer;
import io.swagger.models.ArrayModel;
import io.swagger.models.Model;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.CookieParameter;
import io.swagger.models.parameters.HeaderParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.PathParameter;
import io.swagger.models.parameters.QueryParameter;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.RefModel;
import io.swagger.parser.SwaggerParser;
import io.swagger.util.Json;
import io.swagger.util.Yaml;

public class SwaggerUtility {

	// global variable section

	public final String sampleJsonString = "{\"name\":\"Sample json string\"}";
	public final String sampleTextString = "hello world";
	public final String sampleXmlString = "<?xml version='1.1' encoding='UTF-8'?><dummy><id>0</id><user>string</user><child><childNames>string</childNames></child></dummy>";
	public List<DataLoadOperationType> postOperationList = new ArrayList<DataLoadOperationType>();
	public List<DataLoadOperationType> putOperationList = new ArrayList<DataLoadOperationType>();
	public List<DataLoadOperationType> getOperationList = new ArrayList<DataLoadOperationType>();
	public List<DataLoadOperationType> deleteOperationList = new ArrayList<DataLoadOperationType>();
	public List<DataLoadOperationType> globalOperationList = new ArrayList<DataLoadOperationType>();
	public List<String> externalDataList = new ArrayList<String>();
	public String BASE_URI = null;
	public String SWAGGER_URL = null;

	public SwaggerInformation getSwaggerData(String swaggerString) {

		// Variable declaration section
		SwaggerInformation instance = new SwaggerInformation();
		Map<Object, Object> swaggerMap = new LinkedHashMap<Object, Object>();
		Map<Object, Object> dataMap = null;
		Map<String, Model> definitionMap;

		EndpointOperationType endpointoperationInstance;
		Map<String, Response> responseMap = null;
		Map<String, Object> queryParameterMap = null;
		Map<String, Object> pathParameterMap = null;
		List<Parameter> parameterList = null;
		String baseURI = null;
		String basePath;
		String xmlString = null;
		String jsonString = null;
		String textString = null;
		Map<String, Object> feedData = null;

		try {
			com.fasterxml.jackson.databind.module.SimpleModule simpleModule = new com.fasterxml.jackson.databind.module.SimpleModule();
			simpleModule.addSerializer(new JsonNodeExampleSerializer());
			Json.mapper().registerModule(simpleModule);
			Yaml.mapper().registerModule(simpleModule);

			// Parse input json string and prepare required object model
			/*
			 * System.out.println(swaggerString.toString()); JSONObject jsonObject = new
			 * JSONObject(swaggerString);
			 */

			String url = swaggerString;
			SWAGGER_URL = url;
			swaggerMap.put("url", SWAGGER_URL);

			// Extract path of swagger specification
			Swagger swagger = new SwaggerParser().read(url);
			

			// Extract basepath url for the endpoint

			// file1.getPath();
			// baseURI = file1.getParent();
			File file2 = new File(swagger.getBasePath());
			basePath = file2.getPath();
			// System.out.println(file2.getPath());

			URL urlobject = new URL(url);

			// System.out.println(parentPath);
			URL parentUrl = new URL(urlobject.getProtocol(), urlobject.getHost(), urlobject.getPort(), basePath);
			baseURI = parentUrl.toString();
			BASE_URI = baseURI;
			// System.out.println("Parent: " + baseURI);

			/*
			 * 
			 * JSONArray testDataArray; try { testDataArray =
			 * jsonObject.getJSONArray("testdata"); } catch (JSONException e) {
			 * testDataArray = null; } // Check if external data is provided to JSON if
			 * (testDataArray != null) { generateExternalDataModel(testDataArray); }
			 * 
			 */

			// get all definitions from the Swagger specification
			definitionMap = new HashMap<String, Model>();
			definitionMap = swagger.getDefinitions();
			// Path map of all end points and all operations
			Map<String, Path> pathMap = new HashMap<String, Path>();
			pathMap = swagger.getPaths();
			for (Map.Entry<String, Path> entry : pathMap.entrySet()) {

				String someString = entry.getKey();
				System.out.println(someString);
				Path checkPath = entry.getValue();
				// List all operations associated with the path
				Operation operationValidator = new Operation();
				operationValidator = checkPath.getGet();

				if (!externalDataList.contains(entry.getKey().trim().toLowerCase() + "," + "get")) {

					if (operationValidator == null) {
						// System.out.println("get was null");
					} else {

						// Get all responses associated with the operation
						responseMap = new HashMap<String, Response>();
						responseMap = operationValidator.getResponses();

						// Get consumption list for Operation
						List<String> consumesList = operationValidator.getConsumes();
						dataMap = new HashMap<Object, Object>();
						// Initialize map to populate data
						feedData = new HashMap<String, Object>();

						// Get all Parameters associated with the operation
						parameterList = new ArrayList<Parameter>();
						parameterList = operationValidator.getParameters();

						dataMap.put("hasMandatoryParameterResolved", "true");

						queryParameterMap = new HashMap<String, Object>();
						pathParameterMap = new HashMap<String, Object>();
						for (Parameter param : parameterList) {
							if (param instanceof PathParameter) {
								if (((PathParameter) param).getDefaultValue() != null) {
									pathParameterMap.put(param.getName(), ((PathParameter) param).getDefaultValue());
								}
							}
							if (param instanceof QueryParameter) {
								if (((QueryParameter) param).getDefaultValue() != null) {
									queryParameterMap.put(param.getName(), ((QueryParameter) param).getDefaultValue());
								}
							}
							if (param instanceof PathParameter || param instanceof QueryParameter
									|| param instanceof HeaderParameter) {
								if (param.getRequired() && dataMap.get("hasMandatoryParameterResolved") == "true") {
									dataMap.put("hasMandatoryParameterResolved",
											checkInternalSwaggerParametersResolved(param));
								}
							}

							if (param instanceof BodyParameter) {
								BodyParameter bp = (BodyParameter) param;
								Model model = bp.getSchema();
								Object o = model.getExample();
								if (o == null) {

								} else {

									System.out.println("Object was not null someting to work with");
								}
								System.out.println("Model is of type" + model.getClass());

								if (model instanceof RefModel) {
									System.out.println("Model was found of type RefModel");
									RefModel ref = (RefModel) model;
									String simpleRef = ref.getSimpleRef();
									System.out.println(simpleRef);
									Object test = ExampleBuilder.fromProperty(
											new io.swagger.models.properties.RefProperty(simpleRef), definitionMap);

									if (test != null && consumesList != null) {
										if (consumesList.contains("application/json")) {
											jsonString = Json.pretty(test);
											System.out.println(jsonString);
											feedData.put("data", jsonString);
											feedData.put("schema", "application/json");
											dataMap.put("validdata", feedData);

											// Setup for invalid data
											feedData = new HashMap<String, Object>();
											feedData.put("data", sampleTextString);
											feedData.put("schema", "application/json");
											dataMap.put("invaliddata", feedData);

										} else if (consumesList.contains("application/xml")) {
											xmlString = new XmlExampleSerializer().serialize((Example) test);
											System.out.println(xmlString);
											feedData.put("data", xmlString);
											feedData.put("schema", "application/xml");
											dataMap.put("validdata", feedData);

											// Setup for invalid data
											feedData = new HashMap<String, Object>();
											feedData.put("data", sampleJsonString);
											feedData.put("schema", "application/xml");
											dataMap.put("invaliddata", feedData);

										} else if (consumesList.contains("text/plain")) {
											textString = ((Example) test).asString();
											System.out.println(textString);
											feedData.put("data", textString);
											feedData.put("schema", "text/plain");
											dataMap.put("validdata", feedData);

											// Setup for invalid data
											feedData = new HashMap<String, Object>();
											feedData.put("data", sampleJsonString);
											feedData.put("schema", "application/json");
											dataMap.put("invaliddata", feedData);

										}
									} else {
										dataMap.put("validdata", null);
									}

								} else if (model instanceof ArrayModel) {
									ArrayModel arrayModel = (ArrayModel) model;
									Property prop = arrayModel.getItems();
									if (prop instanceof RefProperty) {
										Object test = ExampleBuilder.fromProperty(new ArrayProperty(prop),
												definitionMap);

										if (test != null && consumesList != null) {

											if (consumesList.contains("application/json")) {
												jsonString = Json.pretty(test);
												System.out.println(jsonString);
												feedData.put("data", jsonString);
												feedData.put("schema", "application/json");
												dataMap.put("validdata", feedData);

												// Setup for invalid data
												feedData = new HashMap<String, Object>();
												feedData.put("data", sampleTextString);
												feedData.put("schema", "application/json");
												dataMap.put("invaliddata", feedData);

											} else if (consumesList.contains("application/xml")) {

												xmlString = new XmlExampleSerializer().serialize((Example) test);
												System.out.println(xmlString);
												feedData.put("data", xmlString);
												feedData.put("schema", "application/xml");
												dataMap.put("validdata", feedData);

												// Setup for invalid data
												feedData = new HashMap<String, Object>();
												feedData.put("data", sampleJsonString);
												feedData.put("schema", "application/xml");
												dataMap.put("invaliddata", feedData);

											} else if (consumesList.contains("text/plain")) {
												textString = ((Example) test).asString();
												System.out.println(textString);
												feedData.put("data", textString);
												feedData.put("schema", "text/plain");
												dataMap.put("validdata", feedData);

												// Setup for invalid data
												feedData = new HashMap<String, Object>();
												feedData.put("data", sampleJsonString);
												feedData.put("schema", "application/json");
												dataMap.put("invaliddata", feedData);
											}

										} else {
											dataMap.put("validdata", null);
										}

									}
								}

							}
						}

						// Feed values into swagger map
						endpointoperationInstance = new EndpointOperationType(entry.getKey(), "get");

						dataMap.put("definition", definitionMap);
						dataMap.put("response", responseMap);
						dataMap.put("baseURI", baseURI);
						dataMap.put("queryParameters", queryParameterMap);
						dataMap.put("pathParameters", pathParameterMap);
						dataMap.put("external", false);
						dataMap.put("skip", "false");

						DataLoadOperationType dataLoader = new DataLoadOperationType(endpointoperationInstance,
								dataMap);
						getOperationList.add(dataLoader);

					}
				}

				operationValidator = checkPath.getDelete();

				if (!externalDataList.contains(entry.getKey().trim().toLowerCase() + "," + "delete")) {
					if (operationValidator == null) {
						// System.out.println("delete was null");
					} else {

						// Get all responses associated with the operation
						responseMap = new HashMap<String, Response>();
						responseMap = operationValidator.getResponses();

						// Get consumption list for Operation
						List<String> consumesList = operationValidator.getConsumes();
						dataMap = new HashMap<Object, Object>();
						// Initialize map to populate data
						feedData = new HashMap<String, Object>();

						// Get all Parameters associated with the operation
						parameterList = new ArrayList<Parameter>();
						parameterList = operationValidator.getParameters();

						dataMap.put("hasMandatoryParameterResolved", "true");

						queryParameterMap = new HashMap<String, Object>();
						pathParameterMap = new HashMap<String, Object>();
						for (Parameter param : parameterList) {
							if (param instanceof PathParameter) {
								if (((PathParameter) param).getDefaultValue() != null) {
									pathParameterMap.put(param.getName(), ((PathParameter) param).getDefaultValue());
								}
							}
							if (param instanceof QueryParameter) {
								if (((QueryParameter) param).getDefaultValue() != null) {
									queryParameterMap.put(param.getName(), ((QueryParameter) param).getDefaultValue());
								}
							}

							if (param instanceof PathParameter || param instanceof QueryParameter
									|| param instanceof HeaderParameter) {
								if (param.getRequired() && dataMap.get("hasMandatoryParameterResolved") == "true") {
									dataMap.put("hasMandatoryParameterResolved",
											checkInternalSwaggerParametersResolved(param));
								}
							}

							if (param instanceof BodyParameter) {
								BodyParameter bp = (BodyParameter) param;
								Model model = bp.getSchema();
								Object o = model.getExample();
								if (o == null) {

								} else {

									System.out.println("Object was not null someting to work with");
								}
								System.out.println("Model is of type" + model.getClass());

								if (model instanceof RefModel) {
									System.out.println("Model was found of type RefModel");
									RefModel ref = (RefModel) model;
									String simpleRef = ref.getSimpleRef();
									System.out.println(simpleRef);

									Object test = ExampleBuilder.fromProperty(
											new io.swagger.models.properties.RefProperty(simpleRef), definitionMap);

									if (test != null && consumesList != null) {
										if (consumesList.contains("application/json")) {
											jsonString = Json.pretty(test);
											System.out.println(jsonString);
											feedData.put("data", jsonString);
											feedData.put("schema", "application/json");
											dataMap.put("validdata", feedData);

											// Setup for invalid data
											feedData = new HashMap<String, Object>();
											feedData.put("data", sampleTextString);
											feedData.put("schema", "application/json");
											dataMap.put("invaliddata", feedData);

										} else if (consumesList.contains("application/xml")) {

											xmlString = new XmlExampleSerializer().serialize((Example) test);
											System.out.println(xmlString);
											feedData.put("data", xmlString);
											feedData.put("schema", "application/xml");
											dataMap.put("validdata", feedData);

											// Setup for invalid data
											feedData = new HashMap<String, Object>();
											feedData.put("data", sampleJsonString);
											feedData.put("schema", "application/xml");
											dataMap.put("invaliddata", feedData);

										} else if (consumesList.contains("text/plain")) {
											textString = ((Example) test).asString();
											System.out.println(textString);
											feedData.put("data", textString);
											feedData.put("schema", "text/plain");
											dataMap.put("validdata", feedData);

											// Setup for invalid data
											feedData = new HashMap<String, Object>();
											feedData.put("data", sampleJsonString);
											feedData.put("schema", "application/json");
											dataMap.put("invaliddata", feedData);

										}
									} else {
										dataMap.put("validdata", null);
									}

								} else if (model instanceof ArrayModel) {
									ArrayModel arrayModel = (ArrayModel) model;
									Property prop = arrayModel.getItems();
									if (prop instanceof RefProperty) {
										Object test = ExampleBuilder.fromProperty(new ArrayProperty(prop),
												definitionMap);

										if (test != null && consumesList != null) {
											if (consumesList.contains("application/json")) {
												jsonString = Json.pretty(test);
												System.out.println(jsonString);
												feedData.put("data", jsonString);
												feedData.put("schema", "application/json");
												dataMap.put("validdata", feedData);

												// Setup for invalid data
												feedData = new HashMap<String, Object>();
												feedData.put("data", sampleTextString);
												feedData.put("schema", "application/json");
												dataMap.put("invaliddata", feedData);

											} else if (consumesList.contains("application/xml")) {

												xmlString = new XmlExampleSerializer().serialize((Example) test);
												System.out.println(xmlString);
												feedData.put("data", xmlString);
												feedData.put("schema", "application/xml");
												dataMap.put("validdata", feedData);

												// Setup for invalid data
												feedData = new HashMap<String, Object>();
												feedData.put("data", sampleJsonString);
												feedData.put("schema", "application/xml");
												dataMap.put("invaliddata", feedData);

											} else if (consumesList.contains("text/plain")) {
												textString = ((Example) test).asString();
												System.out.println(textString);
												feedData.put("data", textString);
												feedData.put("schema", "text/plain");
												dataMap.put("validdata", feedData);

												// Setup for invalid data
												feedData = new HashMap<String, Object>();
												feedData.put("data", sampleJsonString);
												feedData.put("schema", "application/json");
												dataMap.put("invaliddata", feedData);

											}
										} else {
											dataMap.put("validdata", null);
										}

									}
								}

							}
						}

						// Feed values into swagger map
						endpointoperationInstance = new EndpointOperationType(entry.getKey(), "delete");
						// dataMap = new HashMap<Object, Object>();
						dataMap.put("definition", definitionMap);
						dataMap.put("response", responseMap);
						dataMap.put("baseURI", baseURI);
						dataMap.put("queryParameters", queryParameterMap);
						dataMap.put("pathParameters", pathParameterMap);
						dataMap.put("external", false);
						dataMap.put("skip", "false");

						DataLoadOperationType dataLoader = new DataLoadOperationType(endpointoperationInstance,
								dataMap);
						deleteOperationList.add(dataLoader);
					}
				}

				operationValidator = checkPath.getPost();

				if (!externalDataList.contains(entry.getKey().trim().toLowerCase() + "," + "post")) {
					if (operationValidator == null) {
						// System.out.println("post was null");
					} else {
						// Get all responses associated with the operation
						responseMap = new HashMap<String, Response>();
						responseMap = operationValidator.getResponses();

						// Get consumption list for Operation
						List<String> consumesList = operationValidator.getConsumes();
						dataMap = new HashMap<Object, Object>();
						// Initialize map to populate data
						feedData = new HashMap<String, Object>();

						// Get all Parameters associated with the operation
						parameterList = new ArrayList<Parameter>();
						parameterList = operationValidator.getParameters();

						dataMap.put("hasMandatoryParameterResolved", "true");

						queryParameterMap = new HashMap<String, Object>();
						pathParameterMap = new HashMap<String, Object>();
						for (Parameter param : parameterList) {
							if (param instanceof PathParameter) {
								if (((PathParameter) param).getDefaultValue() != null) {
									pathParameterMap.put(param.getName(), ((PathParameter) param).getDefaultValue());
								}
							}
							if (param instanceof QueryParameter) {
								if (((QueryParameter) param).getDefaultValue() != null) {
									queryParameterMap.put(param.getName(), ((QueryParameter) param).getDefaultValue());
								}
							}

							if (param instanceof PathParameter || param instanceof QueryParameter
									|| param instanceof HeaderParameter) {
								if (param.getRequired() && dataMap.get("hasMandatoryParameterResolved") == "true") {
									dataMap.put("hasMandatoryParameterResolved",
											checkInternalSwaggerParametersResolved(param));
								}
							}

							if (param instanceof BodyParameter) {
								BodyParameter bp = (BodyParameter) param;
								Model model = bp.getSchema();
								System.out.println("Model is of type" + model.getClass());

								if (model instanceof RefModel) {
									System.out.println("Model was found of type RefModel");
									RefModel ref = (RefModel) model;
									String simpleRef = ref.getSimpleRef();
									System.out.println(simpleRef);

									Object test = ExampleBuilder.fromProperty(
											new io.swagger.models.properties.RefProperty(simpleRef), definitionMap);

									if (test != null && consumesList != null) {
										if (consumesList.contains("application/json")) {
											jsonString = Json.pretty(test);
											System.out.println(jsonString);
											feedData.put("data", jsonString);
											feedData.put("schema", "application/json");
											dataMap.put("validdata", feedData);

											// Setup for invalid data
											feedData = new HashMap<String, Object>();
											feedData.put("data", sampleTextString);
											feedData.put("schema", "application/json");
											dataMap.put("invaliddata", feedData);

										} else if (consumesList.contains("application/xml")) {

											xmlString = new XmlExampleSerializer().serialize((Example) test);
											System.out.println(xmlString);
											feedData.put("data", xmlString);
											feedData.put("schema", "application/xml");
											dataMap.put("validdata", feedData);

											// Setup for invalid data
											feedData = new HashMap<String, Object>();
											feedData.put("data", sampleJsonString);
											feedData.put("schema", "application/xml");
											dataMap.put("invaliddata", feedData);

										} else if (consumesList.contains("text/plain")) {
											textString = ((Example) test).asString();
											System.out.println(textString);
											feedData.put("data", textString);
											feedData.put("schema", "text/plain");
											dataMap.put("validdata", feedData);

											// Setup for invalid data
											feedData = new HashMap<String, Object>();
											feedData.put("data", sampleJsonString);
											feedData.put("schema", "application/json");
											dataMap.put("invaliddata", feedData);

										}
									} else {
										dataMap.put("validdata", null);
									}

								} else if (model instanceof ArrayModel) {
									ArrayModel arrayModel = (ArrayModel) model;
									Property prop = arrayModel.getItems();
									if (prop instanceof RefProperty) {
										Object test = ExampleBuilder.fromProperty(new ArrayProperty(prop),
												definitionMap);

										if (test != null && consumesList != null) {
											if (consumesList.contains("application/json")) {
												jsonString = Json.pretty(test);
												System.out.println(jsonString);
												feedData.put("data", jsonString);
												feedData.put("schema", "application/json");
												dataMap.put("validdata", feedData);

												// Setup for invalid data
												feedData = new HashMap<String, Object>();
												feedData.put("data", sampleTextString);
												feedData.put("schema", "application/json");
												dataMap.put("invaliddata", feedData);

											} else if (consumesList.contains("application/xml")) {

												xmlString = new XmlExampleSerializer().serialize((Example) test);
												System.out.println(xmlString);
												feedData.put("data", xmlString);
												feedData.put("schema", "application/xml");
												dataMap.put("validdata", feedData);

												// Setup for invalid data
												feedData = new HashMap<String, Object>();
												feedData.put("data", sampleJsonString);
												feedData.put("schema", "application/xml");
												dataMap.put("invaliddata", feedData);

											} else if (consumesList.contains("text/plain")) {
												textString = ((Example) test).asString();
												System.out.println(textString);
												feedData.put("data", textString);
												feedData.put("schema", "text/plain");
												dataMap.put("validdata", feedData);

												// Setup for invalid data
												feedData = new HashMap<String, Object>();
												feedData.put("data", sampleJsonString);
												feedData.put("schema", "application/json");
												dataMap.put("invaliddata", feedData);

											}
										} else {
											dataMap.put("validdata", null);
										}

									}
								}

							}
						}
						// Feed values into swagger map
						endpointoperationInstance = new EndpointOperationType(entry.getKey(), "post");
						// dataMap = new HashMap<Object, Object>();
						dataMap.put("definition", definitionMap);
						dataMap.put("response", responseMap);
						dataMap.put("baseURI", baseURI);
						dataMap.put("queryParameters", queryParameterMap);
						dataMap.put("pathParameters", pathParameterMap);
						dataMap.put("external", false);
						dataMap.put("skip", "false");

						DataLoadOperationType dataLoader = new DataLoadOperationType(endpointoperationInstance,
								dataMap);
						postOperationList.add(dataLoader);
					}
				}
				operationValidator = checkPath.getPut();

				if (!externalDataList.contains(entry.getKey().trim().toLowerCase() + "," + "put")) {
					if (operationValidator == null) {
						// System.out.println("put was null");
					} else {
						// Get all responses associated with the operation
						responseMap = new HashMap<String, Response>();
						responseMap = operationValidator.getResponses();

						// Get consumption list for Operation
						List<String> consumesList = operationValidator.getConsumes();
						dataMap = new HashMap<Object, Object>();
						// Initialize map to populate data
						feedData = new HashMap<String, Object>();

						// Get all Parameters associated with the operation
						parameterList = new ArrayList<Parameter>();
						parameterList = operationValidator.getParameters();
						System.out.println("THIS IS A PUT OPERATION");

						dataMap.put("hasMandatoryParameterResolved", "true");

						queryParameterMap = new HashMap<String, Object>();
						pathParameterMap = new HashMap<String, Object>();
						for (Parameter param : parameterList) {
							if (param instanceof PathParameter) {
								if (((PathParameter) param).getDefaultValue() != null) {
									pathParameterMap.put(param.getName(), ((PathParameter) param).getDefaultValue());
								}
							}
							if (param instanceof QueryParameter) {
								if (((QueryParameter) param).getDefaultValue() != null) {
									queryParameterMap.put(param.getName(), ((QueryParameter) param).getDefaultValue());
								}
							}

							if (param instanceof PathParameter || param instanceof QueryParameter
									|| param instanceof HeaderParameter) {
								if (param.getRequired() && dataMap.get("hasMandatoryParameterResolved") == "true") {
									dataMap.put("hasMandatoryParameterResolved",
											checkInternalSwaggerParametersResolved(param));
								}
							}
							if (param instanceof BodyParameter) {
								// System.out.println("BODY PARAMETER WAS FOUND
								// for
								// a
								// put operation");
								BodyParameter bp = (BodyParameter) param;
								Model model = bp.getSchema();
								Object o = model.getExample();
								if (o == null) {

								} else {

									System.out.println("Object was not null someting to work with");
								}
								System.out.println("Model is of type" + model.getClass());
								if (model instanceof RefModel) {
									System.out.println("Model was found of type RefModel");
									RefModel ref = (RefModel) model;
									String simpleRef = ref.getSimpleRef();
									System.out.println(simpleRef);

									Object test = ExampleBuilder.fromProperty(
											new io.swagger.models.properties.RefProperty(simpleRef), definitionMap);

									if (test != null && consumesList != null) {
										if (consumesList.contains("application/json")) {
											jsonString = Json.pretty(test);
											System.out.println(jsonString);
											feedData.put("data", jsonString);
											feedData.put("schema", "application/json");
											dataMap.put("validdata", feedData);

											// Setup for invalid data
											feedData = new HashMap<String, Object>();
											feedData.put("data", sampleTextString);
											feedData.put("schema", "application/json");
											dataMap.put("invaliddata", feedData);

										} else if (consumesList.contains("application/xml")) {

											xmlString = new XmlExampleSerializer().serialize((Example) test);
											System.out.println(xmlString);
											feedData.put("data", xmlString);
											feedData.put("schema", "application/xml");
											dataMap.put("validdata", feedData);

											// Setup for invalid data
											feedData = new HashMap<String, Object>();
											feedData.put("data", sampleJsonString);
											feedData.put("schema", "application/xml");
											dataMap.put("invaliddata", feedData);

										} else if (consumesList.contains("text/plain")) {
											textString = ((Example) test).asString();
											System.out.println(textString);
											feedData.put("data", textString);
											feedData.put("schema", "text/plain");
											dataMap.put("validdata", feedData);

											// Setup for invalid data
											feedData = new HashMap<String, Object>();
											feedData.put("data", sampleJsonString);
											feedData.put("schema", "application/json");
											dataMap.put("invaliddata", feedData);

										}
									} else {
										dataMap.put("validdata", null);
									}

								} else if (model instanceof ArrayModel) {
									ArrayModel arrayModel = (ArrayModel) model;
									Property prop = arrayModel.getItems();
									if (prop instanceof RefProperty) {
										Object test = ExampleBuilder.fromProperty(new ArrayProperty(prop),
												definitionMap);

										if (test != null && consumesList != null) {
											if (consumesList.contains("application/json")) {
												jsonString = Json.pretty(test);
												System.out.println(jsonString);
												feedData.put("data", jsonString);
												feedData.put("schema", "application/json");
												dataMap.put("validdata", feedData);

												// Setup for invalid data
												feedData = new HashMap<String, Object>();
												feedData.put("data", sampleTextString);
												feedData.put("schema", "application/json");
												dataMap.put("invaliddata", feedData);

											} else if (consumesList.contains("application/xml")) {

												xmlString = new XmlExampleSerializer().serialize((Example) test);
												System.out.println(xmlString);
												feedData.put("data", xmlString);
												feedData.put("schema", "application/xml");
												dataMap.put("validdata", feedData);

												// Setup for invalid data
												feedData = new HashMap<String, Object>();
												feedData.put("data", sampleJsonString);
												feedData.put("schema", "application/xml");
												dataMap.put("invaliddata", feedData);

											} else if (consumesList.contains("text/plain")) {
												textString = ((Example) test).asString();
												System.out.println(textString);
												feedData.put("data", textString);
												feedData.put("schema", "text/plain");
												dataMap.put("validdata", feedData);

												// Setup for invalid data
												feedData = new HashMap<String, Object>();
												feedData.put("data", sampleJsonString);
												feedData.put("schema", "application/json");
												dataMap.put("invaliddata", feedData);

											}
										} else {
											dataMap.put("validdata", null);
										}

									}
								}

							}
						}

						// Feed values into swagger map
						endpointoperationInstance = new EndpointOperationType(entry.getKey(), "put");
						// dataMap = new HashMap<Object, Object>();
						dataMap.put("definition", definitionMap);
						dataMap.put("response", responseMap);
						dataMap.put("baseURI", baseURI);
						dataMap.put("queryParameters", queryParameterMap);
						dataMap.put("pathParameters", pathParameterMap);
						dataMap.put("external", false);
						dataMap.put("skip", "false");

						DataLoadOperationType dataLoader = new DataLoadOperationType(endpointoperationInstance,
								dataMap);
						putOperationList.add(dataLoader);

					}
				}

			}
			// Merge all operation lists into a common global operation list
			// after
			// sequencing the operations
			// Generate the swaggerMap from this global operation List
			globalOperationList.addAll(postOperationList);
			globalOperationList.addAll(putOperationList);
			globalOperationList.addAll(getOperationList);
			globalOperationList.addAll(deleteOperationList);

			// Iterate through globalOperationList sorted and generate final
			// swagger
			// Map
			for (DataLoadOperationType dataLoader : globalOperationList) {
				swaggerMap.put(dataLoader.getEndpointOperationType(), dataLoader.getDataMap());
			}
		} catch (Exception e) {
			e.printStackTrace();
			swaggerMap = null;
		} finally {
			globalOperationList = null;
			externalDataList = null;
			postOperationList = null;
			getOperationList = null;
			deleteOperationList = null;
			putOperationList = null;
			
		}
		instance.swaggerInformation = swaggerMap;
		return instance;

	}

	public Map<Object, Object> getSwaggerInfo(String swaggerString) {

		// Variable declaration section
		Map<Object, Object> swaggerMap = new LinkedHashMap<Object, Object>();
		Map<Object, Object> dataMap = null;
		Map<String, Model> definitionMap;

		EndpointOperationType endpointoperationInstance;
		Map<String, Response> responseMap = null;
		Map<String, Object> queryParameterMap = null;
		Map<String, Object> pathParameterMap = null;
		List<Parameter> parameterList = null;
		String baseURI = null;
		String basePath;
		String xmlString = null;
		String jsonString = null;
		String textString = null;
		Map<String, Object> feedData = null;

		try {
			com.fasterxml.jackson.databind.module.SimpleModule simpleModule = new com.fasterxml.jackson.databind.module.SimpleModule();
			simpleModule.addSerializer(new JsonNodeExampleSerializer());
			Json.mapper().registerModule(simpleModule);
			Yaml.mapper().registerModule(simpleModule);

			// Parse input json string and prepare required object model
			/*
			 * System.out.println(swaggerString.toString()); JSONObject jsonObject = new
			 * JSONObject(swaggerString);
			 */

			String url = swaggerString;
			SWAGGER_URL = url;

			// Extract path of swagger specification
			Swagger swagger = new SwaggerParser().read(url);

			// Extract basepath url for the endpoint

			// file1.getPath();
			// baseURI = file1.getParent();
			File file2 = new File(swagger.getBasePath());
			basePath = file2.getPath();
			// System.out.println(file2.getPath());

			URL urlobject = new URL(url);

			// System.out.println(parentPath);
			URL parentUrl = new URL(urlobject.getProtocol(), urlobject.getHost(), urlobject.getPort(), basePath);
			baseURI = parentUrl.toString();
			BASE_URI = baseURI;
			// System.out.println("Parent: " + baseURI);

			/*
			 * 
			 * JSONArray testDataArray; try { testDataArray =
			 * jsonObject.getJSONArray("testdata"); } catch (JSONException e) {
			 * testDataArray = null; } // Check if external data is provided to JSON if
			 * (testDataArray != null) { generateExternalDataModel(testDataArray); }
			 * 
			 */

			// get all definitions from the Swagger specification
			definitionMap = new HashMap<String, Model>();
			definitionMap = swagger.getDefinitions();
			// Path map of all end points and all operations
			Map<String, Path> pathMap = new HashMap<String, Path>();
			pathMap = swagger.getPaths();
			System.out.println("PRINT INFO FOR SERVICE APIS");
			for (Map.Entry<String, Path> entry : pathMap.entrySet()) {

				String someString = entry.getKey();
				System.out.println("ENDPOINT: " + someString);
				Path checkPath = entry.getValue();
				// List all operations associated with the path
				Operation operationValidator = new Operation();
				operationValidator = checkPath.getGet();

				if (!externalDataList.contains(entry.getKey().trim().toLowerCase() + "," + "get")) {

					if (operationValidator == null) {
						// System.out.println("get was null");
					} else {
						System.out.println("Operation: get");

						// Get all responses associated with the operation
						responseMap = new HashMap<String, Response>();
						responseMap = operationValidator.getResponses();

						// Get consumption list for Operation
						List<String> consumesList = operationValidator.getConsumes();
						dataMap = new HashMap<Object, Object>();
						// Initialize map to populate data
						feedData = new HashMap<String, Object>();

						// Get all Parameters associated with the operation
						parameterList = new ArrayList<Parameter>();
						parameterList = operationValidator.getParameters();

						dataMap.put("hasMandatoryParameterResolved", "true");

						queryParameterMap = new HashMap<String, Object>();
						pathParameterMap = new HashMap<String, Object>();
						for (Parameter param : parameterList) {
							if (param instanceof PathParameter) {
								if (((PathParameter) param).getDefaultValue() != null) {
									pathParameterMap.put(param.getName(), ((PathParameter) param).getDefaultValue());
								}
							}
							if (param instanceof QueryParameter) {
								if (((QueryParameter) param).getDefaultValue() != null) {
									queryParameterMap.put(param.getName(), ((QueryParameter) param).getDefaultValue());
								}
							}
							if (param instanceof PathParameter || param instanceof QueryParameter
									|| param instanceof HeaderParameter) {
								if (param.getRequired() && dataMap.get("hasMandatoryParameterResolved") == "true") {
									dataMap.put("hasMandatoryParameterResolved",
											checkInternalSwaggerParametersResolved(param));
								}
							}

							if (param instanceof BodyParameter) {
								BodyParameter bp = (BodyParameter) param;
								Model model = bp.getSchema();
								Object o = model.getExample();
								if (o == null) {

								} else {

									System.out.println("Object was not null someting to work with");
								}
								// System.out.println("Model is of type" + model.getClass());

								if (model instanceof RefModel) {
									// System.out.println("Model was found of type RefModel");
									RefModel ref = (RefModel) model;
									String simpleRef = ref.getSimpleRef();
									// System.out.println(simpleRef);
									Object test = ExampleBuilder.fromProperty(
											new io.swagger.models.properties.RefProperty(simpleRef), definitionMap);

									if (test != null && consumesList != null) {
										if (consumesList.contains("application/json")) {
											jsonString = Json.pretty(test);
											// System.out.println(jsonString);
											feedData.put("data", jsonString);
											feedData.put("schema", "application/json");
											dataMap.put("validdata", feedData);

											// Setup for invalid data
											feedData = new HashMap<String, Object>();
											feedData.put("data", sampleTextString);
											feedData.put("schema", "application/json");
											dataMap.put("invaliddata", feedData);

										} else if (consumesList.contains("application/xml")) {
											xmlString = new XmlExampleSerializer().serialize((Example) test);
											// System.out.println(xmlString);
											feedData.put("data", xmlString);
											feedData.put("schema", "application/xml");
											dataMap.put("validdata", feedData);

											// Setup for invalid data
											feedData = new HashMap<String, Object>();
											feedData.put("data", sampleJsonString);
											feedData.put("schema", "application/xml");
											dataMap.put("invaliddata", feedData);

										} else if (consumesList.contains("text/plain")) {
											textString = ((Example) test).asString();
											// System.out.println(textString);
											feedData.put("data", textString);
											feedData.put("schema", "text/plain");
											dataMap.put("validdata", feedData);

											// Setup for invalid data
											feedData = new HashMap<String, Object>();
											feedData.put("data", sampleJsonString);
											feedData.put("schema", "application/json");
											dataMap.put("invaliddata", feedData);

										}
									} else {
										dataMap.put("validdata", null);
									}

								} else if (model instanceof ArrayModel) {
									ArrayModel arrayModel = (ArrayModel) model;
									Property prop = arrayModel.getItems();
									if (prop instanceof RefProperty) {
										Object test = ExampleBuilder.fromProperty(new ArrayProperty(prop),
												definitionMap);

										if (test != null && consumesList != null) {

											if (consumesList.contains("application/json")) {
												jsonString = Json.pretty(test);
												// System.out.println(jsonString);
												feedData.put("data", jsonString);
												feedData.put("schema", "application/json");
												dataMap.put("validdata", feedData);

												// Setup for invalid data
												feedData = new HashMap<String, Object>();
												feedData.put("data", sampleTextString);
												feedData.put("schema", "application/json");
												dataMap.put("invaliddata", feedData);

											} else if (consumesList.contains("application/xml")) {

												xmlString = new XmlExampleSerializer().serialize((Example) test);
												// System.out.println(xmlString);
												feedData.put("data", xmlString);
												feedData.put("schema", "application/xml");
												dataMap.put("validdata", feedData);

												// Setup for invalid data
												feedData = new HashMap<String, Object>();
												feedData.put("data", sampleJsonString);
												feedData.put("schema", "application/xml");
												dataMap.put("invaliddata", feedData);

											} else if (consumesList.contains("text/plain")) {
												textString = ((Example) test).asString();
												// System.out.println(textString);
												feedData.put("data", textString);
												feedData.put("schema", "text/plain");
												dataMap.put("validdata", feedData);

												// Setup for invalid data
												feedData = new HashMap<String, Object>();
												feedData.put("data", sampleJsonString);
												feedData.put("schema", "application/json");
												dataMap.put("invaliddata", feedData);
											}

										} else {
											dataMap.put("validdata", null);
										}

									}
								}

							}
						}

						// Feed values into swagger map
						endpointoperationInstance = new EndpointOperationType(entry.getKey(), "get");

						dataMap.put("definition", definitionMap);
						dataMap.put("response", responseMap);
						dataMap.put("baseURI", baseURI);
						dataMap.put("queryParameters", queryParameterMap);
						dataMap.put("pathParameters", pathParameterMap);
						dataMap.put("external", false);
						dataMap.put("skip", "false");

						DataLoadOperationType dataLoader = new DataLoadOperationType(endpointoperationInstance,
								dataMap);
						getOperationList.add(dataLoader);

					}
				}

				operationValidator = checkPath.getDelete();

				if (!externalDataList.contains(entry.getKey().trim().toLowerCase() + "," + "delete")) {
					if (operationValidator == null) {
						// System.out.println("delete was null");
					} else {

						// Get all responses associated with the operation
						System.out.println("Operation: delete");
						responseMap = new HashMap<String, Response>();
						responseMap = operationValidator.getResponses();

						// Get consumption list for Operation
						List<String> consumesList = operationValidator.getConsumes();
						dataMap = new HashMap<Object, Object>();
						// Initialize map to populate data
						feedData = new HashMap<String, Object>();

						// Get all Parameters associated with the operation
						parameterList = new ArrayList<Parameter>();
						parameterList = operationValidator.getParameters();

						dataMap.put("hasMandatoryParameterResolved", "true");

						queryParameterMap = new HashMap<String, Object>();
						pathParameterMap = new HashMap<String, Object>();
						for (Parameter param : parameterList) {
							if (param instanceof PathParameter) {
								if (((PathParameter) param).getDefaultValue() != null) {
									pathParameterMap.put(param.getName(), ((PathParameter) param).getDefaultValue());
								}
							}
							if (param instanceof QueryParameter) {
								if (((QueryParameter) param).getDefaultValue() != null) {
									queryParameterMap.put(param.getName(), ((QueryParameter) param).getDefaultValue());
								}
							}

							if (param instanceof PathParameter || param instanceof QueryParameter
									|| param instanceof HeaderParameter) {
								if (param.getRequired() && dataMap.get("hasMandatoryParameterResolved") == "true") {
									dataMap.put("hasMandatoryParameterResolved",
											checkInternalSwaggerParametersResolved(param));
								}
							}

							if (param instanceof BodyParameter) {
								BodyParameter bp = (BodyParameter) param;
								Model model = bp.getSchema();
								Object o = model.getExample();
								if (o == null) {

								} else {

									System.out.println("Object was not null someting to work with");
								}
								// System.out.println("Model is of type" + model.getClass());

								if (model instanceof RefModel) {
									// System.out.println("Model was found of type RefModel");
									RefModel ref = (RefModel) model;
									String simpleRef = ref.getSimpleRef();
									// System.out.println(simpleRef);

									Object test = ExampleBuilder.fromProperty(
											new io.swagger.models.properties.RefProperty(simpleRef), definitionMap);

									if (test != null && consumesList != null) {
										if (consumesList.contains("application/json")) {
											jsonString = Json.pretty(test);
											// System.out.println(jsonString);
											feedData.put("data", jsonString);
											feedData.put("schema", "application/json");
											dataMap.put("validdata", feedData);

											// Setup for invalid data
											feedData = new HashMap<String, Object>();
											feedData.put("data", sampleTextString);
											feedData.put("schema", "application/json");
											dataMap.put("invaliddata", feedData);

										} else if (consumesList.contains("application/xml")) {

											xmlString = new XmlExampleSerializer().serialize((Example) test);
											// System.out.println(xmlString);
											feedData.put("data", xmlString);
											feedData.put("schema", "application/xml");
											dataMap.put("validdata", feedData);

											// Setup for invalid data
											feedData = new HashMap<String, Object>();
											feedData.put("data", sampleJsonString);
											feedData.put("schema", "application/xml");
											dataMap.put("invaliddata", feedData);

										} else if (consumesList.contains("text/plain")) {
											textString = ((Example) test).asString();
											System.out.println(textString);
											feedData.put("data", textString);
											feedData.put("schema", "text/plain");
											dataMap.put("validdata", feedData);

											// Setup for invalid data
											feedData = new HashMap<String, Object>();
											feedData.put("data", sampleJsonString);
											feedData.put("schema", "application/json");
											dataMap.put("invaliddata", feedData);

										}
									} else {
										dataMap.put("validdata", null);
									}

								} else if (model instanceof ArrayModel) {
									ArrayModel arrayModel = (ArrayModel) model;
									Property prop = arrayModel.getItems();
									if (prop instanceof RefProperty) {
										Object test = ExampleBuilder.fromProperty(new ArrayProperty(prop),
												definitionMap);

										if (test != null && consumesList != null) {
											if (consumesList.contains("application/json")) {
												jsonString = Json.pretty(test);
												// System.out.println(jsonString);
												feedData.put("data", jsonString);
												feedData.put("schema", "application/json");
												dataMap.put("validdata", feedData);

												// Setup for invalid data
												feedData = new HashMap<String, Object>();
												feedData.put("data", sampleTextString);
												feedData.put("schema", "application/json");
												dataMap.put("invaliddata", feedData);

											} else if (consumesList.contains("application/xml")) {

												xmlString = new XmlExampleSerializer().serialize((Example) test);
												// System.out.println(xmlString);
												feedData.put("data", xmlString);
												feedData.put("schema", "application/xml");
												dataMap.put("validdata", feedData);

												// Setup for invalid data
												feedData = new HashMap<String, Object>();
												feedData.put("data", sampleJsonString);
												feedData.put("schema", "application/xml");
												dataMap.put("invaliddata", feedData);

											} else if (consumesList.contains("text/plain")) {
												textString = ((Example) test).asString();
												// System.out.println(textString);
												feedData.put("data", textString);
												feedData.put("schema", "text/plain");
												dataMap.put("validdata", feedData);

												// Setup for invalid data
												feedData = new HashMap<String, Object>();
												feedData.put("data", sampleJsonString);
												feedData.put("schema", "application/json");
												dataMap.put("invaliddata", feedData);

											}
										} else {
											dataMap.put("validdata", null);
										}

									}
								}

							}
						}

						// Feed values into swagger map
						endpointoperationInstance = new EndpointOperationType(entry.getKey(), "delete");
						// dataMap = new HashMap<Object, Object>();
						dataMap.put("definition", definitionMap);
						dataMap.put("response", responseMap);
						dataMap.put("baseURI", baseURI);
						dataMap.put("queryParameters", queryParameterMap);
						dataMap.put("pathParameters", pathParameterMap);
						dataMap.put("external", false);
						dataMap.put("skip", "false");

						DataLoadOperationType dataLoader = new DataLoadOperationType(endpointoperationInstance,
								dataMap);
						deleteOperationList.add(dataLoader);
					}
				}

				operationValidator = checkPath.getPost();

				if (!externalDataList.contains(entry.getKey().trim().toLowerCase() + "," + "post")) {
					if (operationValidator == null) {
						// System.out.println("post was null");
					} else {
						// Get all responses associated with the operation
						System.out.println("Operation: post");
						responseMap = new HashMap<String, Response>();
						responseMap = operationValidator.getResponses();

						// Get consumption list for Operation
						List<String> consumesList = operationValidator.getConsumes();
						dataMap = new HashMap<Object, Object>();
						// Initialize map to populate data
						feedData = new HashMap<String, Object>();

						// Get all Parameters associated with the operation
						parameterList = new ArrayList<Parameter>();
						parameterList = operationValidator.getParameters();

						dataMap.put("hasMandatoryParameterResolved", "true");

						queryParameterMap = new HashMap<String, Object>();
						pathParameterMap = new HashMap<String, Object>();
						for (Parameter param : parameterList) {
							if (param instanceof PathParameter) {
								if (((PathParameter) param).getDefaultValue() != null) {
									pathParameterMap.put(param.getName(), ((PathParameter) param).getDefaultValue());
								}
							}
							if (param instanceof QueryParameter) {
								if (((QueryParameter) param).getDefaultValue() != null) {
									queryParameterMap.put(param.getName(), ((QueryParameter) param).getDefaultValue());
								}
							}

							if (param instanceof PathParameter || param instanceof QueryParameter
									|| param instanceof HeaderParameter) {
								if (param.getRequired() && dataMap.get("hasMandatoryParameterResolved") == "true") {
									dataMap.put("hasMandatoryParameterResolved",
											checkInternalSwaggerParametersResolved(param));
								}
							}

							if (param instanceof BodyParameter) {
								BodyParameter bp = (BodyParameter) param;
								Model model = bp.getSchema();
								// System.out.println("Model is of type" + model.getClass());

								if (model instanceof RefModel) {
									// System.out.println("Model was found of type RefModel");
									RefModel ref = (RefModel) model;
									String simpleRef = ref.getSimpleRef();
									// System.out.println(simpleRef);

									Object test = ExampleBuilder.fromProperty(
											new io.swagger.models.properties.RefProperty(simpleRef), definitionMap);

									if (test != null && consumesList != null) {
										if (consumesList.contains("application/json")) {
											jsonString = Json.pretty(test);
											// System.out.println(jsonString);
											feedData.put("data", jsonString);
											feedData.put("schema", "application/json");
											dataMap.put("validdata", feedData);

											// Setup for invalid data
											feedData = new HashMap<String, Object>();
											feedData.put("data", sampleTextString);
											feedData.put("schema", "application/json");
											dataMap.put("invaliddata", feedData);

										} else if (consumesList.contains("application/xml")) {

											xmlString = new XmlExampleSerializer().serialize((Example) test);
											// System.out.println(xmlString);
											feedData.put("data", xmlString);
											feedData.put("schema", "application/xml");
											dataMap.put("validdata", feedData);

											// Setup for invalid data
											feedData = new HashMap<String, Object>();
											feedData.put("data", sampleJsonString);
											feedData.put("schema", "application/xml");
											dataMap.put("invaliddata", feedData);

										} else if (consumesList.contains("text/plain")) {
											textString = ((Example) test).asString();
											// System.out.println(textString);
											feedData.put("data", textString);
											feedData.put("schema", "text/plain");
											dataMap.put("validdata", feedData);

											// Setup for invalid data
											feedData = new HashMap<String, Object>();
											feedData.put("data", sampleJsonString);
											feedData.put("schema", "application/json");
											dataMap.put("invaliddata", feedData);

										}
									} else {
										dataMap.put("validdata", null);
									}

								} else if (model instanceof ArrayModel) {
									ArrayModel arrayModel = (ArrayModel) model;
									Property prop = arrayModel.getItems();
									if (prop instanceof RefProperty) {
										Object test = ExampleBuilder.fromProperty(new ArrayProperty(prop),
												definitionMap);

										if (test != null && consumesList != null) {
											if (consumesList.contains("application/json")) {
												jsonString = Json.pretty(test);
												// System.out.println(jsonString);
												feedData.put("data", jsonString);
												feedData.put("schema", "application/json");
												dataMap.put("validdata", feedData);

												// Setup for invalid data
												feedData = new HashMap<String, Object>();
												feedData.put("data", sampleTextString);
												feedData.put("schema", "application/json");
												dataMap.put("invaliddata", feedData);

											} else if (consumesList.contains("application/xml")) {

												xmlString = new XmlExampleSerializer().serialize((Example) test);
												// System.out.println(xmlString);
												feedData.put("data", xmlString);
												feedData.put("schema", "application/xml");
												dataMap.put("validdata", feedData);

												// Setup for invalid data
												feedData = new HashMap<String, Object>();
												feedData.put("data", sampleJsonString);
												feedData.put("schema", "application/xml");
												dataMap.put("invaliddata", feedData);

											} else if (consumesList.contains("text/plain")) {
												textString = ((Example) test).asString();
												// System.out.println(textString);
												feedData.put("data", textString);
												feedData.put("schema", "text/plain");
												dataMap.put("validdata", feedData);

												// Setup for invalid data
												feedData = new HashMap<String, Object>();
												feedData.put("data", sampleJsonString);
												feedData.put("schema", "application/json");
												dataMap.put("invaliddata", feedData);

											}
										} else {
											dataMap.put("validdata", null);
										}

									}
								}

							}
						}
						// Feed values into swagger map
						endpointoperationInstance = new EndpointOperationType(entry.getKey(), "post");
						// dataMap = new HashMap<Object, Object>();
						dataMap.put("definition", definitionMap);
						dataMap.put("response", responseMap);
						dataMap.put("baseURI", baseURI);
						dataMap.put("queryParameters", queryParameterMap);
						dataMap.put("pathParameters", pathParameterMap);
						dataMap.put("external", false);
						dataMap.put("skip", "false");

						DataLoadOperationType dataLoader = new DataLoadOperationType(endpointoperationInstance,
								dataMap);
						postOperationList.add(dataLoader);
					}
				}
				operationValidator = checkPath.getPut();

				if (!externalDataList.contains(entry.getKey().trim().toLowerCase() + "," + "put")) {
					if (operationValidator == null) {
						// System.out.println("put was null");
					} else {
						// Get all responses associated with the operation
						System.out.println("Operation: put");
						responseMap = new HashMap<String, Response>();
						responseMap = operationValidator.getResponses();

						// Get consumption list for Operation
						List<String> consumesList = operationValidator.getConsumes();
						dataMap = new HashMap<Object, Object>();
						// Initialize map to populate data
						feedData = new HashMap<String, Object>();

						// Get all Parameters associated with the operation
						parameterList = new ArrayList<Parameter>();
						parameterList = operationValidator.getParameters();
						// System.out.println("THIS IS A PUT OPERATION");

						dataMap.put("hasMandatoryParameterResolved", "true");

						queryParameterMap = new HashMap<String, Object>();
						pathParameterMap = new HashMap<String, Object>();
						for (Parameter param : parameterList) {
							if (param instanceof PathParameter) {
								if (((PathParameter) param).getDefaultValue() != null) {
									pathParameterMap.put(param.getName(), ((PathParameter) param).getDefaultValue());
								}
							}
							if (param instanceof QueryParameter) {
								if (((QueryParameter) param).getDefaultValue() != null) {
									queryParameterMap.put(param.getName(), ((QueryParameter) param).getDefaultValue());
								}
							}

							if (param instanceof PathParameter || param instanceof QueryParameter
									|| param instanceof HeaderParameter) {
								if (param.getRequired() && dataMap.get("hasMandatoryParameterResolved") == "true") {
									dataMap.put("hasMandatoryParameterResolved",
											checkInternalSwaggerParametersResolved(param));
								}
							}
							if (param instanceof BodyParameter) {
								// System.out.println("BODY PARAMETER WAS FOUND
								// for
								// a
								// put operation");
								BodyParameter bp = (BodyParameter) param;
								Model model = bp.getSchema();
								Object o = model.getExample();
								if (o == null) {

								} else {

									System.out.println("Object was not null someting to work with");
								}
								// System.out.println("Model is of type" + model.getClass());
								if (model instanceof RefModel) {
									// System.out.println("Model was found of type RefModel");
									RefModel ref = (RefModel) model;
									String simpleRef = ref.getSimpleRef();
									// System.out.println(simpleRef);

									Object test = ExampleBuilder.fromProperty(
											new io.swagger.models.properties.RefProperty(simpleRef), definitionMap);

									if (test != null && consumesList != null) {
										if (consumesList.contains("application/json")) {
											jsonString = Json.pretty(test);
											// System.out.println(jsonString);
											feedData.put("data", jsonString);
											feedData.put("schema", "application/json");
											dataMap.put("validdata", feedData);

											// Setup for invalid data
											feedData = new HashMap<String, Object>();
											feedData.put("data", sampleTextString);
											feedData.put("schema", "application/json");
											dataMap.put("invaliddata", feedData);

										} else if (consumesList.contains("application/xml")) {

											xmlString = new XmlExampleSerializer().serialize((Example) test);
											// System.out.println(xmlString);
											feedData.put("data", xmlString);
											feedData.put("schema", "application/xml");
											dataMap.put("validdata", feedData);

											// Setup for invalid data
											feedData = new HashMap<String, Object>();
											feedData.put("data", sampleJsonString);
											feedData.put("schema", "application/xml");
											dataMap.put("invaliddata", feedData);

										} else if (consumesList.contains("text/plain")) {
											textString = ((Example) test).asString();
											// System.out.println(textString);
											feedData.put("data", textString);
											feedData.put("schema", "text/plain");
											dataMap.put("validdata", feedData);

											// Setup for invalid data
											feedData = new HashMap<String, Object>();
											feedData.put("data", sampleJsonString);
											feedData.put("schema", "application/json");
											dataMap.put("invaliddata", feedData);

										}
									} else {
										dataMap.put("validdata", null);
									}

								} else if (model instanceof ArrayModel) {
									ArrayModel arrayModel = (ArrayModel) model;
									Property prop = arrayModel.getItems();
									if (prop instanceof RefProperty) {
										Object test = ExampleBuilder.fromProperty(new ArrayProperty(prop),
												definitionMap);

										if (test != null && consumesList != null) {
											if (consumesList.contains("application/json")) {
												jsonString = Json.pretty(test);
												// System.out.println(jsonString);
												feedData.put("data", jsonString);
												feedData.put("schema", "application/json");
												dataMap.put("validdata", feedData);

												// Setup for invalid data
												feedData = new HashMap<String, Object>();
												feedData.put("data", sampleTextString);
												feedData.put("schema", "application/json");
												dataMap.put("invaliddata", feedData);

											} else if (consumesList.contains("application/xml")) {

												xmlString = new XmlExampleSerializer().serialize((Example) test);
												// System.out.println(xmlString);
												feedData.put("data", xmlString);
												feedData.put("schema", "application/xml");
												dataMap.put("validdata", feedData);

												// Setup for invalid data
												feedData = new HashMap<String, Object>();
												feedData.put("data", sampleJsonString);
												feedData.put("schema", "application/xml");
												dataMap.put("invaliddata", feedData);

											} else if (consumesList.contains("text/plain")) {
												textString = ((Example) test).asString();
												// System.out.println(textString);
												feedData.put("data", textString);
												feedData.put("schema", "text/plain");
												dataMap.put("validdata", feedData);

												// Setup for invalid data
												feedData = new HashMap<String, Object>();
												feedData.put("data", sampleJsonString);
												feedData.put("schema", "application/json");
												dataMap.put("invaliddata", feedData);

											}
										} else {
											dataMap.put("validdata", null);
										}

									}
								}

							}
						}

						// Feed values into swagger map
						endpointoperationInstance = new EndpointOperationType(entry.getKey(), "put");
						// dataMap = new HashMap<Object, Object>();
						dataMap.put("definition", definitionMap);
						dataMap.put("response", responseMap);
						dataMap.put("baseURI", baseURI);
						dataMap.put("queryParameters", queryParameterMap);
						dataMap.put("pathParameters", pathParameterMap);
						dataMap.put("external", false);
						dataMap.put("skip", "false");

						DataLoadOperationType dataLoader = new DataLoadOperationType(endpointoperationInstance,
								dataMap);
						putOperationList.add(dataLoader);

					}
				}

			}
			// Merge all operation lists into a common global operation list
			// after
			// sequencing the operations
			// Generate the swaggerMap from this global operation List
			globalOperationList.addAll(postOperationList);
			globalOperationList.addAll(putOperationList);
			globalOperationList.addAll(getOperationList);
			globalOperationList.addAll(deleteOperationList);

			// Iterate through globalOperationList sorted and generate final
			// swagger
			// Map
			for (DataLoadOperationType dataLoader : globalOperationList) {
				swaggerMap.put(dataLoader.getEndpointOperationType(), dataLoader.getDataMap());
			}
		} catch (Exception e) {
			e.printStackTrace();
			swaggerMap = null;
		} finally {
			globalOperationList = null;
			externalDataList = null;
			postOperationList = null;
			getOperationList = null;
			deleteOperationList = null;
			putOperationList = null;
		}

		return swaggerMap;

	}

	/* Check if path parameter is resolved for endpoint before test execution */
	public static boolean hasPathParameterResolved(String endPoint) {

		if (endPoint.toLowerCase().contains("{") || endPoint.toLowerCase().contains("}")) {
			return false;
		} else {
			return true;
		}

	}

	/*
	 * Shreejit Nair Generate external data model if external data support is
	 * provided
	 * 
	 */
	public boolean generateExternalDataModel(JSONArray testdataArray) {

		boolean validOperation;
		EndpointOperationType endpointoperationInstance;
		Map<Object, Object> dataMap = null;
		Map<String, Object> authenticationMap = null;
		Map<String, Object> responseMap = null;
		Map<String, Object> feedData = null;
		Map<String, Object> queryParameterMap = null;
		Map<String, Object> pathParameterMap = null;
		Map<String, Object> headerParameterMap = null;
		Map<String, Object> cookieParameterMap = null;

		try {
			if (testdataArray != null) {
				// Begin generating logic
				for (int i = 0, size = testdataArray.length(); i < size; i++) {
					JSONObject objectInArray = testdataArray.getJSONObject(i);

					String[] endpointArray = JSONObject.getNames(objectInArray);
					for (String elementName : endpointArray) {
						String endpoint = elementName;
						JSONObject endpointObject = objectInArray.getJSONObject(elementName);
						String endpointStringRepresentation = objectInArray.toString();
						JSONObject inputObject = endpointObject.getJSONObject("input");
						String operation = inputObject.getString("operation");
						String skip = inputObject.getString("skip");

						// validate endpoint,operation,supplied parameters
						validOperation = checkValidEndpointOperation(endpoint, operation, testdataArray);
						endpointoperationInstance = new EndpointOperationType(endpoint, operation);
						dataMap = new HashMap<Object, Object>();
						dataMap.put("external", "true");
						dataMap.put("contentAsString", endpointStringRepresentation);
						dataMap.put("accept", inputObject.getString("accept"));
						dataMap.put("baseURI", BASE_URI);
						String validEndpointOperationData = endpoint.trim().toLowerCase() + ","
								+ operation.trim().toLowerCase();
						externalDataList.add(validEndpointOperationData);

						if (!skip.trim().toLowerCase().equals("true")) {
							if (validOperation) {
								dataMap.put("skip", "false");
								dataMap.put("hasMandatoryParameterResolved", "true");
								// basic authentication

								JSONObject authentication = inputObject.optJSONObject("basicauthentication");
								if (authentication != null) {
									authenticationMap = new HashMap<String, Object>();
									authenticationMap.put("username", authentication.getString("username"));
									authenticationMap.put("password", authentication.getString("password"));
									dataMap.put("basicauthentication", authenticationMap);
								}

								// body parameters

								JSONArray bodyParameterArray = inputObject.optJSONArray("bodyparameters");
								if (bodyParameterArray != null) {
									feedData = new HashMap<String, Object>();
									for (int ibodyParameterCount = 0; ibodyParameterCount < bodyParameterArray
											.length(); ibodyParameterCount++) {
										JSONObject bodyobjectInArray = bodyParameterArray
												.getJSONObject(ibodyParameterCount);
										String[] bodyendpointArray = JSONObject.getNames(bodyobjectInArray);
										for (String bodyelementName : bodyendpointArray) {
											feedData.put(bodyelementName, bodyobjectInArray.getString(bodyelementName));
										}

									}
									dataMap.put("bodyParameters", feedData);
								}

								// queryParameters
								JSONArray queryParameterArray = inputObject.optJSONArray("queryparameters");
								if (queryParameterArray != null) {
									queryParameterMap = new HashMap<String, Object>();
									for (int iqueryParameterCount = 0; iqueryParameterCount < queryParameterArray
											.length(); iqueryParameterCount++) {
										JSONObject queryobjectInArray = queryParameterArray
												.getJSONObject(iqueryParameterCount);
										String[] queryendpointArray = JSONObject.getNames(queryobjectInArray);
										for (String queryelementName : queryendpointArray) {
											queryParameterMap.put(queryelementName,
													queryobjectInArray.getString(queryelementName));
										}

									}
									dataMap.put("queryParameters", queryParameterMap);
								}

								// pathParameters
								JSONArray pathParameterArray = inputObject.optJSONArray("pathparameters");
								if (pathParameterArray != null) {
									pathParameterMap = new HashMap<String, Object>();
									for (int ipathParameterCount = 0; ipathParameterCount < pathParameterArray
											.length(); ipathParameterCount++) {
										JSONObject pathobjectInArray = pathParameterArray
												.getJSONObject(ipathParameterCount);
										String[] pathendpointArray = JSONObject.getNames(pathobjectInArray);
										for (String pathelementName : pathendpointArray) {
											pathParameterMap.put(pathelementName,
													pathobjectInArray.getString(pathelementName));
										}

									}
									dataMap.put("pathParameters", pathParameterMap);
								}

								// Header parameters
								JSONArray headerParameterArray = inputObject.optJSONArray("headerparameters");
								if (headerParameterArray != null) {
									headerParameterMap = new HashMap<String, Object>();
									for (int iheaderParameterCount = 0; iheaderParameterCount < pathParameterArray
											.length(); iheaderParameterCount++) {
										JSONObject headerobjectInArray = headerParameterArray
												.getJSONObject(iheaderParameterCount);
										String[] headerendpointArray = JSONObject.getNames(headerobjectInArray);
										for (String headerelementName : headerendpointArray) {
											headerParameterMap.put(headerelementName,
													headerobjectInArray.getString(headerelementName));
										}

									}
									dataMap.put("headerParameters", headerParameterMap);
								}

								// Cookie Parameters
								JSONArray cookieParameterArray = inputObject.optJSONArray("cookieparameters");
								if (cookieParameterArray != null) {
									cookieParameterMap = new HashMap<String, Object>();
									for (int icookieParameterCount = 0; icookieParameterCount < cookieParameterArray
											.length(); icookieParameterCount++) {
										JSONObject cookieobjectInArray = cookieParameterArray
												.getJSONObject(icookieParameterCount);
										String[] cookieendpointArray = JSONObject.getNames(cookieobjectInArray);
										for (String cookieelementName : cookieendpointArray) {
											cookieParameterMap.put(cookieelementName,
													cookieobjectInArray.getString(cookieelementName));
										}

									}
									dataMap.put("cookieParameters", cookieParameterMap);
								}

								// Response object
								JSONObject responseObject = endpointObject.getJSONObject("response");
								if (responseObject != null) {
									responseMap = new HashMap<String, Object>();
									responseMap.put("statuscode", responseObject.getString("statuscode"));
									dataMap.put("response", responseMap);
								}

							} else {
								dataMap.put("hasMandatoryParameterResolved", "false");
							}
						} else {
							dataMap.put("skip", "true");
						}
						DataLoadOperationType dataLoader = new DataLoadOperationType(endpointoperationInstance,
								dataMap);
						switch (operation.toLowerCase()) {
						case "post":
							postOperationList.add(dataLoader);
							break;
						case "put":
							putOperationList.add(dataLoader);
							break;
						case "get":
							getOperationList.add(dataLoader);
							break;
						case "delete":
							deleteOperationList.add(dataLoader);
							break;
						}

					}

				}

			} else {
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
		return true;

	}

	/* Check validEndpoint before uploading and executing it */
	public boolean checkValidEndpointOperation(String endpoint, String operation, JSONArray testdataArray) {

		Operation op = null;
		boolean status = false;
		boolean parameterStatus = true;
		List<Parameter> parameterList = null;

		try {

			Swagger swagger = new SwaggerParser().read(SWAGGER_URL);
			Path endpointPath = swagger.getPath(endpoint);
			if (endpointPath != null) {
				switch (operation.toLowerCase()) {
				case "get":
					op = endpointPath.getGet();
					if (op != null) {
						parameterList = op.getParameters();
						for (Parameter param : parameterList) {
							if (param instanceof PathParameter || param instanceof QueryParameter
									|| param instanceof HeaderParameter || param instanceof CookieParameter
									|| param instanceof BodyParameter) {
								if (param.getRequired() && parameterStatus == true) {
									parameterStatus = checkMandatoryParameterResolved(endpoint, "get", param,
											testdataArray);
								}
							}
						}
						status = parameterStatus;
					} else {
						status = false;
					}
					break;
				case "put":
					op = endpointPath.getPut();
					if (op != null) {
						parameterList = op.getParameters();
						for (Parameter param : parameterList) {
							if (param instanceof PathParameter || param instanceof QueryParameter
									|| param instanceof HeaderParameter || param instanceof CookieParameter
									|| param instanceof BodyParameter) {
								if (param.getRequired() && parameterStatus == true) {
									parameterStatus = checkMandatoryParameterResolved(endpoint, "put", param,
											testdataArray);
								}
							}
						}
						status = parameterStatus;
					} else {
						status = false;
					}
					break;
				case "post":
					op = endpointPath.getPost();
					if (op != null) {
						parameterList = op.getParameters();
						for (Parameter param : parameterList) {
							if (param instanceof PathParameter || param instanceof QueryParameter
									|| param instanceof HeaderParameter || param instanceof CookieParameter
									|| param instanceof BodyParameter) {
								if (param.getRequired() && parameterStatus == true) {
									parameterStatus = checkMandatoryParameterResolved(endpoint, "post", param,
											testdataArray);
								}
							}
						}
						status = parameterStatus;

					} else {
						status = false;
					}
					break;
				case "delete":
					op = endpointPath.getDelete();
					if (op != null) {
						parameterList = op.getParameters();
						for (Parameter param : parameterList) {
							if (param instanceof PathParameter || param instanceof QueryParameter
									|| param instanceof HeaderParameter || param instanceof CookieParameter
									|| param instanceof BodyParameter) {
								if (param.getRequired() && parameterStatus == true) {
									parameterStatus = checkMandatoryParameterResolved(endpoint, "delete", param,
											testdataArray);
								}
							}
						}
						status = parameterStatus;
					} else {
						status = false;
					}
					break;
				default:
					status = false;
				}

			} else {
				status = false;
			}
		} catch (Exception e) {
			status = false;
		}

		return status;
	}

	/*
	 * Check internal Swagger spec parameter resolved for internal specification
	 * documents
	 * 
	 */
	public String checkInternalSwaggerParametersResolved(Parameter param) {

		if (param instanceof QueryParameter) {
			if (((QueryParameter) param).getDefaultValue() != null) {
				return "true";
			}
		}
		if (param instanceof PathParameter) {
			if (((PathParameter) param).getDefaultValue() != null) {
				return "true";
			}
		}
		if (param instanceof CookieParameter) {
			if (((CookieParameter) param).getDefaultValue() != null) {
				return "true";
			}
		}
		if (param instanceof HeaderParameter) {
			if (((HeaderParameter) param).getDefaultValue() != null) {
				return "true";
			}
		}
		return "false";
	}

	/* Check Mandatory Parameter Resolved */
	public static boolean checkMandatoryParameterResolved(String endpoint, String operation, Parameter param,
			JSONArray testDataArray) {

		boolean bParameterFound = false;
		JSONObject input = null;

		try {
			outerloop: for (int i = 0, size = testDataArray.length(); i < size; i++) {
				JSONObject objectInArray = testDataArray.getJSONObject(i);
				String[] elementNames = JSONObject.getNames(objectInArray);
				for (String elementName : elementNames) {
					String value = elementName;
					if (endpoint.trim().equals(value.trim())) {
						JSONObject internalObject = objectInArray.getJSONObject(elementName);
						input = internalObject.getJSONObject("input");

						if (input.get("operation").equals(operation)) {
							bParameterFound = true;
							break outerloop;
						}
					}
				}
			}
			// Fill up remaining parameters like CookieParameter, FormParameter
			// /* TO DO */
			JSONArray parameterObject;
			if (bParameterFound) {
				bParameterFound = false;
				if (param instanceof QueryParameter) {

					parameterObject = input.getJSONArray("queryparameters");
					for (int i = 0, size = parameterObject.length(); i < size; i++) {
						JSONObject queryparameterInArray = parameterObject.getJSONObject(i);
						String[] elementNames = JSONObject.getNames(queryparameterInArray);
						loop: for (String elementName : elementNames) {
							if (param.getName().trim().equals(elementName.trim())) {
								bParameterFound = true;
								break loop;
							}

						}

					}

				}

				else if (param instanceof PathParameter) {

					parameterObject = input.getJSONArray("pathparameters");
					for (int i = 0, size = parameterObject.length(); i < size; i++) {
						JSONObject pathparameterInArray = parameterObject.getJSONObject(i);
						String[] elementNames = JSONObject.getNames(pathparameterInArray);
						loop: for (String elementName : elementNames) {
							if (param.getName().trim().equals(elementName.trim())) {
								bParameterFound = true;
								break loop;
							}

						}

					}

				}

				else if (param instanceof HeaderParameter) {

					parameterObject = input.getJSONArray("headerparameters");
					for (int i = 0, size = parameterObject.length(); i < size; i++) {
						JSONObject headerparameterInArray = parameterObject.getJSONObject(i);
						String[] elementNames = JSONObject.getNames(headerparameterInArray);
						loop: for (String elementName : elementNames) {
							if (param.getName().trim().equals(elementName.trim())) {
								bParameterFound = true;
								break loop;
							}

						}

					}

				}

				else if (param instanceof CookieParameter) {

					parameterObject = input.getJSONArray("cookieparameters");
					for (int i = 0, size = parameterObject.length(); i < size; i++) {
						JSONObject cookieparameterInArray = parameterObject.getJSONObject(i);
						String[] elementNames = JSONObject.getNames(cookieparameterInArray);
						loop: for (String elementName : elementNames) {
							if (param.getName().trim().equals(elementName.trim())) {
								bParameterFound = true;
								break loop;
							}

						}

					}

				}

				else {
					bParameterFound = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			bParameterFound = false;
		}

		return bParameterFound;
	}

	@SuppressWarnings("resource")
	public static String readFile(String filename) {
		String result = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
			result = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public void executeTestFramework(String swaggerurl) {

		// Execute the test framework from here
		/*
		 * String swaggerString = readFile("Data.properties"); SwaggerUtility
		 * swaggerDocument = new SwaggerUtility(); Map<Object, Object> finalMap =
		 * swaggerDocument.getSwaggerData(swaggerString);
		 * System.out.println("final map is generated debug and check once");
		 * 
		 * // Iterate through Final map and see output for (Map.Entry<Object, Object>
		 * output : finalMap.entrySet()) {
		 * 
		 * EndpointOperationType endpointtype = (EndpointOperationType) output.getKey();
		 * System.out.println(endpointtype.getEndpoint() + "+" +
		 * endpointtype.getOperation()); }
		 * 
		 * // Iterate and find external data setup Model
		 * System.out.println("Start printing external data list"); for (String s :
		 * swaggerDocument.externalDataList) { System.out.println(s); }
		 */
		String swaggerPath = swaggerurl;
		SwaggerExecutionEngine engine = new SwaggerExecutionEngine();
		TestFrameworkOutput executionOutput = engine.executeTestFramework(swaggerPath);
		System.exit(executionOutput.getResponseCode()); // Exit with custom response code

	}

}

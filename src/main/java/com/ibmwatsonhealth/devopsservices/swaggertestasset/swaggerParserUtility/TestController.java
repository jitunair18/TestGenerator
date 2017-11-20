package com.ibmwatsonhealth.devopsservices.swaggertestasset.swaggerParserUtility;

/**
 * Driver for Test Framework
 */
public class TestController {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// System.out.println("hello");
		if (args != null && args.length > 0) {
			String option = args[0];
			String swaggerurl = args[1];

			/*
			 * if (args.length > 1) { args2 = new String[args.length - 1];
			 * System.arraycopy(args, 1, args2, 0, args2.length); }
			 */
			// System.out.println(swaggerurl);

			// ******************* Test Driver hooks to execute input main class

			switch (option.toLowerCase()) {

			case "swaggerutility":
				new SwaggerUtility().executeTestFramework(swaggerurl);
				break;
			case "serviceinformation":
				new ServiceInformation().getSwaggerInfo(swaggerurl);
				break;
			default:
				System.out.println("Not supported execution for input class");
				System.exit(-1);
				break;

			}

			// *************************************************************************************************************

		}
	}
}
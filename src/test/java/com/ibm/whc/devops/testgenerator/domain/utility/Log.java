package com.ibm.whc.devops.testgenerator.domain.utility;

import org.apache.log4j.Logger;

public class Log {

	private static Logger Log = Logger.getLogger(Log.class.getName());

	// Log4j generic API handling routine exposed

	public static void info(Object message) {

		Log.info(message);

	}

	public static void warn(Object message) {

		Log.warn(message);

	}

	public static void error(Object message, Throwable t) {

		Log.error(message,t);

	}

	public static void fatal(Object message) {

		Log.fatal(message);

	}

	public static void debug(Object message) {

		Log.debug(message);

	}

}

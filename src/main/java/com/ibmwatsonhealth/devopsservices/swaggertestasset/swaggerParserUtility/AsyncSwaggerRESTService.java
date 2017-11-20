package com.ibmwatsonhealth.devopsservices.swaggertestasset.swaggerParserUtility;

import java.util.concurrent.TimeUnit;
import javax.ws.rs.GET;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.container.TimeoutHandler;
import javax.ws.rs.core.Response;

public class AsyncSwaggerRESTService {
	
	@GET
	public void asyncGetWithTimeout(@Suspended final AsyncResponse asyncResponse) {
	    asyncResponse.setTimeoutHandler(new TimeoutHandler() {
	 
	        @Override
	        public void handleTimeout(AsyncResponse asyncResponse) {
	            asyncResponse.resume(Response.status(Response.Status.SERVICE_UNAVAILABLE)
	                    .entity("Operation time out.").build());
	            
	            
	        }
	    });
	    asyncResponse.setTimeout(20, TimeUnit.MINUTES);
	 
	    new Thread(new Runnable() {
	 
	        @Override
	        public void run() {
	            String result = veryExpensiveOperation();
	            asyncResponse.resume(result);
	        }
	 
	        private String veryExpensiveOperation() {
	            // ... very expensive operation that typically finishes within 20 seconds
	        	return "something";
	        }
	    }).start();
	}

}

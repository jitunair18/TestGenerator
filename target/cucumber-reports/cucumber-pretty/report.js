$(document).ready(function() {var formatter = new CucumberHTML.DOMFormatter($('.cucumber-report'));formatter.uri("src/test/java/resources/features/Feature011_CreateSqlDataMartSchema.feature");
formatter.feature({
  "comments": [
    {
      "line": 1,
      "value": "# TCERID: 5970"
    },
    {
      "line": 2,
      "value": "# TEST CASE: ANA _ DM Creation of relational database multiple tables with schema."
    },
    {
      "line": 3,
      "value": "# Description: The service provides the ability to create one or more relational database tables with a particular schema,"
    },
    {
      "line": 4,
      "value": "#via the use of a create datamart REST API, in which a SQL schema is included as part of the request."
    }
  ],
  "line": 6,
  "name": "DataMart SQL DDL Schema creation",
  "description": "",
  "id": "datamart-sql-ddl-schema-creation",
  "keyword": "Feature",
  "tags": [
    {
      "line": 5,
      "name": "@createSqlSchemaDataMart"
    }
  ]
});
formatter.before({
  "duration": 1588911,
  "status": "passed"
});
formatter.scenario({
  "line": 7,
  "name": "Create new SQL DDL Schema in Datamart",
  "description": "",
  "id": "datamart-sql-ddl-schema-creation;create-new-sql-ddl-schema-in-datamart",
  "type": "scenario",
  "keyword": "Scenario"
});
formatter.step({
  "line": 9,
  "name": "user sends a POST request to \"https://idmz-dp.ci41.lsf04.ibmwhc.net:9443/datamart/api/v1/\"",
  "keyword": "When "
});
formatter.step({
  "line": 10,
  "name": "header with",
  "rows": [
    {
      "cells": [
        "key",
        "value"
      ],
      "line": 11
    },
    {
      "cells": [
        "Accept",
        "application/json"
      ],
      "line": 12
    },
    {
      "cells": [
        "iv-user",
        "hatestdb2"
      ],
      "line": 13
    }
  ],
  "keyword": "And "
});
formatter.step({
  "line": 14,
  "name": "content",
  "keyword": "And ",
  "doc_string": {
    "content_type": "",
    "line": 15,
    "value": "{\"mode\":\"private\", \"name\":\"test_export\", \"description\":\"testing\", \"owner\":\"hatestdb2\", \"datamartType\":\"db2native\", \"schema\":\"CREATE TABLE TEST2 (id bigint NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1), FIELD1 varchar(255) NOT NULL, FIELD2 bigint NOT NULL, PRIMARY KEY (id));\" }  \n"
  }
});
formatter.step({
  "comments": [
    {
      "line": 19,
      "value": "# set variable data mart id from previous step"
    }
  ],
  "line": 20,
  "name": "response value \"id\" is stored",
  "keyword": "Then "
});
formatter.step({
  "line": 21,
  "name": "response status code should be 201",
  "keyword": "And "
});
formatter.step({
  "comments": [
    {
      "line": 23,
      "value": "# check if asynchronous processing is completed"
    },
    {
      "line": 24,
      "value": "# variables tagged {global} would be resolved before execution"
    },
    {
      "line": 25,
      "value": "# build the http request object"
    }
  ],
  "line": 26,
  "name": "user sends asynchronous GET request to \"https://idmz-dp.ci41.lsf04.ibmwhc.net:9443/datamart/api/v1/{id}\"",
  "keyword": "When "
});
formatter.step({
  "line": 27,
  "name": "path parameter with",
  "rows": [
    {
      "cells": [
        "key",
        "value"
      ],
      "line": 28
    },
    {
      "cells": [
        "id",
        "{global}"
      ],
      "line": 29
    }
  ],
  "keyword": "And "
});
formatter.step({
  "line": 30,
  "name": "header with",
  "rows": [
    {
      "cells": [
        "key",
        "value"
      ],
      "line": 31
    },
    {
      "cells": [
        "Accept",
        "application/json"
      ],
      "line": 32
    },
    {
      "cells": [
        "iv-user",
        "hatestdb2"
      ],
      "line": 33
    }
  ],
  "keyword": "And "
});
formatter.step({
  "comments": [
    {
      "line": 34,
      "value": "# keep actively polling till \"status\" field in response changes to \"DONE\""
    }
  ],
  "line": 35,
  "name": "waits on \"status\" to be \"Done\"",
  "keyword": "And "
});
formatter.step({
  "line": 36,
  "name": "maxTimeOut is 500 seconds",
  "keyword": "And "
});
formatter.step({
  "comments": [
    {
      "line": 37,
      "value": "# start running assertions on the response"
    }
  ],
  "line": 38,
  "name": "response status code should be 200",
  "keyword": "Then "
});
formatter.step({
  "line": 39,
  "name": "response header contains",
  "rows": [
    {
      "cells": [
        "key",
        "value"
      ],
      "line": 40
    },
    {
      "cells": [
        "content-type",
        "application/json"
      ],
      "line": 41
    }
  ],
  "keyword": "And "
});
formatter.step({
  "line": 42,
  "name": "response status line is \"OK\"",
  "keyword": "And "
});
formatter.match({
  "arguments": [
    {
      "val": "https://idmz-dp.ci41.lsf04.ibmwhc.net:9443/datamart/api/v1/",
      "offset": 30
    }
  ],
  "location": "StepDefinitionsRestServices.user_sends_a_POST_request_to(String)"
});
formatter.result({
  "duration": 323709303,
  "error_message": "java.lang.IllegalArgumentException: SSL context may not be null\n\tat org.apache.http.util.Args.notNull(Args.java:54)\n\tat org.apache.http.conn.ssl.SSLSocketFactory.\u003cinit\u003e(SSLSocketFactory.java:349)\n\tat com.ibmwatsonhealth.devopsservices.swaggertestasset.swaggerParserUtility.domain.RESTFactory.getSSLCert(RESTFactory.java:718)\n\tat com.ibmwatsonhealth.devopsservices.swaggertestasset.swaggerParserUtility.domain.RESTFactory.postRequest(RESTFactory.java:307)\n\tat com.ibmwatsonhealth.devopsservices.swaggertestasset.swaggerParserUtility.stepdefinitions.StepDefinitionsRestServices.user_sends_a_POST_request_to(StepDefinitionsRestServices.java:147)\n\tat ✽.When user sends a POST request to \"https://idmz-dp.ci41.lsf04.ibmwhc.net:9443/datamart/api/v1/\"(src/test/java/resources/features/Feature011_CreateSqlDataMartSchema.feature:9)\n",
  "status": "failed"
});
formatter.match({
  "location": "StepDefinitionsRestServices.header_with(DataTable)"
});
formatter.result({
  "status": "skipped"
});
formatter.match({
  "location": "StepDefinitionsRestServices.content(String)"
});
formatter.result({
  "status": "skipped"
});
formatter.match({
  "arguments": [
    {
      "val": "id",
      "offset": 16
    }
  ],
  "location": "StepDefinitionsRestServices.response_value_is_stored(String)"
});
formatter.result({
  "status": "skipped"
});
formatter.match({
  "arguments": [
    {
      "val": "201",
      "offset": 31
    }
  ],
  "location": "StepDefinitionsRestServices.response_status_code_should_be(int)"
});
formatter.result({
  "status": "skipped"
});
formatter.match({
  "arguments": [
    {
      "val": "https://idmz-dp.ci41.lsf04.ibmwhc.net:9443/datamart/api/v1/{id}",
      "offset": 40
    }
  ],
  "location": "StepDefinitionsRestServices.user_sends_asynchronous_GET_request_to(String)"
});
formatter.result({
  "status": "skipped"
});
formatter.match({
  "location": "StepDefinitionsRestServices.path_parameter_with(DataTable)"
});
formatter.result({
  "status": "skipped"
});
formatter.match({
  "location": "StepDefinitionsRestServices.header_with(DataTable)"
});
formatter.result({
  "status": "skipped"
});
formatter.match({
  "arguments": [
    {
      "val": "status",
      "offset": 10
    },
    {
      "val": "Done",
      "offset": 25
    }
  ],
  "location": "StepDefinitionsRestServices.waits_on_to_be(String,String)"
});
formatter.result({
  "status": "skipped"
});
formatter.match({
  "arguments": [
    {
      "val": "500",
      "offset": 14
    }
  ],
  "location": "StepDefinitionsRestServices.maxtimeout_is_ms(int)"
});
formatter.result({
  "status": "skipped"
});
formatter.match({
  "arguments": [
    {
      "val": "200",
      "offset": 31
    }
  ],
  "location": "StepDefinitionsRestServices.response_status_code_should_be(int)"
});
formatter.result({
  "status": "skipped"
});
formatter.match({
  "location": "StepDefinitionsRestServices.response_header_contains(DataTable)"
});
formatter.result({
  "status": "skipped"
});
formatter.match({
  "arguments": [
    {
      "val": "OK",
      "offset": 25
    }
  ],
  "location": "StepDefinitionsRestServices.response_status_line_is(String)"
});
formatter.result({
  "status": "skipped"
});
formatter.after({
  "duration": 65082,
  "status": "passed"
});
});
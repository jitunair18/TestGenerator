$(document).ready(function() {var formatter = new CucumberHTML.DOMFormatter($('.cucumber-report'));formatter.uri("src/test/java/resources/features/Feature01_CreateNewUser.feature");
formatter.feature({
  "id": "user-creation",
  "tags": [
    {
      "name": "@createUser",
      "line": 2
    }
  ],
  "description": "",
  "name": "User Creation",
  "keyword": "Feature",
  "line": 3,
  "comments": [
    {
      "value": "#  Create user in the system",
      "line": 1
    }
  ]
});
formatter.before({
  "duration": 2114000,
  "status": "passed"
});
formatter.scenario({
  "id": "user-creation;create-new-user-in-the-system",
  "description": "",
  "name": "Create new user in the system",
  "keyword": "Scenario",
  "line": 4,
  "type": "scenario"
});
formatter.step({
  "name": "the Swagger definition at http://petstore.swagger.io/v2/swagger.json",
  "keyword": "Given ",
  "line": 5
});
formatter.step({
  "name": "user sends a POST request to \"http://petstore.swagger.io/v2/user\"",
  "keyword": "When ",
  "line": 6
});
formatter.step({
  "name": "content",
  "keyword": "And ",
  "line": 7,
  "doc_string": {
    "value": "{\n\"id\": 0,\n\"username\": \"Shreejit\",\n\"firstName\": \"string\",\n\"lastName\": \"string\",\n\"email\": \"string\",\n\"password\": \"string\",\n\"phone\": \"string\",\n\"userStatus\": 0\n}",
    "line": 8,
    "content_type": ""
  }
});
formatter.step({
  "name": "response status code should be 200",
  "keyword": "Then ",
  "line": 20
});
formatter.step({
  "name": "response time is within 600 ms",
  "keyword": "And ",
  "line": 21
});
formatter.step({
  "name": "response header contains",
  "keyword": "And ",
  "line": 22,
  "rows": [
    {
      "cells": [
        "key",
        "value"
      ],
      "line": 23
    },
    {
      "cells": [
        "content-type",
        "application/json"
      ],
      "line": 24
    }
  ]
});
formatter.step({
  "name": "response status line is \"OK\"",
  "keyword": "And ",
  "line": 25
});
formatter.match({
  "arguments": [
    {
      "val": "http://petstore.swagger.io/v2/swagger.json",
      "offset": 26
    }
  ],
  "location": "StepDefinitionsRestServices.getSwaggerInformation(String)"
});
formatter.result({
  "duration": 474240000,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "http://petstore.swagger.io/v2/user",
      "offset": 30
    }
  ],
  "location": "StepDefinitionsRestServices.user_sends_a_POST_request_to(String)"
});
formatter.result({
  "duration": 362181000,
  "status": "passed"
});
formatter.match({
  "location": "StepDefinitionsRestServices.content(String)"
});
formatter.result({
  "duration": 32276000,
  "status": "passed"
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
  "duration": 500690000,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "600",
      "offset": 24
    }
  ],
  "location": "StepDefinitionsRestServices.response_time_is_within_s(String)"
});
formatter.result({
  "duration": 1476000,
  "status": "passed"
});
formatter.match({
  "location": "StepDefinitionsRestServices.response_header_contains(DataTable)"
});
formatter.result({
  "duration": 927000,
  "status": "passed"
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
  "duration": 161000,
  "status": "passed"
});
formatter.after({
  "duration": 39000,
  "status": "passed"
});
});
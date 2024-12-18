# REST_Assured_API_Automation_testing

I will share my first Rest Assured API Automation testing in this repo. So in this learning period, I will learn the most amazing techniques and details about Rest Assured API Automation. Now i wanna share my kint knowledge about REST Assured API Automation.
In this API Automation, i automated a basic login system that takes user ID and password, and after entering this user credential the server sends us e login OTP then users will get a message in the terminal "Login Successfully"
Using Rest Assured, automation testing of APIs, and sending simple HTTP requests with user-friendly customizations is simple if one has a basic background in Java. It is needed to understand API testing and integration testing, but post that automation Rest Assured gives very good confidence on the backend while front-end testing can just focus on the UI and client-side operations. Rest Assured is an open source with a lot of additional methods and libraries being added has made it a great choice for API automation.

## Syntax:
The syntax of Rest Assured.io is the most beautiful part, as it is very BDD-like and understandable.

  ``` bash
Given(). 
        param("x", "y"). 
        header("z", "w").
when().
Method().
Then(). 
        statusCode(XXX).
        body("x, ”y", equalTo("z"));
 ```

## Explanation:
Code	Explanation
``` Given()``` ‘Given’ keyword, lets you set a background, here, you pass the request headers, query and path param, body, and cookies. This is optional if these items are not needed in the request
``` When()```   ‘when’ keyword marks the premise of your scenario. For example, ‘when’ you get/post/put something, do something else.
``` Method()``` Substitute this with any of the CRUD operations(get/post/put/delete)
``` Then()```   Your assert and matcher conditions go here

## The Objective of my test:
The goal of the script is to print the same output on my IDE console as what i received on the browser through Rest Assured.

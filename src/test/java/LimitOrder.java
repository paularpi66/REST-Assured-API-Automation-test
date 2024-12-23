//package test.java;

import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import io.restassured.response.ResponseBody;
import io.restassured.response.Response;

import java.util.*;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasLength;


public class LimitOrder {
    @Test (priority = 1)
    public void setLimitOrder() {
        String baseURL = "https://api-dev.techetronventures.com";
        // =========== sign in =============
        System.out.println("STEP: sign in to an account");

        Response responseLogin = given()
                .header("Content-Type", "application/json")
                .body("{\"identifier\": \"1400000000\",\"countryCode\": \"880\",\"identifier_type\": \"phone\",\"password\": \"1111\"}")
                .baseUri(baseURL)
                .when()
                .post("/api/v1/auth/login");

        System.out.println("STEP: verifying status code to be 200");
        responseLogin.then()
                .assertThat().statusCode(200);

        System.out.println("STEP: verifying not null session token");
        String session = responseLogin.getBody().jsonPath().get("data.session");
        Assert.assertNotNull(session);
        System.out.println("Got a session token for sign in: " + session);

        // =========== sign in otp =============
        System.out.println("STEP: Get a session otp for the sign in");
        Response responsesignInOTP = given()
                .body("{\"code\":\"0000\",\"session\":\""+session+"\",\"deviceInfo\":{\"country\":\"BD\",\"deviceModel\":\"M1 chip\",\"deviceName\":\"TVL Macbook-pro\",\"deviceId\":\"1f930014983c5e17d68df9c7f501cc49\",\"platform\":\"ios\"}}")
                .baseUri(baseURL)
                .post("/api/v1/auth/login/otp");

        System.out.println("STEP: verifying status code to be 200");
        responsesignInOTP.then()
                .assertThat().statusCode(200);

        System.out.println("STEP: verifying not null otp token");
        String sessionOTP = responsesignInOTP.getBody().jsonPath().get("data.access");
        Assert.assertNotNull(sessionOTP);
        System.out.println("Got a otp token for the otp request: " + sessionOTP);

        // =========== Get User =============
        System.out.println("STEP: Get user details");
        Response userResponse = given()
                .baseUri(baseURL)
                .queryParam("access", sessionOTP)
                .queryParam("boStatus", "UNKNOWN_BO_StatusType")
                .queryParam("includeMetaInfo", true)
                .queryParam("includePortfolioMeta", true)
                .queryParam("includeKycInfo", true)
                .when()
                .get("/api/v1/auth/user");

        System.out.println("STEP: verifying status code to be 200");
        userResponse
                .then()
                .assertThat().statusCode(200);
        System.out.println("Got User details: " + userResponse.getBody().jsonPath().get("data"));

        int userID = userResponse.getBody().jsonPath().getInt("data.id");
        int clientCode = userResponse.getBody().jsonPath().getInt("data.metaInfo[0].clientCode");
        System.out.println("Got user id: " + userID + "; clientID : " + clientCode);


        // =========== ticker list =============
        System.out.println("STEP: Get ticker list");
        Response responseTickerList = given()
                .header("STOCKX-AUTH", sessionOTP)
                .baseUri(baseURL)
                .queryParam("userId", userID)
                .queryParam("clientCode", clientCode)
                .when()
                .get("/api/v1/portfolio/trigger-list");

        System.out.println("STEP: verifying status code to be 200");
        responseTickerList.then()
                .assertThat().statusCode(200);
        ArrayList tickerList = responseTickerList.getBody().jsonPath().getJsonObject("data");
        System.out.println("Got " + tickerList.size() + " tickers");

        Object randomTickerID = tickerList.get((int)(Math.random() % tickerList.size()));
        System.out.println("Step 4+++++++" + randomTickerID);



        // =========== set limit =============
        given()
                .header("Content-Type", "application/json")
                .header("STOCKX-AUTH", sessionOTP)
                .body("{\"userId\":\"438\",\"clientCode\":\"61257\",\"orderType\":\"1\",\"tickerId\":"+randomTickerID+",\"quantity\":1,\"limitPrice\":43,\"instructionType\":1}")
                .when()
                .post("https://api-dev.techetronventures.com/place-order")
                .then()
                .assertThat().statusCode(200);
        System.out.println("Step 5+++++++" );

        // =========== place order =============
        given()
                .header("Content-Type", "application/json")
                .header("STOCKX-AUTH", "<access_token>") // todo: collect access token
                .body("{\"userId\": \"711\",\"clientCode\": \"62190\",\"orderType\": \"1\",\"tickerId\": 2102 ,\"limitPrice\": 20,\"quantity\": 2,\"instructionType\": 1}")
                .when()
                .post("https://api-dev.techetronventures.com/api/v1/portfolio/place-order")
                .then()
                .assertThat().statusCode(200);
    }
}

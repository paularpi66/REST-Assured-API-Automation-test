//package test.java;

import io.restassured.path.json.JsonPath;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.restassured.response.ResponseBody;
import io.restassured.response.Response;

import java.util.*;

public class LimitOrder {
    @Test (priority = 1)
    public void setLimitOrder() {
        String baseURL = "https://api-dev.techetronventures.com";
        // =========== sign in =============
        System.out.println("STEP 1: sign in to an account");

        Response responseLogin = given()
                .header("Content-Type", "application/json")
                .body("{\"identifier\": \"1400000000\",\"countryCode\": \"880\",\"identifier_type\": \"phone\",\"password\": \"1111\"}")
                .baseUri(baseURL)
                .when()
                .post("/api/v1/auth/login");

        System.out.println("STEP 2: verifying status code to be 200");
        responseLogin.then()
                .assertThat().statusCode(200);

        System.out.println("STEP 3: verifying not null session token");
        String session = responseLogin.getBody().jsonPath().get("data.session");
        Assert.assertNotNull(session);
        System.out.println("Got a session token for sign in: " + session);

        // =========== sign in otp =============
        System.out.println("STEP 4: Get a session otp for the sign in");
        Response responsesignInOTP = given()
                .body("{\"code\":\"0000\",\"session\":\""+session+"\",\"deviceInfo\":{\"country\":\"BD\",\"deviceModel\":\"M1 chip\",\"deviceName\":\"TVL Macbook-pro\",\"deviceId\":\"1f930014983c5e17d68df9c7f501cc49\",\"platform\":\"ios\"}}")
                .baseUri(baseURL)
                .post("/api/v1/auth/login/otp");

        System.out.println("STEP 5: verifying status code to be 200");
        responsesignInOTP.then()
                .assertThat().statusCode(200);

        System.out.println("STEP 6: verifying not null otp token");
        String sessionOTP = responsesignInOTP.getBody().jsonPath().get("data.access");
        Assert.assertNotNull(sessionOTP);
        System.out.println("Got a otp token for the otp request: " + sessionOTP);

        // =========== Get User =============
        System.out.println("STEP 7: Get user details");
        Response userResponse = given()
                .baseUri(baseURL)
                .queryParam("access", sessionOTP)
                .queryParam("boStatus", "UNKNOWN_BO_StatusType")
                .queryParam("includeMetaInfo", true)
                .queryParam("includePortfolioMeta", true)
                .queryParam("includeKycInfo", true)
                .when()
                .get("/api/v1/auth/user");

        System.out.println("STEP 8: verifying status code to be 200");
        userResponse
                .then()
                .assertThat().statusCode(200);
        System.out.println("Got User details: " + userResponse.getBody().jsonPath().get("data"));

        int userID = userResponse.getBody().jsonPath().getInt("data.id");
        int clientCode = userResponse.getBody().jsonPath().getInt("data.metaInfo[0].clientCode");
        System.out.println("Got user id: " + userID + "; clientID : " + clientCode);


        // =========== ticker status =============
        System.out.println("STEP 9: Get ticker list");
        Response responseTickerList = given()
//                .header("STOCKX-AUTH", sessionOTP)
                .baseUri(baseURL)
//                .queryParam("userId", userID)
//                .queryParam("clientCode", clientCode)
                .when()
                .get("/api/v1/public/bazar/mdf/all-ticker-status");

        System.out.println("STEP 10: verifying status code to be 200");
        responseTickerList.then()
                .assertThat().statusCode(200)
                .assertThat().body("data.allTickerStatus.size()", greaterThan(0));

        int len = responseTickerList.getBody().jsonPath().getInt("data.allTickerStatus.size()");
        System.out.println("STEP 10:  list size" + len);

        Random rand = new Random();
        int randIdx = rand.nextInt(len);
        System.out.println("STEP 11: rand Index" + randIdx);
        JsonPath jp = responseTickerList.getBody().jsonPath();
        String randTickerPath = "data.allTickerStatus["+randIdx+"]";
        int randomTickerID = jp.getInt(randTickerPath+".tickerID");
        double maxPrice = jp.getDouble(randTickerPath + ".maxPrice");
        double minPrice = jp.getDouble(randTickerPath + ".minPrice");
        double LimitPrice = rand.nextDouble(minPrice, maxPrice);
        System.out.println("STEP 12: Max and Min Price " + randomTickerID + ": ["+ minPrice + ", " + maxPrice + "] " );
        System.out.println("STEP 13: Random Limit " + LimitPrice);
        long roundedLimitPrice = Math.round(LimitPrice);
        System.out.println("STEP 14: Limit Price (Rounded): " + roundedLimitPrice);



        // =========== place order =============
        given()
                .header("Content-Type", "application/json")
                //.header("STOCKX-AUTH", "<access_token>") // todo: collect access token
                .body("{\"userId\": "+userID+",\"clientCode\": "+clientCode+"\",\"orderType\": \"1\",\"tickerId\": "+randomTickerID+" ,\"limitPrice\": "+roundedLimitPrice+",\"quantity\": 2,\"instructionType\": 1}")
                .baseUri(baseURL)
                .when()
                .post("/api/v1/portfolio/place-order")
                .then()
                .assertThat().statusCode(200);
    }
}
//package test.java;

import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import io.restassured.response.ResponseBody;
import io.restassured.response.Response;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;


public class LimitOrder {
    @Test (priority = 1)
    public void setLimitOrder() {
        // =========== sign in =============
        Response responseLogin = given()
                .header("Content-Type", "application/json")
                .body("{\"identifier\": \"1400000000\",\"countryCode\": \"880\",\"identifier_type\": \"phone\",\"password\": \"1111\"}")
                .when()
                .post("https://api-dev.techetronventures.com/api/v1/auth/login");

        responseLogin.then()
                .assertThat().statusCode(200);

        String session = responseLogin.getBody().jsonPath().get("data.session");
        Assert.assertNotNull(session);
        System.out.println("Step 1++++ " + session);

        // =========== sign in otp =============

        Response responsesignInOTP = given()
                //.header() empty header
                .body("{\"code\":\"0000\",\"session\":\""+session+"\",\"deviceInfo\":{\"country\":\"BD\",\"deviceModel\":\"M1 chip\",\"deviceName\":\"TVL Macbook-pro\",\"deviceId\":\"1f930014983c5e17d68df9c7f501cc49\",\"platform\":\"ios\"}}")
                .post("https://api-dev.techetronventures.com/api/v1/auth/login/otp"); // 0000 = otp

        responsesignInOTP.then()
                .assertThat().statusCode(200);

        String sessionOTP = responsesignInOTP.getBody().jsonPath().get("data.access");
        System.out.println("Step 2++++ " + sessionOTP);

        // =========== Get User =============

        given()
                //.header() header empty
                .when()
                .get("https://api-dev.techetronventures.com/api/v1/auth/user?access="+sessionOTP+"&boStatus=UNKNOWN_BO_StatusType&includeMetaInfo=true&includePortfolioMeta=true&includeKycInfo=true")
                .then()
                .assertThat().statusCode(200);
        System.out.println("Step 3+++++++");


        // =========== ticker list =============
        String Access_Token = sessionOTP;
        Response responseTickerList = given()
                .header("STOCKX-AUTH", Access_Token)
                //.param("userId", 710)
                //.param("clientCode", 62185)
                .when()
                .get("https://api-dev.techetronventures.com/api/v1/portfolio/trigger-list?userId=710&clientCode=62185");
        responseTickerList.then()
                .assertThat().statusCode(200);
        Integer randomTickerID = Integer.valueOf(responseTickerList.getBody().jsonPath().get("data[0].tickerId"));
        System.out.println("Step 4+++++++" + randomTickerID);



        // =========== set limit =============
        given()
                .header("Content-Type", "application/json")
                .header("STOCKX-AUTH", Access_Token)
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

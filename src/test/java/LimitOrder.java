//package test.java;

import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import io.restassured.response.ResponseBody;
import io.restassured.response.Response;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;


public class LimitOrder {
    @Test
    public void setLimitOrder() {
        // =========== log in =============
        Response responseLogin = given()
                .header("Content-Type", "application/json")
                .body("{\"identifier\": \"1900000000\",\"countryCode\": \"880\",\"identifier_type\": \"phone\",\"password\": \"0000\"}")
                .when()
                .post("https://api-dev.techetronventures.com/api/v1/auth/login");

        responseLogin.then()
                .assertThat().statusCode(200);

        String session = responseLogin.getBody().jsonPath().get("data.session");
        System.out.println("++++ " + session);

        // =========== ticker list =============
        given()
                .header("STOCKX-AUTH", "<access_token>") // todo: collect access token
                .param("userId", 711)
                .param("clientCode", 62190)
                .when()
                .get("https://api-dev.techetronventures.com/api/v1/portfolio/trigger-list")
                .then()
                .assertThat().statusCode(200);

        // =========== set limit =============
        given()
                .header("Content-Type", "application/json")
                .header("STOCKX-AUTH", "<access_token>") // todo: collect access token
                .body("{\"identifier\": \"1900000000}")
                .when()
                .post("https://api-dev.techetronventures.com/api/v1/auth/login")
                .then()
                .assertThat().statusCode(200);

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

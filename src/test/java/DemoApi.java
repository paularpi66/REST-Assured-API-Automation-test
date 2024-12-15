//package test.java;

import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

public class DemoApi {
    @Test
    public void getPetsByStatus() {
        given()
                .header("accept", "application/json")
                .when()
                .get("https://petstore3.swagger.io/api/v3/pet/findByStatus?status=available")
                .then()
                .assertThat().statusCode(200);
    }

    @Test
    public void createPet() {
        String json = "{ \"id\": \"10\", \"name\": \"Tail\", \"category\": {\"id\": \"1\", \"name\": \"Dogs\"} }";
        given()
                .body(json)
                .header("Content-Type", "application/json")
                .when()
                .post("https://petstore3.swagger.io/api/v3/pet")
                .then()
                .assertThat().statusCode(200)
                .assertThat().body("id", equalTo(10))
                .assertThat().body("name", equalTo("Tail"))
                .assertThat().body("category.id", equalTo(1))
                .assertThat().body("category.name", equalTo("Dogs"));
    }

    @Test
    public void deletePet() {
        given()
                .pathParam("petId", "10")
                .header("Accept", "*/*")
                .when()
                .delete("https://petstore3.swagger.io/api/v3/pet/{petId}")
                .then()
                .assertThat().statusCode(200)
                .assertThat().body(equalTo("Pet deleted"));
    }

    @Test
    public void login() {
        given()
                .header("Content-Type", "application/json")
//                .body("""
//                        {
//                            "username": "emilys",
//                            "password": "emilyspass",
//                            "expiresInMins": 30,
//                          }
//                        """)
                .body("{\"username\": \"emilys\",\"password\": \"emilyspass\",\"expiresInMins\": 30}")
                .when()
                .post("https://dummyjson.com/auth/login")
                .then()
                .assertThat().statusCode(200);

        System.out.println("+++++++++++++++++++++++++");
        boolean flag = true;
        if (flag) {
            given()
                    .header("accept", "application/json")
                    .when()
                    .get("https://petstore3.swagger.io/api/v3/pet/findByStatus?status=available")
                    .then()
                    .assertThat().statusCode(200);
        }
    }
}

/*
Написать 1 позитивный и 1 негативный тест на каждый из эндпоинтов к сервису https://reqres.in/:  
POST /register  
PUT /users/{id}  
DELETE /users/{id}  
GET /users/{id}

Проверить бизнес-логику, статус код и модель ответа.

Для тестов настроить автоматический прогон и формирование отчетов на удаленной машине.
*/
package dz4_;

import org.junit.Assert;
import org.junit.Test;

import dz4_.api.Register;
import dz4_.api.Specifications;
import dz4_.api.SuccessReg;
import dz4_.api.UnsuccessReg;
import dz4_.api.UserData;
import dz4_.api.UserUpdateDateRequest;
import dz4_.api.UserUpdateRequest;
import dz4_.api.UserUpdateResponse;
import io.restassured.RestAssured;

import static io.restassured.RestAssured.given;

import java.time.Clock;

public class AppTest {
        private final static String BASE_URL = "https://reqres.in";

        /**
         * GET, получение пользователя и проверка того, что название файла его аватара
         * содержит его id
         */
        @Test
        public void checkGetUser() {
                Specifications.installSpecification(Specifications.requestSpec(BASE_URL),
                                Specifications.responseSpecOK200());
                UserData user = given()
                                .when()
                                .get("/api/users/2")
                                .then().log().all()
                                .extract().body().jsonPath().getObject("data", UserData.class);

                // Проверка модели ответа
                Assert.assertNotNull(user.getId());
                Assert.assertNotNull(user.getEmail());
                Assert.assertNotNull(user.getFirst_name());
                Assert.assertNotNull(user.getLast_name());
                Assert.assertNotNull(user.getAvatar());

                // Проверка бизнес-модели
                Assert.assertTrue(user.getAvatar().contains(user.getId().toString()));
        }

        /**
         * GET негативный, проверка, что пользователь с id 9999 не существует
         */
        @Test
        public void checkNonExistentUserId() {
                Specifications.installSpecification(Specifications.requestSpec(BASE_URL),
                                Specifications.responseSpecNOTFOUND404());

                RestAssured.given()
                                .when()
                                .get("/users/9999")
                                .then()
                                .extract()
                                .response();

        }

        /**
         * POST позитивный, проверка успешной регистрации пользователя
         */
        @Test
        public void checkRegistration() {
                Specifications.installSpecification(Specifications.requestSpec(BASE_URL),
                                Specifications.responseSpecOK200());
                Integer id = 4;
                String token = "QpwL5tke4Pnpja7X4";
                Register user = new Register("eve.holt@reqres.in", "pistol");
                SuccessReg successReg = given()
                                .body(user)
                                .when()
                                .post("api/register")
                                .then().log().all()
                                .extract().as(SuccessReg.class);

                Assert.assertNotNull(successReg.getId());
                Assert.assertNotNull(successReg.getToken());

                Assert.assertEquals(id, successReg.getId());
                Assert.assertEquals(token, successReg.getToken());

        }

        /**
         * POST негативный, проверка регистрации пользователя с пустым паролем
         */
        @Test
        public void unsuccessRegistration() {
                Specifications.installSpecification(Specifications.requestSpec(BASE_URL),
                                Specifications.responseSpecBADREQUEST400());
                Register user = new Register("eve.holt@reqres.in", "");
                UnsuccessReg unsuccessReg = given()
                                .body(user)
                                .when()
                                .post("api/register")
                                .then().log().all()
                                .extract().as(UnsuccessReg.class);

                Assert.assertNotNull(unsuccessReg.getError());
                Assert.assertEquals("Missing password", unsuccessReg.getError());

        }

        /**
         * DELETE позитивный, удаление пользователя
         */
        @Test
        public void deleteUser() {
                Specifications.installSpecification(Specifications.requestSpec(BASE_URL),
                                Specifications.responseSpecUnique(204));

                given()
                                .when()
                                .delete("api/users/2")
                                .then().log().all();
        }

        /**
         * DELETE негативный, удаление пользователя по несуществующему id
         */
        @Test
        public void deleteNonExistentUser() {
                Specifications.installSpecification(Specifications.requestSpec(BASE_URL),
                                Specifications.responseSpecUnique(204));
                given()
                                .when()
                                .delete("api/users/9999")
                                .then().log().all();
        }

        /**
         * PUT позитивный, обновление данных пользователя, сверка по времени
         */
        @Test
        public void updateUserData() {
                Specifications.installSpecification(Specifications.requestSpec(BASE_URL),
                                Specifications.responseSpecOK200());
                UserUpdateRequest requestData = new UserUpdateRequest("morpheus", "zion resident");
                UserUpdateResponse response = given().body(requestData).when().put("api/users/2").then().extract()
                                .as(UserUpdateResponse.class);

                String regex = "(.{5})$";
                String currentTimeRegex = "(.{11})$";

                String currentTime = Clock.systemUTC().instant().toString().replaceAll(currentTimeRegex, "");

                Assert.assertNotNull(response.getJob());
                Assert.assertNotNull(response.getName());
                Assert.assertNotNull(response.getUpdatedAt());

                Assert.assertEquals(currentTime, response.getUpdatedAt().replaceAll(regex, ""));
        }

        /**
         * PUT негативный, обновление данных пользователя с указанием времени обновления
         * пользователя в самом запросе
         */
        @Test
        public void updateUserDataId() {
                Specifications.installSpecification(Specifications.requestSpec(BASE_URL),
                                Specifications.responseSpecOK200());
                UserUpdateDateRequest requestData = new UserUpdateDateRequest("morpheus", "zion resident",
                                "2020-01-01T00:00:00.000Z");
                UserUpdateResponse response = given().body(requestData).when().put("api/users/2").then().extract()
                                .as(UserUpdateResponse.class);

                String regex = "(.{5})$";
                String currentTimeRegex = "(.{11})$";

                String currentTime = Clock.systemUTC().instant().toString().replaceAll(currentTimeRegex, "");

                Assert.assertNotNull(response.getJob());
                Assert.assertNotNull(response.getName());
                Assert.assertNotNull(response.getUpdatedAt());

                Assert.assertEquals(currentTime, response.getUpdatedAt().replaceAll(regex, ""));
        }

}

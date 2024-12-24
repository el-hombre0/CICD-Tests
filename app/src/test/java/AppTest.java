/*
Написать 1 позитивный и 1 негативный тест на каждый из эндпоинтов к сервису https://reqres.in/:  
POST /register  
PUT /users/{id}  
DELETE /users/{id}  
GET /users/{id}

Проверить бизнес-логику, статус код и модель ответа.

Для тестов настроить автоматический прогон и формирование отчетов на удаленной машине.
*/


import org.junit.jupiter.api.*;

import io.restassured.RestAssured;
import ru.evendot.api.Register;
import ru.evendot.api.Specifications;
import ru.evendot.api.SuccessReg;
import ru.evendot.api.UnsuccessReg;
import ru.evendot.api.UserData;
import ru.evendot.api.UserUpdateDateRequest;
import ru.evendot.api.UserUpdateRequest;
import ru.evendot.api.UserUpdateResponse;

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
                Assertions.assertNotNull(user.getId());
                Assertions.assertNotNull(user.getEmail());
                Assertions.assertNotNull(user.getFirst_name());
                Assertions.assertNotNull(user.getLast_name());
                Assertions.assertNotNull(user.getAvatar());

                // Проверка бизнес-модели
                Assertions.assertTrue(user.getAvatar().contains(user.getId().toString()));
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

                Assertions.assertNotNull(successReg.getId());
                Assertions.assertNotNull(successReg.getToken());

                Assertions.assertEquals(id, successReg.getId());
                Assertions.assertEquals(token, successReg.getToken());

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

                Assertions.assertNotNull(unsuccessReg.getError());
                Assertions.assertEquals("Missing password", unsuccessReg.getError());

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

                Assertions.assertNotNull(response.getJob());
                Assertions.assertNotNull(response.getName());
                Assertions.assertNotNull(response.getUpdatedAt());

                Assertions.assertEquals(currentTime, response.getUpdatedAt().replaceAll(regex, ""));
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

                Assertions.assertNotNull(response.getJob());
                Assertions.assertNotNull(response.getName());
                Assertions.assertNotNull(response.getUpdatedAt());

                Assertions.assertEquals(currentTime, response.getUpdatedAt().replaceAll(regex, ""));
        }

}

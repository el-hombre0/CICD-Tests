package ru.evendot.api;

/**
 * Класс, описывающий данные ответа при обновлении информации о пользователе
 */
public class UserUpdateRequest {
    private String name;
    private String job;

    public UserUpdateRequest(String name, String job) {
        this.name = name;
        this.job = job;
    }

    public UserUpdateRequest() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

}

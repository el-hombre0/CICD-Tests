package dz4_.api;

/**
 * Класс, описывающий данные ответа при обновлении информации о пользователе
 */
public class UserUpdateResponse extends UserUpdateRequest {
    private String updatedAt;

    public UserUpdateResponse(String name, String job, String updatedAt) {
        super(name, job);
        this.updatedAt = updatedAt;
    }

    public UserUpdateResponse(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UserUpdateResponse() {
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}

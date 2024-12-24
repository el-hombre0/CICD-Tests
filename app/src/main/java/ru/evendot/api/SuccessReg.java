package ru.evendot.api;

/**
 * Данные ответа успешной регистрации
 */
public class SuccessReg {
    private Integer id;
    private String token;

    public SuccessReg(Integer id, String token) {
        this.id = id;
        this.token = token;
    }

    public SuccessReg(){
        
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}

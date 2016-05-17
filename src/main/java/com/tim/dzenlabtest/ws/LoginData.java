package com.tim.dzenlabtest.ws;

import javax.json.JsonObject;

/**
 * Created by tim1 on 14.05.16.
 */
public class LoginData {
    private final String email;
    private final String pwd;

    public LoginData(String email, String pwd) {
        this.email = email;
        this.pwd = pwd;
    }

    public String getEmail() {
        return email;
    }

    public String getPwd() {
        return pwd;
    }

    @Override
    public String toString() {
        return "LoginData{" +
                "email='" + email + '\'' +
                ", pwd='" + pwd + '\'' +
                '}';
    }

    public static class Coder implements DataCoder<LoginData> {

        @Override
        public JsonObject encode(LoginData message) {
            return null;
        }

        @Override
        public LoginData decode(JsonObject data) {
            return new LoginData(data.getString("email"), data.getString("password"));
        }
    }
}

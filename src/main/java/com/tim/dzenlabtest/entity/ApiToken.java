package com.tim.dzenlabtest.entity;

import javax.persistence.Embeddable;
import java.util.Date;

/**
 * Created by Mikhail Titov on 15.05.16.
 */
@Embeddable
public class ApiToken {
    private String token;
    private Date expirationDate;

    public ApiToken() { }

    public ApiToken(String token, Date expirationDate) {
        this.token = token;
        this.expirationDate = expirationDate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (o==this)
//            return true;
//        if (o instanceof ApiToken) {
//            ApiToken other = (ApiToken) o;
//            return Object
//        }
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApiToken apiToken = (ApiToken) o;

        if (token != null ? !token.equals(apiToken.token) : apiToken.token != null) return false;
        return expirationDate != null ? expirationDate.equals(apiToken.expirationDate) : apiToken.expirationDate == null;

    }

    @Override
    public int hashCode() {
        int result = token != null ? token.hashCode() : 0;
        result = 31 * result + (expirationDate != null ? expirationDate.hashCode() : 0);
        return result;
    }
}

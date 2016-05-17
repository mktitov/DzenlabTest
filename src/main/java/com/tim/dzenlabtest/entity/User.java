/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tim.dzenlabtest.entity;

import java.io.Serializable;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.persistence.*;

/**
 *
 * @author Mikhail Titov
 */
@Table(name = "USERS", indexes = {@Index(columnList = "token", unique = true)})
@Entity
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String pwd;
    @Embedded
    private ApiToken token;

    public User() {
    }

    public User(String email, String pwd, ApiToken token) {
        this.email = email;
        this.pwd = pwd;
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ApiToken getToken() {
        return token;
    }

    public void setToken(ApiToken token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.tim.dzenlabtest.User[ id=" + id + " ]";
    }

    public JsonObject toJson() {
        JsonObjectBuilder builder =  Json.createObjectBuilder()
                .add("id", id)
                .add("email", email)
                .add("pwd", pwd);
        if (token!=null) {
            builder
                .add("token", token.getToken())
                .add("tokenExpirationDate", token.getExpirationDate().toString());
        } else
            builder.addNull("token").addNull("tokenExpirationDate");
        return builder.build();
    }
}

package com.tim.dzenlabtest.entity;

import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.*;

/**
 * Created by tim1 on 15.05.16.
 */
@Entity
public class TokenAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User user;

    @Embedded
    private ApiToken token;

    public TokenAudit() {
    }

    public TokenAudit(User user, ApiToken token) {
        this.user = user;
        this.token = token;
    }

    public ApiToken getToken() {
        return token;
    }

    public void setToken(ApiToken token) {
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public JsonObject toJson() {
        return Json.createObjectBuilder()
                .add("id", id)
                .add("userId", user.getId())
                .add("userEmail", user.getEmail())
                .add("token", token.getToken())
                .add("tokenExpirationDate", token.getExpirationDate().toString())
                .build();
    }
}

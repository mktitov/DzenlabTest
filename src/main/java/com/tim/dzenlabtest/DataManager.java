
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tim.dzenlabtest;

import com.tim.dzenlabtest.entity.ApiToken;
import com.tim.dzenlabtest.entity.TokenAudit;
import com.tim.dzenlabtest.entity.User;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

/**
 *
 * @author Mikhail Titov
 */
@Stateless
public class DataManager {
    public final static long TOKEN_EXPIRATION_TIMEOUT = 300_000; //in ms

    @PersistenceContext(unitName = "DzenlabDS")
    private EntityManager entityManager;

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public List<User> listUsers() {
        return entityManager.createQuery("select u from User u").getResultList();
    }

    public List<TokenAudit> listTokenAudit() {
        return entityManager.createQuery("select t from TokenAudit t ").getResultList();
    }
    
    public void addOrUpdateUser(User user) {
        entityManager.persist(user);
    }

    public <T> T withTx(Supplier<T> block) {
        return block.get();
    }

    public User getUser(String email, String pwd) {
        try {
            return (User) entityManager.createQuery("select u from User u where u.email=:email and u.pwd = :pwd") //TODO refactor to using the named query
                    .setParameter("email", email)
                    .setParameter("pwd", pwd)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void auditTokenCreation(User user, ApiToken token) {
        entityManager.persist(new TokenAudit(user, token));
    }

    public static String generateUniqId() {
        return UUID.randomUUID().toString();
    }

    public ApiToken generateToken() {
        return new ApiToken(generateUniqId(), new Date(System.currentTimeMillis()+TOKEN_EXPIRATION_TIMEOUT));
    }

    public User getUserByApiToken(String apiToken) {
        try {
            return (User) entityManager.createQuery(
                    "select u from User u " +
                    "where u.token.token = :token " +
                    "  and u.token.expirationDate > :ts")
                    .setParameter("token", apiToken)
                    .setParameter("ts", new Timestamp(System.currentTimeMillis()))
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Transactional
    public void createTestUsers() {
        createUserIfNeed(new User("user1@email.ru", "user1", null));
        createUserIfNeed(new User("user2@gmail.com", "user2", null));
    }

    private void createUserIfNeed(User user) {
        long cnt = (long)entityManager.createQuery(
                "select count(*) from User u where u.email=:email").setParameter("email", user.getEmail()).getSingleResult();
        if (cnt==0)
            entityManager.persist(user);
    }
}

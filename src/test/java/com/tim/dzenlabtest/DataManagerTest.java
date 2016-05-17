package com.tim.dzenlabtest;

import com.tim.dzenlabtest.entity.ApiToken;
import com.tim.dzenlabtest.entity.TokenAudit;
import com.tim.dzenlabtest.entity.User;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.Date;
import java.util.List;

/**
 * Created by Mikhail Titov on 15.05.16.
 */
@RunWith(JMockit.class)
public class DataManagerTest extends Assert {
    @Tested DataManager dataManager;
    @Injectable EntityManager em = Persistence.createEntityManagerFactory("DzenlabDS-test").createEntityManager();

    @Test
    public void userNotFoundTest() {
        assertNull(dataManager.getUser("email", "pwd"));
    }

    @Test
    public void userFoundTest() {
        em.getTransaction().begin();
        em.persist(new User("user1", "pwd1", null));
        em.persist(new User("user2", "pwd2", null));
        em.getTransaction().commit();
        User user = dataManager.getUser("user1", "pwd1");
        assertNotNull(user);
        assertEquals("user1", user.getEmail());
        assertEquals("pwd1", user.getPwd());
    }

    @Test
    public void getUserByApiTokenTest() throws InterruptedException {
        assertNull(dataManager.getUserByApiToken("1"));

        em.getTransaction().begin();
        Date expirationDate = new Date(System.currentTimeMillis()+500);
        em.persist(new User("u1", "p1", new ApiToken("1", expirationDate)));
        em.getTransaction().commit();

        User user = dataManager.getUserByApiToken("1");
        assertNotNull(user);
        assertNotNull(user.getToken());
        assertEquals("1", user.getToken().getToken());
        assertEquals(expirationDate, user.getToken().getExpirationDate());
        Thread.sleep(1000); //waiting for expiration
        assertNull(dataManager.getUserByApiToken("1"));
    }

    @Test
    public void auditTokenCreation() {
        em.getTransaction().begin();

        User user = new User("u1", "p1", null);
        dataManager.addOrUpdateUser(user);
        ApiToken token = dataManager.generateToken();
        dataManager.auditTokenCreation(user, token);

        em.getTransaction().commit();

        List<TokenAudit> tokens = em.createQuery("select a from TokenAudit a").getResultList();
        assertNotNull(tokens);
        assertEquals(1, tokens.size());
        assertEquals(token, tokens.get(0).getToken());
        assertEquals(user, tokens.get(0).getUser());
    }
}
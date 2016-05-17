package com.tim.dzenlabtest.ws;

import com.tim.dzenlabtest.DataManager;
import com.tim.dzenlabtest.entity.ApiToken;
import com.tim.dzenlabtest.entity.User;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.websocket.Session;
import java.util.Date;
import java.util.Map;
import java.util.function.Supplier;

import static com.tim.dzenlabtest.ws.ErrorData.*;
import static com.tim.dzenlabtest.ws.Message.*;

/**
 * Created by Mikhail Titov on 14.05.16.
 */
@RunWith(JMockit.class)
public class CustomerChannelTest extends Assert {
    @Tested CustomerChannel channel;
    @Injectable DataManager dataManager;

    @Test
    public void successLoginTest(
            @Mocked final Session session,
            @Mocked final User user,
            @Mocked final ApiToken token,
            @Mocked final Map<String, Object> props
    ) throws Exception {
        new Expectations() {{
            dataManager.withTx(withNotNull()); result = new Delegate() {
                public <T> T withTx(Supplier<T> block) {
                    return block.get();
                }
            };
            session.getUserProperties(); result = props;
            token.getToken(); result = "123";
        }};
        Message request = new Message(MessageType.LOGIN_CUSTOMER, "1", new LoginData("cust1", "cust1_pwd"));
        Message resp = channel.onMessage(request, session);
        assertNotNull(resp);
        assertEquals(MessageType.CUSTOMER_API_TOKEN, resp.getType());
        assertEquals("1", resp.getSequenceId());
        assertSame(token, resp.getData());
        new Verifications(){{
            dataManager.getUser("cust1", "cust1_pwd");
            dataManager.generateToken();
            dataManager.auditTokenCreation(user, token);
            user.setToken(token);
            dataManager.addOrUpdateUser(user);
            props.put("123", token);
        }};
    }

    @Test
    public void unsuccessLoginTest(
            @Mocked final Session session
    ) throws Exception {
        new Expectations(){{
            dataManager.withTx(withNotNull()); result = new Delegate() {
                public <T> T withTx(Supplier<T> block) {
                    return block.get();
                }
            };
            dataManager.getUser("cust1", "cust1_pwd"); result = null;
        }};
        Message request = new Message(MessageType.LOGIN_CUSTOMER, "1", new LoginData("cust1", "cust1_pwd"));
        Message resp = channel.onMessage(request, session);
        assertNotNull(resp);
        assertEquals(MessageType.CUSTOMER_ERROR, resp.getType());
        assertEquals("1", resp.getSequenceId());
        assertTrue(resp.getData() instanceof ErrorData);
        ErrorData error = resp.getData();
        assertEquals(ErrorCode.CUSTOMER_NOT_FOUND, error.getErrorCode());
        assertEquals("Customer not found", error.getErrorDescription());
    }

    @Test
    public void requestWithoutApiToken(
            @Mocked final Session session
    ) throws Exception {
        Message resp = channel.onMessage(new Message(MessageType.ECHO_REQUEST, "1", new EchoData("test")), session);
        checkErrorMessage(resp, ErrorCode.INVALID_API_TOKEN);
    }

    @Test
    public void requestWithUnknownApiToken(
            @Mocked final Session session,
            @Mocked final Map<String, Object> props
    ) throws Exception  {
        new Expectations(){{
            session.getUserProperties(); result = props;
            dataManager.getUserByApiToken("123"); result = null;
        }};
        Message resp = channel.onMessage(new Message(MessageType.ECHO_REQUEST, "1", new EchoData("test"), "123"), session);
        checkErrorMessage(resp, ErrorCode.INVALID_API_TOKEN);
        new Verifications(){{
            props.get("123");
            dataManager.getUserByApiToken("123");
        }};
    }

    @Test
    public void requestWithCachedButExpiredApiToken(
            @Mocked final Session session,
            @Mocked final Map<String, Object> props
    ) throws Exception  {
        new Expectations(){{
            session.getUserProperties(); result = props;
            props.get("123"); result = new ApiToken("123", new Date(System.currentTimeMillis()-1000));
        }};
        Message resp = channel.onMessage(new Message(MessageType.ECHO_REQUEST, "1", new EchoData("test"), "123"), session);
        checkErrorMessage(resp, ErrorCode.INVALID_API_TOKEN);
        new Verifications(){{
            props.remove("123");
        }};
    }

    @Test
    public void requestWithCachedApiToken(
            @Mocked final Session session,
            @Mocked final Map<String, Object> props
    ) throws Exception  {
        new Expectations(){{
            session.getUserProperties(); result = props;
            props.get("123"); result = new ApiToken("123", new Date(System.currentTimeMillis()+1000));
        }};
        Message resp = channel.onMessage(new Message(MessageType.ECHO_REQUEST, "1", new EchoData("test"), "123"), session);
        checkEchoResponse(resp, "1", "test");
    }

    @Test
    public void requestWithApiTokenFromDB(
            @Mocked final Session session,
            @Mocked final Map<String, Object> props
    ) throws Exception  {
        new Expectations(){{
            session.getUserProperties(); result = props;
            props.get("123"); result = new ApiToken("123", new Date(System.currentTimeMillis()+1000));
        }};
        Message resp = channel.onMessage(new Message(MessageType.ECHO_REQUEST, "1", new EchoData("test"), "123"), session);
        checkEchoResponse(resp, "1", "test");
    }

    private void checkErrorMessage(Message message, ErrorCode errorCode) {
        assertNotNull(message);
        assertEquals(MessageType.CUSTOMER_ERROR, message.getType());
        ErrorData error = message.getData();
        assertNotNull(error);
        assertEquals(errorCode, error.getErrorCode());
    }

    private void checkEchoResponse(Message message, String seqId, String echoMessage) {
        assertNotNull(message);
        assertEquals(seqId, message.getSequenceId());
        assertEquals(MessageType.ECHO_RESPONSE, message.getType());
        assertTrue(message.getData() instanceof EchoData);
        assertEquals(echoMessage, ((EchoData)message.getData()).getMessage());
    }
}
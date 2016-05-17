package com.tim.dzenlabtest.ws;

import com.tim.dzenlabtest.DataManager;
import com.tim.dzenlabtest.entity.ApiToken;
import com.tim.dzenlabtest.entity.User;
import com.tim.dzenlabtest.ws.ErrorData.ErrorCode;
import com.tim.dzenlabtest.ws.Message.MessageType;

import javax.inject.Inject;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * Created by Mikhail Titov on 14.05.16.
 */
@ServerEndpoint(value = "/customer-channel", encoders = Message.WsEncoder.class, decoders = Message.WsDecoder.class)
public class CustomerChannel {

    @Inject
    private DataManager dataManager;

    @OnError
    public void onError(Session session, Throwable err) throws Exception {
        err.printStackTrace(System.out);
        Message resp = new Message(MessageType.CUSTOMER_ERROR, DataManager.generateUniqId(), new ErrorData(ErrorCode.INVALID_MESSAGE_FORMAT,  err.getClass().getName()+": "+err.getMessage()));
        session.getBasicRemote().sendText(new Message.WsEncoder().encode(resp).toString());
    }

    @OnMessage
    public Message onMessage(Message request, Session session) throws Exception {
        if (!MessageType.LOGIN_CUSTOMER.equals(request.getType())) {
            //checking correctness of the api token
            if (!checkApiToken(request.getApiToken(), session))
                return new Message(MessageType.CUSTOMER_ERROR, request.getSequenceId(),
                        new ErrorData(ErrorCode.INVALID_API_TOKEN));
        }
        switch (request.getType()) {
            case LOGIN_CUSTOMER:
                return processLogin(request, session);
            case ECHO_REQUEST:
                return new Message(MessageType.ECHO_RESPONSE, request.getSequenceId(), request.getData());
            default:
                return new Message(MessageType.CUSTOMER_ERROR, request.getSequenceId(),
                        new ErrorData(ErrorCode.INVALID_MESSAGE_TYPE));
        }
    }

    private boolean checkApiToken(String apiToken, Session session) {
        //looking up for token in the session user properties
        if (apiToken==null)
            return false;
        ApiToken token = (ApiToken) session.getUserProperties().get(apiToken);
        boolean res = false;
        if (token!=null) {
            if (token.getExpirationDate().getTime() > System.currentTimeMillis())
                res = true;
            else
                session.getUserProperties().remove(apiToken);
        } else {
            //checking token using database manager
            User user = dataManager.getUserByApiToken(apiToken);
            if (user!=null) {
                session.getUserProperties().put(user.getToken().getToken(), user.getToken());
                res =  true;
            }
        }
        return res;
    }

    private Message processLogin(Message message, Session session) throws Exception {
        LoginData loginData = message.getData();
        System.out.println("LOGIN DATA: "+loginData);
        System.out.println("DATA MANAGER: "+dataManager);
        return dataManager.withTx(()->{
            Message resp;
            User user = dataManager.getUser(loginData.getEmail(), loginData.getPwd());
            if (user == null)
                resp = new Message(MessageType.CUSTOMER_ERROR, message.getSequenceId(),
                        new ErrorData(ErrorCode.CUSTOMER_NOT_FOUND));
            else {
                ApiToken token = dataManager.generateToken();
                dataManager.auditTokenCreation(user, token);
                user.setToken(token);
                dataManager.addOrUpdateUser(user);
                session.getUserProperties().put(token.getToken(), token);
                resp = new Message(MessageType.CUSTOMER_API_TOKEN, message.getSequenceId(), token);
            }
            return resp;
        });
    }
}

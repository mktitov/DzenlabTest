package com.tim.dzenlabtest.ws;

import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.*;
import java.io.StringReader;

/**
 * Created by Mikhail Titov on 14.05.16.
 */
public class Message {
    public enum MessageType {
        LOGIN_CUSTOMER(new LoginData.Coder()),
        CUSTOMER_API_TOKEN(new ApiTokenCoder()),
        CUSTOMER_ERROR(new ErrorData.Coder()),
        ECHO_REQUEST(EchoData.Coder.INSTANCE),
        ECHO_RESPONSE(EchoData.Coder.INSTANCE);

        private final DataCoder coder;

        MessageType(DataCoder coder) {
            this.coder = coder;
        }
    };

    private final MessageType type;
    private final String sequenceId;
    private final String apiToken;
    private final Object data;

    public Message(MessageType type, String sequenceId, Object data) {
        this.type = type;
        this.sequenceId = sequenceId;
        this.data = data;
        this.apiToken = null;
    }

    public Message(MessageType type, String sequenceId, Object data, String apiToken) {
        this.type = type;
        this.sequenceId = sequenceId;
        this.apiToken = apiToken;
        this.data = data;
    }

    public MessageType getType() {
        return type;
    }

    public String getSequenceId() {
        return sequenceId;
    }

    public <T> T getData() {
        return (T)data;
    }

    public String getApiToken() {
        return apiToken;
    }

    public static class WsEncoder implements Encoder.Text<Message> {

        @Override
        public String encode(Message message) throws EncodeException {
            return Json.createObjectBuilder()
                    .add("type", message.type.name())
                    .add("sequence_id", message.sequenceId)
                    .add("data", message.type.coder.encode(message.data))
                    .build().toString();
        }

        @Override
        public void init(EndpointConfig config) {
        }

        @Override
        public void destroy() {
        }
    }

    public static class WsDecoder implements Decoder.Text<Message> {

        @Override
        public Message decode(String s) throws DecodeException {
            JsonObject obj = Json.createReader(new StringReader(s)).readObject();
            MessageType type = decodeMessageType(obj);
            return new Message(
                    type,
                    obj.getString("sequence_id"),
                    type.coder.decode(obj.getJsonObject("data")),
                    obj.getString("api_token", null)
                );
        }

        private MessageType decodeMessageType(JsonObject obj) throws DecodeException {
            try {
                return MessageType.valueOf(obj.getString("type"));
            } catch (Throwable e) {
                throw new DecodeException(obj.getString("type"), "Invalid message type");
            }
        }

        @Override
        public boolean willDecode(String s) {
            return true;
        }

        @Override
        public void init(EndpointConfig config) {

        }

        @Override
        public void destroy() {

        }
    }
}

package com.tim.dzenlabtest.ws;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Created by tim1 on 15.05.16.
 */
public class EchoData  {
    private String message;

    public EchoData(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public static class Coder implements DataCoder<EchoData> {
        public final static Coder INSTANCE = new Coder();

        @Override
        public JsonObject encode(EchoData message) {
            return Json.createObjectBuilder().add("message", message.message).build();
        }

        @Override
        public EchoData decode(JsonObject data) {
            return new EchoData(data.getString("message"));
        }
    }
}

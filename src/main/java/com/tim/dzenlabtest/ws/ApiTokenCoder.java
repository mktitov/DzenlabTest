package com.tim.dzenlabtest.ws;

import com.tim.dzenlabtest.entity.ApiToken;

import javax.json.Json;
import javax.json.JsonObject;
import java.text.SimpleDateFormat;

/**
 * Created by Mikhail Titov on 15.05.16.
 */
public class ApiTokenCoder implements DataCoder<ApiToken> {

        @Override
        public JsonObject encode(ApiToken message) {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            return Json.createObjectBuilder()
                    .add("api_token", message.getToken())
                    .add("api_token_expiration_date", fmt.format(message.getExpirationDate()))
                    .build();
        }

        @Override
        public ApiToken decode(JsonObject data) {
            return null; //в данной реализации нет необходимости
        }
}

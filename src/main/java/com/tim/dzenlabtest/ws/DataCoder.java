package com.tim.dzenlabtest.ws;

import javax.json.JsonObject;

/**
 * Created by tim1 on 14.05.16.
 */
public interface DataCoder<T> {
    public JsonObject encode(T message);
    public T decode(JsonObject data);
}

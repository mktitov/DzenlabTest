package com.tim.dzenlabtest.ws;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Created by Mikhail Titov on 14.05.16.
 */
public class ErrorData {
    public enum ErrorCode {
        CUSTOMER_NOT_FOUND("customer.notFound", "Customer not found"),
        INVALID_API_TOKEN("customer.invalidApiToken", "Invalid or expired api token"),
        INVALID_MESSAGE_TYPE("customer.invalidMessageType", "Not supported message type"),
        INVALID_MESSAGE_FORMAT("customer.invalidMessageFormat", "Invalid message format");


        private final String message;
        private final String description;

        ErrorCode(String message, String description) {
            this.message = message;
            this.description = description;
        }

        public String getCode() {
            return message;
        }

        public String getDescription() {
            return description;
        }
    }
    private final ErrorCode errorCode;
    private final String errorDescription;

    public ErrorData(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorDescription = errorCode.description;
    }

    public ErrorData(ErrorCode errorCode, String errorDescription) {
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public static class Coder implements DataCoder<ErrorData> {

        @Override
        public JsonObject encode(ErrorData message) {
            return Json.createObjectBuilder()
                    .add("error_code", message.errorCode.getCode())
                    .add("error_description", message.errorDescription).build();
        }

        @Override
        public ErrorData decode(JsonObject data) {
            return null; //нет необходимости в данной реализации
        }
    }
}

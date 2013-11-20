package com.utopia.bttendance.model.json;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.utopia.bttendance.BTDebug;

/**
 * Created by TheFinestArtist on 2013. 11. 20..
 */
public class ErrorJson {
    public String error;

    @Override
    public String toString() {
        BTDebug.LogError(error);
        ValidationErrorJson valError = new Gson().fromJson(error, ValidationErrorJson.class);
        if (valError.ValidationError != null)
            return valError.toString();
        return error;
    }

    public class ValidationErrorJson {
        public JsonObject ValidationError;

        public String toString() {
            String message = "ValidationError : " + ValidationError.entrySet().iterator().next().getKey();
            return message;
        }
    }
}

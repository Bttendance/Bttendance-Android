package com.utopia.bttendance.model.json;

import com.google.gson.Gson;

/**
 * Created by TheFinestArtist on 2013. 12. 20..
 */
public class BTJson {

    public int id;

    public String toJson() {
        return new Gson().toJson(this);
    }

    public JsonType getType() {
        if (this instanceof CourseJson)
            return JsonType.Course;
        else if (this instanceof ErrorsJson)
            return JsonType.Errors;
        else if (this instanceof PostJson)
            return JsonType.Post;
        else if (this instanceof SchoolJson)
            return JsonType.School;
        else if (this instanceof UserJson)
            return JsonType.User;
        else if (this instanceof ValidationJson)
            return JsonType.Validation;

        return JsonType.Null;
    }

    public enum JsonType {Course, Errors, Post, School, User, Validation, Null}
}

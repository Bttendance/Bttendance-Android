package com.bttendance.service.request;

import com.bttendance.model.json.PreferencesJson;

/**
 * Created by TheFinestArtist on 12/26/14.
 */
public class PreferencesPutRequest {

    public Preferences preferences;

    public class Preferences {
        public boolean clicker;
        public boolean attendance;
        public boolean curious;
        public boolean following;
        public boolean notice;
    }

    public PreferencesPutRequest(PreferencesJson preferencesJson) {
        this.preferences = new Preferences();
        this.preferences.clicker = preferencesJson.clicker;
        this.preferences.attendance = preferencesJson.attendance;
        this.preferences.curious = preferencesJson.curious;
        this.preferences.following = preferencesJson.following;
        this.preferences.notice = preferencesJson.notice;
    }
}

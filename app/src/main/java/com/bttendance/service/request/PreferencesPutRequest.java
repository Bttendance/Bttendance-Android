package com.bttendance.service.request;

import com.bttendance.model.json.PreferencesJson;

/**
 * Created by TheFinestArtist on 12/26/14.
 */
public class PreferencesPutRequest {

    public Preferences preferences;

    public class Preferences {
        public String clicker;
        public String attendance;
        public String curious;
        public String following;
        public String notice;
    }

    public PreferencesPutRequest(PreferencesJson preferencesJson) {
        this.preferences = new Preferences();
        this.preferences.clicker = Boolean.toString(preferencesJson.clicker);
        this.preferences.attendance = Boolean.toString(preferencesJson.attendance);
        this.preferences.curious = Boolean.toString(preferencesJson.curious);
        this.preferences.following = Boolean.toString(preferencesJson.following);
        this.preferences.notice = Boolean.toString(preferencesJson.notice);
    }
}

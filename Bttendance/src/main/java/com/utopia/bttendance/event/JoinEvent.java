package com.utopia.bttendance.event;

import com.utopia.bttendance.model.json.BTJson;

/**
 * Created by TheFinestArtist on 2013. 12. 20..
 */
public class JoinEvent {

    private BTJson mJson;

    public JoinEvent(BTJson json) {
        mJson = json;
    }

    public BTJson getJson() {
        return mJson;
    }
}

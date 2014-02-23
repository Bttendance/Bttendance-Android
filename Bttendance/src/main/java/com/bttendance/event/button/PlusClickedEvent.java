package com.bttendance.event.button;

import com.bttendance.model.json.BTJson;

/**
 * Created by TheFinestArtist on 2013. 12. 20..
 */
public class PlusClickedEvent {

    private BTJson mJson;
    private int mId;

    public PlusClickedEvent(BTJson json, int id) {
        mJson = json;
        mId = id;
    }

    public BTJson getJson() {
        return mJson;
    }

    public int getId() {
        return mId;
    }
}

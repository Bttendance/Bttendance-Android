package com.bttendance.model.json;

import com.bttendance.helper.DateHelper;

import java.util.Date;

/**
 * Created by TheFinestArtist on 2013. 12. 20..
 */
public class BTJson extends BTJsonSimple {

    public String createdAt;
    public String updatedAt;

    public Date getCreatedDate() {
        return DateHelper.getDate(createdAt);
    }

    public Date getUpdatedDate() {
        return DateHelper.getDate(updatedAt);
    }
}
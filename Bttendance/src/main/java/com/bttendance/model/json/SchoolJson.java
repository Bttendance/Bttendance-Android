package com.bttendance.model.json;

import com.bttendance.R;
import com.bttendance.adapter.kit.InstantText;

/**
 * Created by TheFinestArtist on 2013. 11. 19..
 */
public class SchoolJson extends BTJson {

    public String name;
    public String logo_image;
    public String website;
    public int[] courses;
    public int[] professors;
    public int[] students;
    public int[] serials;

    @InstantText(viewId = R.id.name)
    public String getName() {
        return name;
    }

    @InstantText(viewId = R.id.website)
    public String getWebsite() {
        return website;
    }

}

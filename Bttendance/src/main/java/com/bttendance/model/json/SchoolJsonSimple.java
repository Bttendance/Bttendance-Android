package com.bttendance.model.json;

import com.bttendance.R;
import com.bttendance.adapter.kit.InstantText;

/**
 * Created by TheFinestArtist on 2014. 5. 11..
 */

public class SchoolJsonSimple extends BTJsonSimple {

    public String name;
    public String logo_image;
    public String website;
    public String type;

    @InstantText(viewId = R.id.name)
    public String getName() {
        return name;
    }

    @InstantText(viewId = R.id.website)
    public String getWebsite() {
        return website;
    }
}

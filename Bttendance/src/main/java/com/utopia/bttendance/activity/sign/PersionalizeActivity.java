package com.utopia.bttendance.activity.sign;

import android.os.Bundle;

import com.utopia.bttendance.activity.BTActivity;

/**
 * Created by TheFinestArtist on 2013. 11. 20..
 */
public class PersionalizeActivity extends BTActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStack.clear(this);
    }
}

package com.utopia.bttendance.activity;

import android.os.Bundle;

/**
 * Created by TheFinestArtist on 2013. 11. 20..
 */
public class StudentActivity extends BTActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStack.clear(this);
    }
}

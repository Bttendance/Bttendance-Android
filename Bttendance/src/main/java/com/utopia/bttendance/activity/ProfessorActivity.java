package com.utopia.bttendance.activity;

import android.os.Bundle;

import com.utopia.bttendance.model.BTPreference;
import com.utopia.bttendance.model.json.UserJson;
import com.utopia.bttendance.view.BeautiToast;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 11. 20..
 */
public class ProfessorActivity extends BTActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStack.clear(this);
        UserJson user = BTPreference.getUser(getApplicationContext());
    }
}

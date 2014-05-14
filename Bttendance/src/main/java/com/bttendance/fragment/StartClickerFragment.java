package com.bttendance.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.bttendance.R;
import com.bttendance.helper.KeyboardHelper;
import com.bttendance.model.BTPreference;
import com.bttendance.model.json.CourseJsonHelper;
import com.bttendance.model.json.PostJson;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2014. 1. 27..
 */
public class StartClickerFragment extends BTFragment {

    private CourseJsonHelper mCourseHelper;
    private EditText mMessage;

    public StartClickerFragment(int courseID) {
        mCourseHelper = new CourseJsonHelper(BTPreference.getUser(getActivity()), courseID);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        KeyboardHelper.hide(getActivity(), mMessage);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start_clicker, container, false);
        mMessage = (EditText) view.findViewById(R.id.message);
        KeyboardHelper.show(getActivity(), mMessage);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setTitle(getString(R.string.start_clicker));
        actionBar.setDisplayHomeAsUpEnabled(true);
        inflater.inflate(R.menu.start_clicker_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.abs__home:
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.action_start:
                if (mMessage != null && mMessage.getText().toString().length() > 0)
                    getBTService().postStartClicker(mCourseHelper.getID(), mMessage.getText().toString(), 4, new Callback<PostJson>() {
                        @Override
                        public void success(PostJson postJson, Response response) {
                            StartClickerFragment.this.getActivity().onBackPressed();
                        }

                        @Override
                        public void failure(RetrofitError retrofitError) {
                            StartClickerFragment.this.getActivity().onBackPressed();
                        }
                    });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
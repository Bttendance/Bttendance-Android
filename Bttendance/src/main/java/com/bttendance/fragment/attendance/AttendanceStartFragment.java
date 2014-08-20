package com.bttendance.fragment.attendance;

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
import com.bttendance.fragment.BTFragment;
import com.bttendance.helper.KeyboardHelper;
import com.bttendance.model.json.PostJson;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2014. 1. 27..
 */
public class AttendanceStartFragment extends BTFragment {

    private int mCourseID;
    private EditText mMessage;

    public AttendanceStartFragment(int courseID) {
        mCourseID = courseID;
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
        View view = inflater.inflate(R.layout.fragment_attendance_start, container, false);
        mMessage = (EditText) view.findViewById(R.id.message_edit);
        KeyboardHelper.show(getActivity(), mMessage);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (getSherlockActivity() == null)
            return;

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(getString(R.string.start_clicker));
        inflater.inflate(R.menu.start_clicker_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.abs__home:
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.action_start:
                if (mMessage != null && mMessage.getText().toString().length() > 0) {
                    item.setEnabled(false);
                    getBTService().postStartClicker(mCourseID, mMessage.getText().toString(), 4, new Callback<PostJson>() {
                        @Override
                        public void success(PostJson postJson, Response response) {
                            if (AttendanceStartFragment.this.getActivity() != null)
                                AttendanceStartFragment.this.getActivity().onBackPressed();
                            item.setEnabled(true);
                        }

                        @Override
                        public void failure(RetrofitError retrofitError) {
                            item.setEnabled(true);
                        }
                    });
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
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
import com.bttendance.event.dialog.HideProgressDialogEvent;
import com.bttendance.event.dialog.ShowProgressDialogEvent;
import com.bttendance.fragment.BTFragment;
import com.bttendance.helper.KeyboardHelper;
import com.bttendance.model.json.PostJson;
import com.squareup.otto.BTEventBus;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2014. 1. 27..
 */
public class AttendanceStartFragment extends BTFragment {

    private int mCourseID;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendance_start, container, false);
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
        actionBar.setTitle(getString(R.string.start_attendance));
        inflater.inflate(R.menu.attendance_start_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.abs__home:
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.action_start:
                if (true) {
                    item.setEnabled(false);
                    BTEventBus.getInstance().post(new ShowProgressDialogEvent(getString(R.string.starting_attendance)));
                    getBTService().postStartClicker(mCourseID, "Asdf", 4, new Callback<PostJson>() {
                        @Override
                        public void success(PostJson postJson, Response response) {
                            item.setEnabled(true);
                            BTEventBus.getInstance().post(new HideProgressDialogEvent());
                            if (AttendanceStartFragment.this.getActivity() != null)
                                AttendanceStartFragment.this.getActivity().onBackPressed();
                        }

                        @Override
                        public void failure(RetrofitError retrofitError) {
                            item.setEnabled(true);
                            BTEventBus.getInstance().post(new HideProgressDialogEvent());
                        }
                    });
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

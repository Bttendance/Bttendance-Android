package com.bttendance.fragment.notice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.bttendance.R;
import com.bttendance.event.dialog.HideProgressDialogEvent;
import com.bttendance.event.dialog.ShowProgressDialogEvent;
import com.bttendance.fragment.BTFragment;
import com.bttendance.helper.KeyboardHelper;
import com.bttendance.model.BTKey;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.PostJson;
import com.squareup.otto.BTEventBus;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2014. 1. 27..
 */
public class NoticePostFragment extends BTFragment {

    private int mCourseID;
    private EditText mMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mCourseID = getArguments() != null ? getArguments().getInt(BTKey.EXTRA_COURSE_ID) : 0;

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
        View view = inflater.inflate(R.layout.fragment_notice_post, container, false);
        int studentCount = 0;
        if (BTTable.MyCourseTable.get(mCourseID) != null)
            studentCount = BTTable.MyCourseTable.get(mCourseID).students_count;

        TextView guide = (TextView) view.findViewById(R.id.notice_guide);
        guide.setText(String.format(getString(R.string.notice_post_guide), studentCount));
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
        actionBar.setTitle(getString(R.string.create_notice));
        inflater.inflate(R.menu.notice_create_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.abs__home:
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.action_post:
                if (mMessage != null && mMessage.getText().toString().length() > 0) {
                    item.setEnabled(false);
                    BTEventBus.getInstance().post(new ShowProgressDialogEvent(getString(R.string.posting_notice)));
                    getBTService().postCreateNotice(mCourseID, mMessage.getText().toString(), new Callback<PostJson>() {
                        @Override
                        public void success(PostJson postJson, Response response) {
                            item.setEnabled(true);
                            BTEventBus.getInstance().post(new HideProgressDialogEvent());
                            if (NoticePostFragment.this.getActivity() != null)
                                NoticePostFragment.this.getActivity().onBackPressed();
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

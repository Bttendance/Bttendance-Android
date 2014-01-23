package com.bttendance.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.bttendance.R;
import com.bttendance.adapter.ProfileAdapter;
import com.bttendance.event.AttdCheckedEvent;
import com.bttendance.event.AttdEndEvent;
import com.bttendance.event.AttdStartedEvent;
import com.bttendance.event.JoinSchoolEvent;
import com.bttendance.event.LoadingEvent;
import com.bttendance.event.ProfileUpdateEvent;
import com.bttendance.event.UpdateProfileEvent;
import com.bttendance.helper.DipPixelHelper;
import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.cursor.SchoolCursor;
import com.bttendance.model.json.SchoolJson;
import com.bttendance.model.json.UserJson;
import com.squareup.otto.BTEventBus;
import com.squareup.otto.Subscribe;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 12. 1..
 */
public class ProfileFragment extends BTFragment implements View.OnClickListener {

    ListView mListView;
    ProfileAdapter mAdapter;
    View header;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mListView = (ListView) view.findViewById(android.R.id.list);
        header = inflater.inflate(R.layout.profile_header, null, false);
        mListView.addHeaderView(header);

        header.findViewById(R.id.mail_edit).setOnClickListener(this);
        header.findViewById(R.id.name_edit).setOnClickListener(this);
        header.findViewById(R.id.profile_image_edit).setOnClickListener(this);
        refreshHeader();

        LayoutInflater mInflater = (LayoutInflater) getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View footer = mInflater.inflate(R.layout.school_join, null);
        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BTEventBus.getInstance().post(new JoinSchoolEvent());
            }
        });
        mListView.addFooterView(footer);

        View padding = new View(getActivity());
        padding.setMinimumHeight((int) DipPixelHelper.getPixel(getActivity(), 7));
        mListView.addFooterView(padding);
        mAdapter = new ProfileAdapter(getActivity(), null);
        mListView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onServieConnected() {
        super.onServieConnected();
        getSchools();
    }

    public void getSchools() {
        if (getBTService() == null)
            return;

        BTEventBus.getInstance().post(new LoadingEvent(true));
        getBTService().schools(new Callback<SchoolJson[]>() {
            @Override
            public void success(SchoolJson[] schools, Response response) {
                mAdapter.swapCursor(new SchoolCursor(BTTable.FILTER_MY_SCHOOL));
                BTEventBus.getInstance().post(new LoadingEvent(false));
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                BTEventBus.getInstance().post(new LoadingEvent(false));
            }
        });
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        swapCursor();
    }

    @Subscribe
    public void onProfileUpdate(ProfileUpdateEvent event) {
        refreshHeader();
    }

    private void swapCursor() {
        if (this.isAdded() && mAdapter != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.swapCursor(new SchoolCursor(BTTable.FILTER_MY_SCHOOL));
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        if (header == null)
            return;

        switch (v.getId()) {
            case R.id.profile_image_edit:
                break;
            case R.id.name_edit:
                TextView name = (TextView) header.findViewById(R.id.name);
                BTEventBus.getInstance().post(new UpdateProfileEvent(getString(R.string.name),
                        name.getText().toString(),
                        UpdateProfileEvent.Type.NAME));
                break;
            case R.id.mail_edit:
                TextView mail = (TextView) header.findViewById(R.id.mail);
                BTEventBus.getInstance().post(new UpdateProfileEvent(getString(R.string.mail),
                        mail.getText().toString(),
                        UpdateProfileEvent.Type.MAIL));
                break;
        }
    }

    private void refreshHeader() {
        if (header == null)
            return;

        UserJson user = BTPreference.getUser(getActivity());
        TextView account = (TextView) header.findViewById(R.id.account_type);
        account.setText(user.type);
        TextView username = (TextView) header.findViewById(R.id.username);
        username.setText(user.username);
        TextView name = (TextView) header.findViewById(R.id.name);
        name.setText(user.full_name);
        TextView mail = (TextView) header.findViewById(R.id.mail);
        mail.setText(user.email);
    }
}

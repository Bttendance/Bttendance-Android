package com.bttendance.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.bttendance.R;
import com.bttendance.activity.MainActivity;
import com.bttendance.adapter.ProfileAdapter;
import com.bttendance.event.AddFragmentEvent;
import com.bttendance.event.update.UpdateProfileEvent;
import com.bttendance.model.BTKey;
import com.bttendance.model.BTPreference;
import com.squareup.otto.BTEventBus;
import com.squareup.otto.Subscribe;

/**
 * Created by TheFinestArtist on 2013. 12. 1..
 */
public class ProfileFragment extends BTFragment implements AdapterView.OnItemClickListener {

    ListView mListView;
    ProfileAdapter mAdapter;

    /**
     * Action Bar Menu
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        if (!((MainActivity) getActivity()).isDrawerOpen())
            actionBar.setTitle(getString(R.string.profile));
    }

    /**
     * Drawing View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mListView = (ListView) view.findViewById(android.R.id.list);
        mAdapter = new ProfileAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        refreshAdapter();
    }

    @Subscribe
    public void onUpdate(UpdateProfileEvent event) {
        refreshAdapter();
    }

    private void refreshAdapter() {
        if (this.isAdded() && mAdapter != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.refreshAdapter();
                }
            });
        }
    }

    /**
     * ************************
     * onItemClick
     * *************************
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        switch (mAdapter.getItem(position).getType()) {
            case Name:
                showEditName();
                break;
            case Email:
                showEditEmail();
                break;
            case SavedClicker:
                showSavedClicker();
                break;
            case Course:
                showCourse();
                break;
            case Institution:
                showEditIdentity();
                break;
            case Password:
                showChangePassword();
                break;
            case Section:
                break;
            case Margin:
                break;
        }
    }

    /**
     * Private Methods
     */
    private void showEditName() {
        ProfileEditFragment frag = new ProfileEditFragment();
        Bundle undle = new Bundle();
        undle.putString(BTKey.EXTRA_TITLE, getString(R.string.name));
        undle.putString(BTKey.EXTRA_MESSAGE, BTPreference.getUser(getActivity()).full_name);
        undle.putSerializable(BTKey.EXTRA_TYPE, ProfileEditFragment.Type.NAME);
        frag.setArguments(undle);
        BTEventBus.getInstance().post(new AddFragmentEvent(frag));
    }

    private void showEditEmail() {
        ProfileEditFragment frag = new ProfileEditFragment();
        Bundle undle = new Bundle();
        undle.putString(BTKey.EXTRA_TITLE, getString(R.string.mail));
        undle.putString(BTKey.EXTRA_MESSAGE, BTPreference.getUser(getActivity()).full_name);
        undle.putSerializable(BTKey.EXTRA_TYPE, ProfileEditFragment.Type.MAIL);
        frag.setArguments(undle);
        BTEventBus.getInstance().post(new AddFragmentEvent(frag));
    }

    private void showSavedClicker() {

    }

    private void showCourse() {

    }

    private void showEditIdentity() {

    }

    private void showChangePassword() {

    }
}

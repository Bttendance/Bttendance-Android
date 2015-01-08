package com.bttendance.fragment.profile;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bttendance.R;
import com.bttendance.adapter.ProfileAdapter;
import com.bttendance.event.AddFragmentEvent;
import com.bttendance.event.update.UserUpdatedEvent;
import com.bttendance.fragment.BTFragment;
import com.bttendance.model.BTKey;
import com.bttendance.model.BTTable;
import com.bttendance.widget.BTDialog;
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
        if (getActivity() == null)
            return;

        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
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
    public void onUpdate(UserUpdatedEvent event) {
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
            case Employed:
                break;
            case Enrolled:
                showEditIdentity((Integer) view.getTag(R.id.school_id));
                break;
            case Password:
                showChangePassword();
                break;
            case SignOut:
                signOut();
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
        Bundle bundle = new Bundle();
        bundle.putString(BTKey.EXTRA_TITLE, getString(R.string.name));
        bundle.putString(BTKey.EXTRA_MESSAGE, BTTable.getMe().name);
        bundle.putSerializable(BTKey.EXTRA_TYPE, ProfileEditFragment.Type.NAME);
        frag.setArguments(bundle);
        BTEventBus.getInstance().post(new AddFragmentEvent(frag));
    }

    private void showEditEmail() {
        ProfileEditFragment frag = new ProfileEditFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BTKey.EXTRA_TITLE, getString(R.string.email));
        bundle.putString(BTKey.EXTRA_MESSAGE, BTTable.getMe().email);
        bundle.putSerializable(BTKey.EXTRA_TYPE, ProfileEditFragment.Type.MAIL);
        frag.setArguments(bundle);
        BTEventBus.getInstance().post(new AddFragmentEvent(frag));
    }

    private void showEditIdentity(int schoolID) {
        ProfileEditFragment frag = new ProfileEditFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BTKey.EXTRA_TITLE, getString(R.string.identity));
        bundle.putString(BTKey.EXTRA_MESSAGE, BTTable.getMe().getIdentity(schoolID));
        bundle.putSerializable(BTKey.EXTRA_SCHOOL_ID, schoolID);
        bundle.putSerializable(BTKey.EXTRA_TYPE, ProfileEditFragment.Type.IDENTITY);
        frag.setArguments(bundle);
        BTEventBus.getInstance().post(new AddFragmentEvent(frag));
    }

    private void showChangePassword() {
        UpdatePasswordFragment frag = new UpdatePasswordFragment();
        BTEventBus.getInstance().post(new AddFragmentEvent(frag));
    }

    private void signOut() {
        BTDialog.alert(getActivity(),
                getString(R.string.sign_out),
                getString(R.string.sign_out_message),
                new BTDialog.OnDialogListener() {
            @Override
            public void onConfirmed(String edit) {
                if (getBTService() != null)
                    getBTService().signOut();
            }

            @Override
            public void onCanceled() {
            }
        });
    }
}

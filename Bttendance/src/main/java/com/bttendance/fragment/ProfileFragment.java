package com.bttendance.fragment;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.bttendance.R;
import com.bttendance.adapter.ProfileAdapter;
import com.bttendance.event.AddFragmentEvent;
import com.bttendance.event.update.UpdateProfileEvent;
import com.bttendance.helper.DipPixelHelper;
import com.bttendance.model.BTKey;
import com.bttendance.model.BTPreference;
import com.bttendance.model.cursor.MySchoolCursor;
import com.bttendance.model.json.UserJson;
import com.squareup.otto.BTEventBus;
import com.squareup.otto.Subscribe;

/**
 * Created by TheFinestArtist on 2013. 12. 1..
 */
public class ProfileFragment extends BTFragment implements View.OnClickListener {

    ListView mListView;
    ProfileAdapter mAdapter;
    View header;

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
        inflater.inflate(R.menu.profile_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                registerForContextMenu(mListView);
                getActivity().openContextMenu(mListView);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Context Menu
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.profile_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {

        switch (item.getItemId()) {
            case R.id.edit_name:
                showEditName();
                return true;
            case R.id.edit_email:
                showEditEmail();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * Drawing View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mListView = (ListView) view.findViewById(android.R.id.list);
        header = inflater.inflate(R.layout.profile_header, null, false);
        mListView.addHeaderView(header);

        header.findViewById(R.id.profile_image_edit).setOnClickListener(this);

        View padding = new View(getActivity());
        padding.setMinimumHeight((int) DipPixelHelper.getPixel(getActivity(), 7));
        mListView.addFooterView(padding);

        mAdapter = new ProfileAdapter(getActivity(), null);
        mListView.setAdapter(mAdapter);

        refreshHeader();
        swapCursor();

        return view;
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        refreshHeader();
        swapCursor();
    }

    @Subscribe
    public void onUpdate(UpdateProfileEvent event) {
        refreshHeader();
        swapCursor();
    }

    private void swapCursor() {
        if (this.isAdded() && mAdapter != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.swapCursor(new MySchoolCursor(getActivity()));
                }
            });
        }
    }

    private void refreshHeader() {
        if (!this.isAdded() && header == null)
            return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UserJson user = BTPreference.getUser(getActivity());
                TextView account = (TextView) header.findViewById(R.id.account_type);
                if (user.employed_schools.length > 0 && user.enrolled_schools.length > 0)
                    account.setText(getString(R.string.professor) + " & " + getString(R.string.student));
                else if (user.employed_schools.length > 0)
                    account.setText(getString(R.string.professor));
                else
                    account.setText(getString(R.string.student));

                TextView username = (TextView) header.findViewById(R.id.username);
                username.setText(user.username);
                TextView name = (TextView) header.findViewById(R.id.name);
                name.setText(user.full_name);
                TextView mail = (TextView) header.findViewById(R.id.mail);
                mail.setText(user.email);
            }
        });
    }

    /**
     * View.OnClickListner
     */
    @Override
    public void onClick(View v) {
        if (header == null)
            return;

        switch (v.getId()) {
            case R.id.profile_image_edit:
                break;
        }
    }

    /**
     * Private Methods
     */
    private void showEditName() {
        TextView name = (TextView) header.findViewById(R.id.name);
        ProfileEditFragment frag = new ProfileEditFragment();
        Bundle undle = new Bundle();
        undle.putString(BTKey.EXTRA_TITLE, getString(R.string.name));
        undle.putString(BTKey.EXTRA_MESSAGE, name.getText().toString());
        undle.putSerializable(BTKey.EXTRA_TYPE, ProfileEditFragment.Type.NAME);
        frag.setArguments(undle);
        BTEventBus.getInstance().post(new AddFragmentEvent(frag));
    }

    private void showEditEmail() {
        TextView mail = (TextView) header.findViewById(R.id.mail);
        ProfileEditFragment frag = new ProfileEditFragment();
        Bundle undle = new Bundle();
        undle.putString(BTKey.EXTRA_TITLE, getString(R.string.mail));
        undle.putString(BTKey.EXTRA_MESSAGE, mail.getText().toString());
        undle.putSerializable(BTKey.EXTRA_TYPE, ProfileEditFragment.Type.MAIL);
        frag.setArguments(undle);
        BTEventBus.getInstance().post(new AddFragmentEvent(frag));
    }
}

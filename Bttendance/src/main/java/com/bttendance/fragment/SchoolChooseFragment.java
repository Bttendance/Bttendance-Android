package com.bttendance.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.bttendance.R;
import com.bttendance.adapter.ChooseSchoolAdapter;
import com.bttendance.adapter.kit.Sectionizer;
import com.bttendance.adapter.kit.SimpleSectionAdapter;
import com.bttendance.event.AddFragmentEvent;
import com.bttendance.event.update.UpdateSchoolChooseEvent;
import com.bttendance.helper.IntArrayHelper;
import com.bttendance.model.BTPreference;
import com.bttendance.model.cursor.SectionedSchoolCursor;
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
public class SchoolChooseFragment extends BTFragment implements AdapterView.OnItemClickListener {

    ChooseSchoolAdapter mAdapter;
    SimpleSectionAdapter<Cursor> mSectionAdapter;
    private boolean mAuth;
    private ListView mListView;
    private UserJson user;

    public SchoolChooseFragment(boolean auth) {
        mAuth = auth;
        user = BTPreference.getUser(getActivity());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_school_choose, container, false);
        mListView = (ListView) view.findViewById(android.R.id.list);

        mAdapter = new ChooseSchoolAdapter(
                getActivity(),
                R.layout.school_choose_item,
                SchoolJson.class,
                null);

        mSectionAdapter = new SimpleSectionAdapter<Cursor>(
                getActivity(), mAdapter, R.layout.simple_section,
                R.id.section_text, new Sectionizer<Cursor>() {

            @Override
            public String getSectionTitleForItem(Cursor cursor) {
                if (mAuth) {
                    if (IntArrayHelper.contains(user.employed_schools, cursor.getInt(0)))
                        return getString(R.string.employed_schools);
                    else
                        return getString(R.string.joinable_schools);
                } else {
                    if (IntArrayHelper.contains(user.enrolled_schools, cursor.getInt(0)))
                        return getString(R.string.enrolled_schools);
                    else
                        return getString(R.string.joinable_schools);
                }
            }
        });

        mListView.setAdapter(mSectionAdapter);
        mListView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        swapItems();
    }

    @Subscribe
    public void onUpdate(UpdateSchoolChooseEvent event) {
        swapItems();
    }

    private void swapItems() {
        if (this.isAdded() && mAdapter != null) {
            user = BTPreference.getUser(getActivity());
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.swapCursor(new SectionedSchoolCursor(getActivity(), mAuth));
                    mSectionAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        getBTService().allSchools(new Callback<SchoolJson[]>() {
            @Override
            public void success(SchoolJson[] schoolJsons, Response response) {
                swapItems();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setTitle(getString(R.string.choose_school));
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.abs__home:
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {
        if (mAuth) {
            CourseCreateFragment frag = new CourseCreateFragment((int) id);
            BTEventBus.getInstance().post(new AddFragmentEvent(frag));
        } else {
            CourseAttendFragment frag = new CourseAttendFragment((int) id);
            BTEventBus.getInstance().post(new AddFragmentEvent(frag));
        }
    }
}

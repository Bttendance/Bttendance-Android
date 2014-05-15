package com.bttendance.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.bttendance.R;
import com.bttendance.adapter.CourseListAdapter;
import com.bttendance.adapter.kit.Sectionizer;
import com.bttendance.adapter.kit.SimpleSectionAdapter;
import com.bttendance.event.AddFragmentEvent;
import com.bttendance.event.attendance.AttdStartedEvent;
import com.bttendance.event.dialog.ShowContextDialogEvent;
import com.bttendance.event.refresh.RefreshCourseListEvent;
import com.bttendance.event.update.UpdateCourseListEvent;
import com.bttendance.helper.DipPixelHelper;
import com.bttendance.helper.IntArrayHelper;
import com.bttendance.model.BTPreference;
import com.bttendance.model.cursor.MyCourseCursor;
import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.UserJson;
import com.squareup.otto.BTEventBus;
import com.squareup.otto.Subscribe;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 12. 1..
 */
public class CourseListFragment extends BTFragment {

    ListView mListView;
    CourseListAdapter mAdapter;
    SimpleSectionAdapter<Cursor> mSectionAdapter;
    UserJson mUser;
    View mView;

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
        inflater.inflate(R.menu.course_list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_plus:
                String[] options = {getString(R.string.create_course), getString(R.string.attend_course)};
                BTEventBus.getInstance().post(new ShowContextDialogEvent(options, new BTDialogFragment.OnDialogListener() {
                    @Override
                    public void onConfirmed(String edit) {
                        if (getString(R.string.create_course).equals(edit))
                            showSchoolChoose(true);
                        if (getString(R.string.attend_course).equals(edit))
                            showSchoolChoose(false);
                    }

                    @Override
                    public void onCanceled() {
                    }
                }));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Drawing View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_course_list, container, false);
        mListView = (ListView) mView.findViewById(android.R.id.list);

        View header = new View(getActivity());
        header.setMinimumHeight((int) DipPixelHelper.getPixel(getActivity(), 8));
        mListView.addHeaderView(header);

        mUser = BTPreference.getUser(getActivity());
        mAdapter = new CourseListAdapter(getActivity(), null);
        mSectionAdapter = new SimpleSectionAdapter<Cursor>(
                getActivity(),
                mAdapter,
                R.layout.simple_section,
                R.id.section_text,
                new Sectionizer<Cursor>() {
                    @Override
                    public String getSectionTitleForItem(Cursor cursor) {
                        if (IntArrayHelper.contains(mUser.supervising_courses, cursor.getInt(0)))
                            return getString(R.string.supervising_courses);
                        else
                            return getString(R.string.attending_courses);
                    }
                });
        mListView.setAdapter(mSectionAdapter);
        swapCursor();

        return mView;
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        getCourseList();
    }

    public void getCourseList() {
        if (getBTService() == null)
            return;

        getBTService().courses(new Callback<CourseJson[]>() {
            @Override
            public void success(CourseJson[] courses, Response response) {
                swapCursor();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
            }
        });
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        swapCursor();
    }

    @Subscribe
    public void onRefresh(RefreshCourseListEvent event) {
        getCourseList();
    }

    @Subscribe
    public void onUpdate(UpdateCourseListEvent event) {
        swapCursor();
    }

    @Subscribe
    public void onAttdStarted(AttdStartedEvent event) {
        swapCursor();
    }

    private void swapCursor() {
        if (this.isAdded() && mAdapter != null) {

            mUser = BTPreference.getUser(getActivity());
            if (mUser == null)
                return;

            if (mUser.getCourses().length == 0)
                mView.findViewById(R.id.no_course_layout).setVisibility(View.VISIBLE);
            else
                mView.findViewById(R.id.no_course_layout).setVisibility(View.GONE);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.swapCursor(new MyCourseCursor(getActivity()));
                    mSectionAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    /**
     * Private Methods
     */
    private void showSchoolChoose(boolean auth) {
        SchoolChooseFragment frag = new SchoolChooseFragment(auth);
        BTEventBus.getInstance().post(new AddFragmentEvent(frag));
    }
}

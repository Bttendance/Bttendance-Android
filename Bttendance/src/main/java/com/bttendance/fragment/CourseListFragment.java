package com.bttendance.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bttendance.R;
import com.bttendance.adapter.CourseListAdapter;
import com.bttendance.adapter.kit.Sectionizer;
import com.bttendance.adapter.kit.SimpleSectionAdapter;
import com.bttendance.event.LoadingEvent;
import com.bttendance.event.attendance.AttdEndEvent;
import com.bttendance.event.attendance.AttdStartedEvent;
import com.bttendance.helper.DipPixelHelper;
import com.bttendance.helper.IntArrayHelper;
import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
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
    UserJson user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_list, container, false);
        mListView = (ListView) view.findViewById(android.R.id.list);

        View header = new View(getActivity());
        header.setMinimumHeight((int) DipPixelHelper.getPixel(getActivity(), 8));
        mListView.addHeaderView(header);

        user = BTPreference.getUser(getActivity());
        mAdapter = new CourseListAdapter(getActivity(), null);
        mSectionAdapter = new SimpleSectionAdapter<Cursor>(
                getActivity(),
                mAdapter,
                R.layout.simple_section,
                R.id.section_text,
                new Sectionizer<Cursor>() {
                    @Override
                    public String getSectionTitleForItem(Cursor cursor) {
                        if (IntArrayHelper.contains(user.supervising_courses, cursor.getInt(0))
                                || MyCourseCursor.ADD_BUTTON_CREATE_COURSE == cursor.getInt(0))
                            return getString(R.string.supervising_courses);
                        else
                            return getString(R.string.attending_courses);
                    }
                });
        mListView.setAdapter(mSectionAdapter);
        swapCursor();
        return view;
    }

    @Override
    public void onServieConnected() {
        super.onServieConnected();
        getCourseList();
    }

    public void getCourseList() {
        if (getBTService() == null)
            return;

        BTEventBus.getInstance().post(new LoadingEvent(true));
        getBTService().courses(new Callback<CourseJson[]>() {
            @Override
            public void success(CourseJson[] courses, Response response) {
                swapCursor();
                if (BTTable.getCheckingPostIds().size() > 0)
                    BTEventBus.getInstance().post(new AttdStartedEvent(true));
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Subscribe
    public void onAttdStarted(AttdStartedEvent event) {
        swapCursor();
    }

    @Subscribe
    public void onAttdEndEvent(AttdEndEvent event) {
        getCourseList();
    }

    private void swapCursor() {
        if (this.isAdded() && mAdapter != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    user = BTPreference.getUser(getActivity());
                    mAdapter.swapCursor(new MyCourseCursor(getActivity()));
                    mSectionAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}

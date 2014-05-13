package com.bttendance.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.bttendance.R;
import com.bttendance.adapter.BTListAdapter;
import com.bttendance.event.dialog.HideProgressDialogEvent;
import com.bttendance.event.dialog.ShowAlertDialogEvent;
import com.bttendance.event.dialog.ShowProgressDialogEvent;
import com.bttendance.event.refresh.RefreshFeedEvent;
import com.bttendance.event.update.UpdateCourseListEvent;
import com.bttendance.event.update.UpdateProfileEvent;
import com.bttendance.helper.IntArrayHelper;
import com.bttendance.helper.SparceArrayHelper;
import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.UserJson;
import com.squareup.otto.BTEventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 12. 1..
 */
public class CourseAttendFragment extends BTFragment implements View.OnClickListener {

    BTListAdapter mAdapter;
    private int mSchoolID;
    private ListView mListView;
    private UserJson user;

    public CourseAttendFragment(int schoolID) {
        mSchoolID = schoolID;
        user = BTPreference.getUser(getActivity());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_attend, container, false);
        mListView = (ListView) view.findViewById(android.R.id.list);
        mAdapter = new BTListAdapter(getActivity(), this);
        mListView.setAdapter(mAdapter);
        swapItems();
        return view;
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        swapItems();
    }

    private void swapItems() {
        if (this.isAdded() && mAdapter != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    user = BTPreference.getUser(getActivity());

                    ArrayList<CourseJson> courses = SparceArrayHelper.asArrayList(BTTable.getCoursesOfSchool(mSchoolID));
                    Collections.sort(courses, new Comparator<CourseJson>() {
                        @Override
                        public int compare(CourseJson lhs, CourseJson rhs) {
                            return lhs.name.compareToIgnoreCase(rhs.name);
                        }
                    });

                    ArrayList<CourseJson> attendingCourses = new ArrayList<CourseJson>();
                    ArrayList<CourseJson> joinableCourses = new ArrayList<CourseJson>();
                    for (CourseJson course : courses) {
                        boolean joined = IntArrayHelper.contains(user.attending_courses, course.id)
                                || IntArrayHelper.contains(user.supervising_courses, course.id);
                        if (joined)
                            attendingCourses.add(course);
                        else
                            joinableCourses.add(course);
                    }

                    ArrayList<BTListAdapter.Item> items = new ArrayList<BTListAdapter.Item>();
                    if (attendingCourses.size() > 0)
                        items.add(new BTListAdapter.Item(getString(R.string.attending_courses)));

                    for (CourseJson course : attendingCourses) {
                        String title = course.name;
                        String message = course.professor_name;
                        items.add(new BTListAdapter.Item(BTListAdapter.Item.Type.JOINED, title, message, course));
                    }

                    if (joinableCourses.size() > 0)
                        items.add(new BTListAdapter.Item(getString(R.string.joinable_courses)));

                    for (CourseJson course : joinableCourses) {
                        String title = course.name;
                        String message = course.professor_name;
                        items.add(new BTListAdapter.Item(BTListAdapter.Item.Type.UNJOINED, title, message, course));
                    }
                    mAdapter.setItems(items);
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        getBTService().schoolCourses(mSchoolID, new Callback<CourseJson[]>() {
            @Override
            public void success(CourseJson[] courseJsons, Response response) {
                swapItems();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_btn:

                final CourseJson course = (CourseJson) v.getTag(R.id.json);

                BTDialogFragment.DialogType type;
                String title;
                String message;
                String placeholder = null;
                BTDialogFragment.OnDialogListener listener;

                if (IntArrayHelper.contains(BTPreference.getUser(getActivity()).enrolled_schools, course.school.id)) {
                    type = BTDialogFragment.DialogType.CONFIRM;
                    title = getString(R.string.attend_course);
                    message = String.format(getString(R.string.would_you_like_to), course.name, getString(R.string.prof_) + course.professor_name);
                } else {
                    type = BTDialogFragment.DialogType.EDIT;
                    if ("public".equals(course.school.type)) {
                        title = getString(R.string.student_id);
                        message = String.format(getString(R.string.before_you_join_course_public), course.name);
                    } else {
                        title = getString(R.string.phone_number);
                        message = String.format(getString(R.string.before_you_join_course_private), course.name);
                    }

                    if ("private".equals(course.school.type))
                        placeholder = "XXX-XXXX-XXXX";
                }

                listener = new BTDialogFragment.OnDialogListener() {
                    @Override
                    public void onConfirmed(String edit) {
                        BTEventBus.getInstance().post(new ShowProgressDialogEvent(getString(R.string.attending_course)));
                        if (IntArrayHelper.contains(BTPreference.getUser(getActivity()).enrolled_schools, course.school.id)) {
                            getBTService().attendCourse(course.id, new Callback<UserJson>() {
                                @Override
                                public void success(UserJson userJson, Response response) {
                                    swapItems();
                                    BTEventBus.getInstance().post(new HideProgressDialogEvent());
                                    BTEventBus.getInstance().post(new UpdateCourseListEvent());
                                    BTEventBus.getInstance().post(new RefreshFeedEvent());
                                }

                                @Override
                                public void failure(RetrofitError retrofitError) {
                                    BTEventBus.getInstance().post(new HideProgressDialogEvent());
                                }
                            });
                        } else {
                            getBTService().enrollSchool(course.school.id, edit, new Callback<UserJson>() {
                                @Override
                                public void success(UserJson userJson, Response response) {
                                    BTEventBus.getInstance().post(new UpdateProfileEvent());
                                    getBTService().attendCourse(course.id, new Callback<UserJson>() {
                                        @Override
                                        public void success(UserJson userJson, Response response) {
                                            swapItems();
                                            BTEventBus.getInstance().post(new HideProgressDialogEvent());
                                            BTEventBus.getInstance().post(new UpdateCourseListEvent());
                                            BTEventBus.getInstance().post(new RefreshFeedEvent());
                                        }

                                        @Override
                                        public void failure(RetrofitError retrofitError) {
                                            BTEventBus.getInstance().post(new HideProgressDialogEvent());
                                        }
                                    });
                                }

                                @Override
                                public void failure(RetrofitError retrofitError) {
                                    BTEventBus.getInstance().post(new HideProgressDialogEvent());
                                }
                            });
                        }
                    }

                    @Override
                    public void onCanceled() {
                    }
                };

                BTEventBus.getInstance().post(new ShowAlertDialogEvent(type, title, message, placeholder, listener));
                break;
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setTitle(getString(R.string.attend_course));
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
}

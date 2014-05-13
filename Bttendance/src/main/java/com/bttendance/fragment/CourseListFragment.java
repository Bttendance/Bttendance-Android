package com.bttendance.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.bttendance.BTDebug;
import com.bttendance.R;
import com.bttendance.adapter.CourseListAdapter;
import com.bttendance.adapter.kit.Sectionizer;
import com.bttendance.adapter.kit.SimpleSectionAdapter;
import com.bttendance.event.AddFragmentEvent;
import com.bttendance.event.LoadingEvent;
import com.bttendance.event.attendance.AttdStartedEvent;
import com.bttendance.event.refresh.RefreshCourseListEvent;
import com.bttendance.event.update.UpdateCourseListEvent;
import com.bttendance.helper.DipPixelHelper;
import com.bttendance.helper.IntArrayHelper;
import com.bttendance.model.BTPreference;
import com.bttendance.model.cursor.MyCourseCursor;
import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.UserJson;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.socketio.Acknowledge;
import com.koushikdutta.async.http.socketio.ConnectCallback;
import com.koushikdutta.async.http.socketio.EventCallback;
import com.koushikdutta.async.http.socketio.JSONCallback;
import com.koushikdutta.async.http.socketio.SocketIOClient;
import com.koushikdutta.async.http.socketio.StringCallback;
import com.squareup.otto.BTEventBus;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONObject;

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
        getActivity().getMenuInflater().inflate(R.menu.course_list_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {

        switch (item.getItemId()) {
            case R.id.create_course:
                showSchoolChoose(true);
                return true;
            case R.id.attend_course:
                showSchoolChoose(false);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * Drawing View
     */
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
                        if (IntArrayHelper.contains(user.supervising_courses, cursor.getInt(0)))
                            return getString(R.string.supervising_courses);
                        else
                            return getString(R.string.attending_courses);
                    }
                });
        mListView.setAdapter(mSectionAdapter);
        swapCursor();

        SocketIOClient.connect(AsyncHttpClient.getDefaultInstance(), "http://bttendance-dev.herokuapp.com", new ConnectCallback() {
            @Override
            public void onConnectCompleted(Exception ex, SocketIOClient client) {
                if (ex != null) {
                    ex.printStackTrace();
                    return;
                }

                BTDebug.LogError("Connected");

                client.setStringCallback(new StringCallback() {
                    @Override
                    public void onString(String string, Acknowledge acknowledge) {
                        BTDebug.LogError(string);
                    }
                });
                client.on("bttendance", new EventCallback() {
                    @Override
                    public void onEvent(JSONArray arguments, Acknowledge acknowledge) {
                        BTDebug.LogError("args: " + arguments.toString());
                    }
                });
                client.setJSONCallback(new JSONCallback() {
                    @Override
                    public void onJSON(JSONObject json, Acknowledge acknowledge) {
                        BTDebug.LogError("json: " + json.toString());
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
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
//                if (BTTable.getCheckingPostIds().size() > 0) {
//                    BTEventBus.getInstance().post(new AttdStartedEvent(true));
//                }
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

    /**
     * Private Methods
     */
    private void showSchoolChoose(boolean auth) {
        SchoolChooseFragment frag = new SchoolChooseFragment(auth);
        BTEventBus.getInstance().post(new AddFragmentEvent(frag));
    }
}

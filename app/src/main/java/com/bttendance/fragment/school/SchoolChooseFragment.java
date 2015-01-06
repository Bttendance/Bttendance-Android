package com.bttendance.fragment.school;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.bttendance.R;
import com.bttendance.activity.MainActivity;
import com.bttendance.activity.course.CreateCourseActivity;
import com.bttendance.adapter.SchoolAdapter;
import com.bttendance.adapter.kit.Sectionizer;
import com.bttendance.adapter.kit.SimpleSectionAdapter;
import com.bttendance.event.AddFragmentEvent;
import com.bttendance.fragment.BTFragment;
import com.bttendance.helper.KeyboardHelper;
import com.bttendance.model.BTTable;
import com.bttendance.model.cursor.AllSchoolCursor;
import com.bttendance.model.json.SchoolJson;
import com.bttendance.model.json.UserJson;
import com.squareup.otto.BTEventBus;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 12. 1..
 */
public class SchoolChooseFragment extends BTFragment implements AdapterView.OnItemClickListener, Callback<SchoolJson[]> {

    @InjectView(android.R.id.list)
    public ListView mListView;
    @InjectView(R.id.create_school)
    public Button mCreateSchoolBt;

    SchoolAdapter mAdapter;
    SimpleSectionAdapter<Cursor> mSectionAdapter;
    EndlessScrollListener endlessScrollListener;

    EditText mEditSearch;
    String mFilter;
    private UserJson mUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mUser = BTTable.getMe();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_school_choose, container, false);
        ButterKnife.inject(this, view);

        mAdapter = new SchoolAdapter(
                getActivity(),
                R.layout.school_choose_item,
                SchoolJson.class,
                null);

        mSectionAdapter = new SimpleSectionAdapter<Cursor>(
                getActivity(), mAdapter, R.layout.simple_section,
                R.id.section_text, new Sectionizer<Cursor>() {

            @Override
            public String getSectionTitleForItem(Cursor cursor) {
                if (mUser.hasSchool(cursor.getInt(0)))
                    return getString(R.string.my_institutions);
                else
                    return getString(R.string.other_institutions);
            }
        });

        mListView.setAdapter(mSectionAdapter);
        mListView.setOnItemClickListener(this);

        endlessScrollListener = new EndlessScrollListener();
        mListView.setOnScrollListener(endlessScrollListener);

        mCreateSchoolBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SchoolCreateFragment fragment = new SchoolCreateFragment();
                BTEventBus.getInstance().post(new AddFragmentEvent(fragment));
            }
        });

        return view;
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        KeyboardHelper.hide(getActivity(), mEditSearch);
        getBTService().getMySchools(this);
        loadSchool(0);
        swapItems();
    }

    private void swapItems() {
        if (this.isAdded() && mAdapter != null) {
            mUser = BTTable.getMe();
            if (mEditSearch != null)
                mFilter = mEditSearch.getText().toString();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.swapCursor(new AllSchoolCursor(mFilter));
                    mSectionAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (getActivity() == null)
            return;

        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(getString(R.string.choose_institution));
        actionBar.setDisplayHomeAsUpEnabled(true);

        inflater.inflate(R.menu.school_choose_menu, menu);

        mEditSearch = (EditText) MenuItemCompat.getActionView(menu.findItem(R.id.action_search)).findViewById(R.id.search_edit);
        mEditSearch.addTextChangedListener(mTextWatcher);

        MenuItem menuSearch = menu.findItem(R.id.action_search);
        MenuItemCompat.setOnActionExpandListener(menuSearch, new MenuItemCompat.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                mEditSearch.setText("");
                KeyboardHelper.hide(getActivity(), mEditSearch);
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                KeyboardHelper.show(getActivity(), mEditSearch);
                return true;
            }
        });

        KeyboardHelper.hide(getActivity(), mEditSearch);
    }

    // EditText TextWatcher
    private TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
            searchSchool(s.toString());
            swapItems();
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {
        int schoolID = mAdapter.getCursor().getInt(0);
        ((CreateCourseActivity) getActivity()).setSchool(BTTable.SchoolTable.get(schoolID));
        ((CreateCourseActivity) getActivity()).getSupportActionBar().collapseActionView();
        getActivity().onBackPressed();
    }

    private void loadSchool(int page) {
        if (getBTService() != null)
            getBTService().schools(page, this);
    }

    private void searchSchool(String query) {
        if (getBTService() != null)
            getBTService().searchSchool(query, this);
    }

    @Override
    public void success(SchoolJson[] schoolJsons, Response response) {
        swapItems();
    }

    @Override
    public void failure(RetrofitError error) {
    }


    public class EndlessScrollListener implements AbsListView.OnScrollListener {

        private int visibleThreshold = 5;
        private int currentPage = 1;
        private int previousTotal = 0;
        private boolean loading = true;

        public EndlessScrollListener() {
        }

        public void resetPage() {
            this.currentPage = 1;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                    currentPage++;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                loadSchool(currentPage + 1);
                loading = true;
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    }
}

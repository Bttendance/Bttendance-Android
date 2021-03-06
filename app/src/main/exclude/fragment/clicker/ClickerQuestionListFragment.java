package com.bttendance.fragment.clicker;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.bttendance.R;
import com.bttendance.adapter.QuestionAdapter;
import com.bttendance.event.AddFragmentEvent;
import com.bttendance.fragment.BTFragment;
import com.bttendance.helper.DipPixelHelper;
import com.bttendance.model.BTKey;
import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.cursor.QuestionCursor;
import com.bttendance.model.json.QuestionJson;
import com.bttendance.model.json.QuestionJsonArray;
import com.bttendance.model.json.UserJson;
import com.squareup.otto.BTEventBus;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2014. 8. 20..
 */
public class ClickerQuestionListFragment extends BTFragment implements AdapterView.OnItemClickListener {

    ListView mListView;
    QuestionAdapter mAdapter;
    UserJson mUser;
    boolean mForProfile;

    QuestionChosenListener mListener;

    public interface QuestionChosenListener {
        public void OnQuestionChosen(QuestionJson question);
    }

    public void setOnQuestionChosenListener(QuestionChosenListener listener) {
        mListener = listener;
    }

    /**
     * Action Bar Menu
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        mUser = BTPreference.getUser(getActivity());
        mForProfile = getArguments() != null ? getArguments().getBoolean(BTKey.EXTRA_FOR_PROFILE) : false;

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
        actionBar.setTitle(getString(R.string.clicker_questions));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
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
        View view = inflater.inflate(R.layout.fragment_clicker_question_list, container, false);
        mListView = (ListView) view.findViewById(android.R.id.list);
        View paddingTop = new View(getActivity());
        paddingTop.setMinimumHeight((int) DipPixelHelper.getPixel(getActivity(), 7));
        mListView.addHeaderView(paddingTop);
        View paddingBottom = new View(getActivity());
        paddingBottom.setMinimumHeight((int) DipPixelHelper.getPixel(getActivity(), 7));
        mListView.addFooterView(paddingBottom);
        mAdapter = new QuestionAdapter(getActivity(), null);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        Button addQuestionBt = (Button) view.findViewById(R.id.add_question_bt);
        if (!mForProfile)
            addQuestionBt.setVisibility(View.GONE);
        addQuestionBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickerCRUDFragment fragment = new ClickerCRUDFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(BTKey.EXTRA_TYPE, ClickerCRUDFragment.ClickerType.QUESTION_CREATE);
                fragment.setArguments(bundle);
                BTEventBus.getInstance().post(new AddFragmentEvent(fragment));
            }
        });

        swapCursor();
        return view;
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        getQuestion();
        swapCursor();
    }

    public void getQuestion() {
        if (getBTService() == null)
            return;

        getBTService().myQuestions(new Callback<QuestionJson[]>() {
            @Override
            public void success(QuestionJson[] questionJsons, Response response) {
                swapCursor();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
            }
        });
    }

    private void swapCursor() {
        if (this.isAdded() && mAdapter != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (BTTable.MyQuestionTable.size() == 0 && mUser.questions_count != 0) {
                        QuestionJsonArray questionJsonArray = BTPreference.getMyQuestions(getActivity());
                        if (questionJsonArray != null)
                            for (QuestionJson question : questionJsonArray.questions)
                                BTTable.MyQuestionTable.append(question.id, question);
                    }
                    mAdapter.swapCursor(new QuestionCursor(BTTable.MyQuestionTable));
                }
            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        if (view == null || view.getTag(R.id.question_id) == null)
            return;

        int tag = (Integer) view.getTag(R.id.question_id);
        QuestionJson question = BTTable.MyQuestionTable.get(tag);
        if (question == null)
            return;

        if (mForProfile) {
            ClickerCRUDFragment fragment = new ClickerCRUDFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(BTKey.EXTRA_TYPE, ClickerCRUDFragment.ClickerType.QUESTION_EDIT);
            bundle.putInt(BTKey.EXTRA_QUESTION_ID, question.id);
            fragment.setArguments(bundle);
            BTEventBus.getInstance().post(new AddFragmentEvent(fragment));
        } else if (mListener != null) {
            mListener.OnQuestionChosen(question);
            getActivity().onBackPressed();
        }
    }
}

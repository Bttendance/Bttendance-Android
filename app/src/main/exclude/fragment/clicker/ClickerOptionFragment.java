package com.bttendance.fragment.clicker;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bttendance.R;
import com.bttendance.fragment.BTFragment;
import com.bttendance.model.BTKey;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by TheFinestArtist on 2014. 1. 27..
 */
public class ClickerOptionFragment extends BTFragment implements View.OnClickListener {

    public enum OptionType {DEFAULT, CLICKER, QUESTION}

    private OptionType mType;
    private int mProgressTime;
    private boolean mShowInfoOnSelect;
    private String mDetailPrivacy;

    @InjectView(R.id.clicker_option_detail_privacy_option_1_selector)
    View mDetailPrivacyOption1Selector;
    @InjectView(R.id.clicker_option_detail_privacy_option_1_check)
    View mDetailPrivacyOption1Check;
    @InjectView(R.id.clicker_option_detail_privacy_option_2_selector)
    View mDetailPrivacyOption2Selector;
    @InjectView(R.id.clicker_option_detail_privacy_option_2_check)
    View mDetailPrivacyOption2Check;
    @InjectView(R.id.clicker_option_detail_privacy_option_3_selector)
    View mDetailPrivacyOption3Selector;
    @InjectView(R.id.clicker_option_detail_privacy_option_3_check)
    View mDetailPrivacyOption3Check;

    @InjectView(R.id.clicker_option_show_info_on_select_option_1_selector)
    View mShowInfoOnSelectOption1Selector;
    @InjectView(R.id.clicker_option_show_info_on_select_option_1_check)
    View mShowInfoOnSelectOption1Check;
    @InjectView(R.id.clicker_option_show_info_on_select_option_2_selector)
    View mShowInfoOnSelectOption2Selector;
    @InjectView(R.id.clicker_option_show_info_on_select_option_2_check)
    View mShowInfoOnSelectOption2Check;

    @InjectView(R.id.clicker_option_progress_time_option_1_selector)
    View mProgressTimeOption1Selector;
    @InjectView(R.id.clicker_option_progress_time_option_1_check)
    View mProgressTimeOption1Check;
    @InjectView(R.id.clicker_option_progress_time_option_2_selector)
    View mProgressTimeOption2Selector;
    @InjectView(R.id.clicker_option_progress_time_option_2_check)
    View mProgressTimeOption2Check;
    @InjectView(R.id.clicker_option_progress_time_option_3_selector)
    View mProgressTimeOption3Selector;
    @InjectView(R.id.clicker_option_progress_time_option_3_check)
    View mProgressTimeOption3Check;
    @InjectView(R.id.clicker_option_progress_time_option_4_selector)
    View mProgressTimeOption4Selector;
    @InjectView(R.id.clicker_option_progress_time_option_4_check)
    View mProgressTimeOption4Check;

    OptionChosenListener mListener;

    public interface OptionChosenListener {
        public void OnOptionChosen(int progressTime, boolean showInfoOnSelect, String detailPrivacy);
    }

    public void setOnOptionChosenListener(OptionChosenListener listener) {
        mListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mType = getArguments() != null ? (OptionType) getArguments().getSerializable(BTKey.EXTRA_TYPE) : null;
        mProgressTime = getArguments() != null ? getArguments().getInt(BTKey.EXTRA_PROGRESS_TIME) : 60;
        mShowInfoOnSelect = getArguments() == null || getArguments().getBoolean(BTKey.EXTRA_SHOW_INFO_ON_SELECT);
        mDetailPrivacy = getArguments() != null ? getArguments().getString(BTKey.EXTRA_DETAIL_PRIVACY) : "professor";
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clicker_option, container, false);
        ButterKnife.inject(this, view);

        mDetailPrivacyOption1Selector.setOnClickListener(this);
        mDetailPrivacyOption2Selector.setOnClickListener(this);
        mDetailPrivacyOption3Selector.setOnClickListener(this);
        mShowInfoOnSelectOption1Selector.setOnClickListener(this);
        mShowInfoOnSelectOption2Selector.setOnClickListener(this);
        mProgressTimeOption1Selector.setOnClickListener(this);
        mProgressTimeOption2Selector.setOnClickListener(this);
        mProgressTimeOption3Selector.setOnClickListener(this);
        mProgressTimeOption4Selector.setOnClickListener(this);

        refreshOptions();
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clicker_option_detail_privacy_option_1_selector:
                mDetailPrivacy = "all";
                break;
            case R.id.clicker_option_detail_privacy_option_2_selector:
                mDetailPrivacy = "professor";
                break;
            case R.id.clicker_option_detail_privacy_option_3_selector:
                mDetailPrivacy = "none";
                break;
            case R.id.clicker_option_show_info_on_select_option_1_selector:
                mShowInfoOnSelect = true;
                break;
            case R.id.clicker_option_show_info_on_select_option_2_selector:
                mShowInfoOnSelect = false;
                break;
            case R.id.clicker_option_progress_time_option_1_selector:
                mProgressTime = 60;
                break;
            case R.id.clicker_option_progress_time_option_2_selector:
                mProgressTime = 120;
                break;
            case R.id.clicker_option_progress_time_option_3_selector:
                mProgressTime = 180;
                break;
            case R.id.clicker_option_progress_time_option_4_selector:
                mProgressTime = 300;
                break;
        }
        refreshOptions();
        if (mListener != null)
            mListener.OnOptionChosen(mProgressTime, mShowInfoOnSelect, mDetailPrivacy);
    }

    private void refreshOptions() {
        mDetailPrivacyOption1Check.setVisibility(View.GONE);
        mDetailPrivacyOption2Check.setVisibility(View.GONE);
        mDetailPrivacyOption3Check.setVisibility(View.GONE);
        mShowInfoOnSelectOption1Check.setVisibility(View.GONE);
        mShowInfoOnSelectOption2Check.setVisibility(View.GONE);
        mProgressTimeOption1Check.setVisibility(View.GONE);
        mProgressTimeOption2Check.setVisibility(View.GONE);
        mProgressTimeOption3Check.setVisibility(View.GONE);
        mProgressTimeOption4Check.setVisibility(View.GONE);

        if ("all".equals(mDetailPrivacy))
            mDetailPrivacyOption1Check.setVisibility(View.VISIBLE);
        else if ("none".equals(mDetailPrivacy))
            mDetailPrivacyOption3Check.setVisibility(View.VISIBLE);
        else
            mDetailPrivacyOption2Check.setVisibility(View.VISIBLE);

        if (mShowInfoOnSelect)
            mShowInfoOnSelectOption1Check.setVisibility(View.VISIBLE);
        else
            mShowInfoOnSelectOption2Check.setVisibility(View.VISIBLE);

        if (mProgressTime == 300)
            mProgressTimeOption4Check.setVisibility(View.VISIBLE);
        else if (mProgressTime == 180)
            mProgressTimeOption3Check.setVisibility(View.VISIBLE);
        else if (mProgressTime == 120)
            mProgressTimeOption2Check.setVisibility(View.VISIBLE);
        else
            mProgressTimeOption1Check.setVisibility(View.VISIBLE);
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

        switch (mType) {
            case DEFAULT:
                actionBar.setTitle(getString(R.string.clicker_default_option));
                break;
            case CLICKER:
            case QUESTION:
                actionBar.setTitle(getString(R.string.clicker_option));
                break;
        }
    }
}

package com.bttendance.fragment.clicker;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.bttendance.R;
import com.bttendance.event.AddFragmentEvent;
import com.bttendance.event.dialog.HideProgressDialogEvent;
import com.bttendance.event.dialog.ShowAlertDialogEvent;
import com.bttendance.event.dialog.ShowProgressDialogEvent;
import com.bttendance.fragment.BTDialogFragment;
import com.bttendance.fragment.BTFragment;
import com.bttendance.helper.DateHelper;
import com.bttendance.helper.KeyboardHelper;
import com.bttendance.model.BTKey;
import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.PostJson;
import com.bttendance.model.json.QuestionJson;
import com.bttendance.model.json.UserJson;
import com.squareup.otto.BTEventBus;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2014. 1. 27..
 */
public class ClickerCRUDFragment extends BTFragment {

    public enum ClickerType {CLICKER_CREATE, CLICKER_EDIT, QUESTION_CREATE, QUESTION_EDIT}

    private ClickerType mType;
    private int mCourseID;
    private QuestionJson mQuestion;
    private PostJson mPost;
    private UserJson mUser;

    private String mMessage;
    private int mChoiceCount;
    private int mProgressTime;
    private boolean mShowInfoOnSelect;
    private String mDetailPrivacy;

    @InjectView(R.id.clicker_start_guide)
    TextView mGuide;
    @InjectView(R.id.message_edit)
    EditText mEditText;
    @InjectView(R.id.message_edit_error)
    View mEditTextError;
    @InjectView(R.id.clicker_start_info)
    TextView mInfoView;
    @InjectView(R.id.choice_2_image)
    View mChoice2Img;
    @InjectView(R.id.choice_3_image)
    View mChoice3Img;
    @InjectView(R.id.choice_4_image)
    View mChoice4Img;
    @InjectView(R.id.choice_5_image)
    View mChoice5Img;

    @InjectView(R.id.clicker_option_layout)
    View mClickerOptionLayout;
    @InjectView(R.id.clicker_option_selector)
    View mClickerOptionSelector;

    @InjectView(R.id.more_option_padding)
    View mMoreOptionPadding;
    @InjectView(R.id.more_option_layout)
    View mMoreOptionLayout;
    @InjectView(R.id.more_option_selector)
    View mMoreOptionSelector;
    @InjectView(R.id.more_option_text)
    TextView mMoreOptionText;

    private boolean mShowKeyboard;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mType = getArguments() != null ? (ClickerType) getArguments().getSerializable(BTKey.EXTRA_TYPE) : null;

        mCourseID = getArguments() != null ? getArguments().getInt(BTKey.EXTRA_COURSE_ID) : 0;
        int questionID = getArguments() != null ? getArguments().getInt(BTKey.EXTRA_QUESTION_ID) : 0;
        int postID = getArguments() != null ? getArguments().getInt(BTKey.EXTRA_POST_ID) : 0;

        mQuestion = BTTable.MyQuestionTable.get(questionID);
        mPost = BTTable.PostTable.get(postID);

        switch (mType) {
            case CLICKER_CREATE:
            case QUESTION_CREATE:
                mUser = BTPreference.getUser(getActivity());
                mMessage = "";
                mChoiceCount = 0;
                mProgressTime = mUser.setting.progress_time;
                mShowInfoOnSelect = mUser.setting.show_info_on_select;
                mDetailPrivacy = mUser.setting.detail_privacy;
                break;
            case CLICKER_EDIT:
                mMessage = mPost.message;
                mChoiceCount = mPost.clicker.choice_count;
                mProgressTime = mPost.clicker.progress_time;
                mShowInfoOnSelect = mPost.clicker.show_info_on_select;
                mDetailPrivacy = mPost.clicker.detail_privacy;
                break;
            case QUESTION_EDIT:
                mMessage = mQuestion.message;
                mChoiceCount = mQuestion.choice_count;
                mProgressTime = mQuestion.progress_time;
                mShowInfoOnSelect = mQuestion.show_info_on_select;
                mDetailPrivacy = mQuestion.detail_privacy;
                break;
        }

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mShowKeyboard = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        KeyboardHelper.hide(getActivity(), mEditText);
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();

        if (mType != ClickerType.QUESTION_EDIT && mShowKeyboard) {
            mEditText.setText(mMessage);
            KeyboardHelper.show(getActivity(), mEditText);
            mEditText.setSelection(mEditText.getText().length(), mEditText.getText().length());
            mShowKeyboard = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clicker_crud, container, false);
        ButterKnife.inject(this, view);

        if (mType == ClickerType.CLICKER_CREATE)
            mEditText.setHint(String.format(getString(R.string.clicker_hint), DateHelper.getCurrentTimeString()));

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                mMessage = charSequence.toString();
                mEditTextError.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mChoice2Img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChoiceCount = 2;
                refreshView();
                KeyboardHelper.hide(getActivity(), mEditText);
            }
        });

        mChoice3Img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChoiceCount = 3;
                refreshView();
                KeyboardHelper.hide(getActivity(), mEditText);
            }
        });

        mChoice4Img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChoiceCount = 4;
                refreshView();
                KeyboardHelper.hide(getActivity(), mEditText);
            }
        });

        mChoice5Img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChoiceCount = 5;
                refreshView();
                KeyboardHelper.hide(getActivity(), mEditText);
            }
        });

        switch (mType) {
            case CLICKER_CREATE:
                mMoreOptionPadding.setVisibility(View.GONE);
                mMoreOptionSelector.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        KeyboardHelper.hide(getActivity(), mEditText);

                        final ClickerQuestionListFragment fragment = new ClickerQuestionListFragment();
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(BTKey.EXTRA_FOR_PROFILE, false);
                        fragment.setArguments(bundle);
                        fragment.setOnQuestionChosenListener(new ClickerQuestionListFragment.QuestionChosenListener() {
                            @Override
                            public void OnQuestionChosen(QuestionJson question) {
                                mMessage = question.message;
                                mChoiceCount = question.choice_count;
                                mProgressTime = question.progress_time;
                                mShowInfoOnSelect = question.show_info_on_select;
                                mDetailPrivacy = question.detail_privacy;
                                refreshView();
                            }
                        });

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                BTEventBus.getInstance().post(new AddFragmentEvent(fragment));
                            }
                        }, 500);
                    }
                });
                mMoreOptionText.setText(getString(R.string.load_saved_question));
                break;
            case CLICKER_EDIT:
                mChoice2Img.setOnClickListener(null);
                mChoice3Img.setOnClickListener(null);
                mChoice4Img.setOnClickListener(null);
                mChoice5Img.setOnClickListener(null);
                mClickerOptionLayout.setVisibility(View.GONE);
                mMoreOptionPadding.setVisibility(View.GONE);
                mMoreOptionLayout.setVisibility(View.GONE);
                break;
            case QUESTION_CREATE:
                mMoreOptionPadding.setVisibility(View.GONE);
                mMoreOptionLayout.setVisibility(View.GONE);
                break;
            case QUESTION_EDIT:
                mMoreOptionSelector.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String title = getString(R.string.delete_question);
                        String message = getString(R.string.delete_question_message);
                        BTEventBus.getInstance().post(new ShowAlertDialogEvent(BTDialogFragment.DialogType.CONFIRM, title, message, new BTDialogFragment.OnDialogListener() {
                            @Override
                            public void onConfirmed(String edit) {
                                BTEventBus.getInstance().post(new ShowProgressDialogEvent(getString(R.string.deleting_question)));
                                getBTService().removeQuestion(mQuestion.id, new Callback<QuestionJson>() {
                                    @Override
                                    public void success(QuestionJson questionJson, Response response) {
                                        BTEventBus.getInstance().post(new HideProgressDialogEvent());
                                        getActivity().onBackPressed();
                                    }

                                    @Override
                                    public void failure(RetrofitError retrofitError) {
                                        BTEventBus.getInstance().post(new HideProgressDialogEvent());
                                    }
                                });
                            }

                            @Override
                            public void onCanceled() {
                            }
                        }));
                    }
                });
                mMoreOptionText.setText(getString(R.string.delete_question));
                mMoreOptionText.setTextColor(getResources().getColor(R.color.bttendance_red));
                break;
        }

        mClickerOptionSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyboardHelper.hide(getActivity(), mEditText);

                final ClickerOptionFragment fragment = new ClickerOptionFragment();
                Bundle bundle = new Bundle();
                switch (mType) {
                    case CLICKER_CREATE:
                    case CLICKER_EDIT:
                        bundle.putSerializable(BTKey.EXTRA_TYPE, ClickerOptionFragment.OptionType.CLICKER);
                        break;
                    case QUESTION_CREATE:
                    case QUESTION_EDIT:
                        bundle.putSerializable(BTKey.EXTRA_TYPE, ClickerOptionFragment.OptionType.QUESTION);
                        break;
                }
                bundle.putInt(BTKey.EXTRA_PROGRESS_TIME, mProgressTime);
                bundle.putBoolean(BTKey.EXTRA_SHOW_INFO_ON_SELECT, mShowInfoOnSelect);
                bundle.putString(BTKey.EXTRA_DETAIL_PRIVACY, mDetailPrivacy);
                fragment.setArguments(bundle);
                fragment.setOnOptionChosenListener(new ClickerOptionFragment.OptionChosenListener() {
                    @Override
                    public void OnOptionChosen(int progressTime, boolean showInfoOnSelect, String detailPrivacy) {
                        mProgressTime = progressTime;
                        mShowInfoOnSelect = showInfoOnSelect;
                        mDetailPrivacy = detailPrivacy;
                        refreshView();
                    }
                });

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BTEventBus.getInstance().post(new AddFragmentEvent(fragment));
                    }
                }, 500);
            }
        });

        refreshView();
        return view;
    }

    private void refreshView() {

        String guideText;
        if ("all".equals(mDetailPrivacy))
            guideText = getString(R.string.clicker_guide_detail_privacy_all);
        else if ("none".equals(mDetailPrivacy))
            guideText = getString(R.string.clicker_guide_detail_privacy_none);
        else
            guideText = getString(R.string.clicker_guide_detail_privacy_professor);

        if (mShowInfoOnSelect)
            guideText = guideText + "<br>" + getString(R.string.clicker_guide_show_info_on_select_true);
        else
            guideText = guideText + "<br>" + getString(R.string.clicker_guide_show_info_on_select_false);

        guideText = guideText + "<br>" + String.format(getString(R.string.clicker_guide_progress_time), mProgressTime / 60);
        mGuide.setText(Html.fromHtml(guideText));

        mEditText.setText(mMessage);

        mInfoView.setTextColor(getResources().getColor(R.color.bttendance_silver));
        mInfoView.setBackgroundColor(getResources().getColor(R.color.bttendance_grey));

        mChoice2Img.setSelected(false);
        mChoice3Img.setSelected(false);
        mChoice4Img.setSelected(false);
        mChoice5Img.setSelected(false);

        switch (mChoiceCount) {
            case 2:
                mChoice2Img.setSelected(true);
                break;
            case 3:
                mChoice3Img.setSelected(true);
                break;
            case 4:
                mChoice4Img.setSelected(true);
                break;
            case 5:
                mChoice5Img.setSelected(true);
                break;
        }
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
            case CLICKER_CREATE:
                actionBar.setTitle(getString(R.string.start_clicker));
                inflater.inflate(R.menu.clicker_start_menu, menu);
                break;
            case CLICKER_EDIT:
                actionBar.setTitle(getString(R.string.edit_message));
                inflater.inflate(R.menu.clicker_edit_menu, menu);
                break;
            case QUESTION_CREATE:
                actionBar.setTitle(getString(R.string.add_question));
                inflater.inflate(R.menu.add_question_menu, menu);
                break;
            case QUESTION_EDIT:
                actionBar.setTitle(getString(R.string.edit_question));
                inflater.inflate(R.menu.edit_question_menu, menu);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.action_edit_message:
                if (mEditText != null && mEditText.getText().toString().length() > 0) {
                    item.setEnabled(false);
                    BTEventBus.getInstance().post(new ShowProgressDialogEvent(getString(R.string.updating_clicker)));
                    getBTService().updatePostMessage(mPost.id, mEditText.getText().toString(), new Callback<PostJson>() {
                        @Override
                        public void success(PostJson postJson, Response response) {
                            item.setEnabled(true);
                            BTEventBus.getInstance().post(new HideProgressDialogEvent());
                            if (ClickerCRUDFragment.this.getActivity() != null)
                                ClickerCRUDFragment.this.getActivity().onBackPressed();
                        }

                        @Override
                        public void failure(RetrofitError retrofitError) {
                            item.setEnabled(true);
                            BTEventBus.getInstance().post(new HideProgressDialogEvent());
                        }
                    });
                } else {
                    Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(200);
                    mEditTextError.setBackgroundColor(getResources().getColor(R.color.bttendance_red_10));
                }
                return true;
            case R.id.action_start:
                if (mEditText != null && mChoiceCount >= 2 && mChoiceCount <= 5) {
                    item.setEnabled(false);
                    String message = mEditText.getText().toString();
                    if (message == null || message.length() == 0)
                        message = mEditText.getHint().toString();
                    BTEventBus.getInstance().post(new ShowProgressDialogEvent(getString(R.string.starting_clicker)));
                    getBTService().postStartClicker(mCourseID, message, mChoiceCount, mProgressTime, mShowInfoOnSelect, mDetailPrivacy, new Callback<PostJson>() {
                        @Override
                        public void success(PostJson postJson, Response response) {
                            item.setEnabled(true);
                            BTEventBus.getInstance().post(new HideProgressDialogEvent());
                            if (ClickerCRUDFragment.this.getActivity() != null)
                                ClickerCRUDFragment.this.getActivity().onBackPressed();

                            ClickerDetailFragment frag = new ClickerDetailFragment();
                            Bundle bundle = new Bundle();
                            bundle.putInt(BTKey.EXTRA_POST_ID, postJson.id);
                            frag.setArguments(bundle);
                            BTEventBus.getInstance().post(new AddFragmentEvent(frag));
                        }

                        @Override
                        public void failure(RetrofitError retrofitError) {
                            item.setEnabled(true);
                            BTEventBus.getInstance().post(new HideProgressDialogEvent());
                        }
                    });
                } else {
                    Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(200);
                    mInfoView.setTextColor(getResources().getColor(R.color.bttendance_red));
                    mInfoView.setBackgroundColor(getResources().getColor(R.color.bttendance_red_10));
                }
                return true;
            case R.id.action_save:
                if (mEditText != null && mEditText.getText().toString().length() > 0 && mChoiceCount >= 2 && mChoiceCount <= 5) {
                    item.setEnabled(false);
                    BTEventBus.getInstance().post(new ShowProgressDialogEvent(getString(R.string.saving_question)));
                    getBTService().createQuestion(mEditText.getText().toString(), mChoiceCount, mProgressTime, mShowInfoOnSelect, mDetailPrivacy, new Callback<QuestionJson>() {
                        @Override
                        public void success(QuestionJson questionJson, Response response) {
                            item.setEnabled(true);
                            BTEventBus.getInstance().post(new HideProgressDialogEvent());
                            if (ClickerCRUDFragment.this.getActivity() != null)
                                ClickerCRUDFragment.this.getActivity().onBackPressed();
                        }

                        @Override
                        public void failure(RetrofitError retrofitError) {
                            item.setEnabled(true);
                            BTEventBus.getInstance().post(new HideProgressDialogEvent());
                        }
                    });
                } else if (mEditText != null && mEditText.getText().toString().length() > 0) {
                    Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(200);
                    mInfoView.setTextColor(getResources().getColor(R.color.bttendance_red));
                    mInfoView.setBackgroundColor(getResources().getColor(R.color.bttendance_red_10));
                } else if (mChoiceCount >= 2 && mChoiceCount <= 5) {
                    Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(200);
                    mEditTextError.setBackgroundColor(getResources().getColor(R.color.bttendance_red_10));
                } else {
                    Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(200);
                    mEditTextError.setBackgroundColor(getResources().getColor(R.color.bttendance_red_10));
                    mInfoView.setTextColor(getResources().getColor(R.color.bttendance_red));
                    mInfoView.setBackgroundColor(getResources().getColor(R.color.bttendance_red_10));
                }
                return true;
            case R.id.action_edit:
                if (mEditText != null && mEditText.getText().toString().length() > 0 && mChoiceCount >= 2 && mChoiceCount <= 5) {
                    item.setEnabled(false);
                    BTEventBus.getInstance().post(new ShowProgressDialogEvent(getString(R.string.updating_question)));
                    getBTService().editQuestion(mQuestion.id, mEditText.getText().toString(), mChoiceCount, mProgressTime, mShowInfoOnSelect, mDetailPrivacy, new Callback<QuestionJson>() {
                        @Override
                        public void success(QuestionJson questionJson, Response response) {
                            item.setEnabled(true);
                            BTEventBus.getInstance().post(new HideProgressDialogEvent());
                            if (ClickerCRUDFragment.this.getActivity() != null)
                                ClickerCRUDFragment.this.getActivity().onBackPressed();
                        }

                        @Override
                        public void failure(RetrofitError retrofitError) {
                            item.setEnabled(true);
                            BTEventBus.getInstance().post(new HideProgressDialogEvent());
                        }
                    });
                } else if (mEditText != null && mEditText.getText().toString().length() > 0) {
                    Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(200);
                    mInfoView.setTextColor(getResources().getColor(R.color.bttendance_red));
                    mInfoView.setBackgroundColor(getResources().getColor(R.color.bttendance_red_10));
                } else if (mChoiceCount >= 2 && mChoiceCount <= 5) {
                    Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(200);
                    mEditTextError.setBackgroundColor(getResources().getColor(R.color.bttendance_red_10));
                } else {
                    Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(200);
                    mEditTextError.setBackgroundColor(getResources().getColor(R.color.bttendance_red_10));
                    mInfoView.setTextColor(getResources().getColor(R.color.bttendance_red));
                    mInfoView.setBackgroundColor(getResources().getColor(R.color.bttendance_red_10));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

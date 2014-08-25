package com.bttendance.fragment.clicker;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
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
import com.bttendance.model.BTTable;
import com.bttendance.model.json.PostJson;
import com.bttendance.model.json.QuestionJson;
import com.squareup.otto.BTEventBus;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2014. 1. 27..
 */
public class ClickerStartFragment extends BTFragment {

    private int mCourseID;
    private QuestionJson mQuestion;
    private PostJson mPost;
    private EditText mMessage;
    boolean mForProfile;

    private View mChoiceView1;
    private View mChoiceView2;
    private TextView mInfoView;

    int mChoice;
    View mChoice2Img;
    View mChoice3Img;
    View mChoice4Img;
    View mChoice5Img;
    TextView mChoice2Text;
    TextView mChoice3Text;
    TextView mChoice4Text;
    TextView mChoice5Text;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mCourseID = getArguments() != null ? getArguments().getInt(BTKey.EXTRA_COURSE_ID) : 0;
        mForProfile = getArguments() != null ? getArguments().getBoolean(BTKey.EXTRA_FOR_PROFILE) : false;
        int questionID = getArguments() != null ? getArguments().getInt(BTKey.EXTRA_QUESTION_ID) : 0;
        mQuestion = BTTable.MyQuestionTable.get(questionID);
        int postID = getArguments() != null ? getArguments().getInt(BTKey.EXTRA_POST_ID) : 0;
        mPost = BTTable.PostTable.get(postID);

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        KeyboardHelper.show(getActivity(), mMessage);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        KeyboardHelper.hide(getActivity(), mMessage);
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        KeyboardHelper.show(getActivity(), mMessage);
        mMessage.setSelection(mMessage.getText().length(), mMessage.getText().length());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clicker_start, container, false);
        mMessage = (EditText) view.findViewById(R.id.message_edit);
        KeyboardHelper.show(getActivity(), mMessage);

        mChoiceView1 = view.findViewById(R.id.clicker_start_choice_1);
        mChoiceView2 = view.findViewById(R.id.clicker_start_choice_2);
        mInfoView = (TextView) view.findViewById(R.id.clicker_start_info);

        mChoice2Img = view.findViewById(R.id.choice_2_image);
        mChoice3Img = view.findViewById(R.id.choice_3_image);
        mChoice4Img = view.findViewById(R.id.choice_4_image);
        mChoice5Img = view.findViewById(R.id.choice_5_image);

        mChoice2Text = (TextView) view.findViewById(R.id.choice_2_text);
        mChoice3Text = (TextView) view.findViewById(R.id.choice_3_text);
        mChoice4Text = (TextView) view.findViewById(R.id.choice_4_text);
        mChoice5Text = (TextView) view.findViewById(R.id.choice_5_text);

        view.findViewById(R.id.choice_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChoice = 2;
                refreshChoiceView();
            }
        });

        view.findViewById(R.id.choice_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChoice = 3;
                refreshChoiceView();
            }
        });

        view.findViewById(R.id.choice_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChoice = 4;
                refreshChoiceView();
            }
        });

        view.findViewById(R.id.choice_5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChoice = 5;
                refreshChoiceView();
            }
        });

        Button loadQuestionBt = (Button) view.findViewById(R.id.load_question);
        if (mForProfile) {
            loadQuestionBt.setVisibility(View.GONE);
            view.findViewById(R.id.clicker_start_guide).setVisibility(View.GONE);
        } else {
            loadQuestionBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    KeyboardHelper.hide(getActivity(), mMessage);

                    final ClickerQuestionListFragment fragment = new ClickerQuestionListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(BTKey.EXTRA_FOR_PROFILE, false);
                    fragment.setArguments(bundle);
                    fragment.setOnQuestionChosenListener(new ClickerQuestionListFragment.QuestionChosenListener() {
                        @Override
                        public void OnQuestionChosen(QuestionJson question) {
                            mMessage.setText(question.message);
                            mChoice = question.choice_count;
                            refreshChoiceView();
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
        }

        if (mQuestion == null && mPost == null && !mForProfile) {
            mMessage.setHint(String.format(getString(R.string.clicker_hint), DateHelper.getCurrentTimeString()));
        }

        if (mQuestion != null) {
            mMessage.setText(mQuestion.message);
            mChoice = mQuestion.choice_count;
            refreshChoiceView();
            loadQuestionBt.setVisibility(View.VISIBLE);
            loadQuestionBt.setText(getString(R.string.delete));
            loadQuestionBt.setBackgroundResource(R.drawable.load_btn_bg_red);
            loadQuestionBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getBTService() == null)
                        return;

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
        }

        if (mPost != null) {
            loadQuestionBt.setVisibility(View.GONE);
            view.findViewById(R.id.clicker_start_guide).setVisibility(View.GONE);
            mInfoView.setVisibility(View.GONE);
            mMessage.setText(mPost.message);
            mChoice = mPost.clicker.choice_count;
            refreshChoiceView();
            view.findViewById(R.id.choice_2).setOnClickListener(null);
            view.findViewById(R.id.choice_3).setOnClickListener(null);
            view.findViewById(R.id.choice_4).setOnClickListener(null);
            view.findViewById(R.id.choice_5).setOnClickListener(null);
        }

        return view;
    }

    private void refreshChoiceView() {

        mChoice2Img.setSelected(false);
        mChoice3Img.setSelected(false);
        mChoice4Img.setSelected(false);
        mChoice5Img.setSelected(false);

        mChoice2Text.setTextColor(getResources().getColor(R.color.bttendance_silver));
        mChoice3Text.setTextColor(getResources().getColor(R.color.bttendance_silver));
        mChoice4Text.setTextColor(getResources().getColor(R.color.bttendance_silver));
        mChoice5Text.setTextColor(getResources().getColor(R.color.bttendance_silver));

        switch (mChoice) {
            case 2:
                mChoice2Img.setSelected(true);
                mChoice2Text.setTextColor(getResources().getColor(R.color.bttendance_navy));
                break;
            case 3:
                mChoice3Img.setSelected(true);
                mChoice3Text.setTextColor(getResources().getColor(R.color.bttendance_navy));
                break;
            case 4:
                mChoice4Img.setSelected(true);
                mChoice4Text.setTextColor(getResources().getColor(R.color.bttendance_navy));
                break;
            case 5:
                mChoice5Img.setSelected(true);
                mChoice5Text.setTextColor(getResources().getColor(R.color.bttendance_navy));
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (getSherlockActivity() == null)
            return;

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        if (mPost != null) {
            actionBar.setTitle(getString(R.string.edit_message));
            inflater.inflate(R.menu.clicker_edit_menu, menu);
        } else if (!mForProfile) {
            actionBar.setTitle(getString(R.string.start_clicker));
            inflater.inflate(R.menu.clicker_start_menu, menu);
        } else if (mQuestion == null) {
            actionBar.setTitle(getString(R.string.add_question));
            inflater.inflate(R.menu.add_question_menu, menu);
        } else {
            actionBar.setTitle(getString(R.string.edit_question));
            inflater.inflate(R.menu.edit_question_menu, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.abs__home:
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.action_edit_message:
                if (mMessage != null && mMessage.getText().toString().length() > 0) {
                    item.setEnabled(false);
                    BTEventBus.getInstance().post(new ShowProgressDialogEvent(getString(R.string.updating_clicker)));
                    getBTService().updatePostMessage(mPost.id, mMessage.getText().toString(), new Callback<PostJson>() {
                        @Override
                        public void success(PostJson postJson, Response response) {
                            item.setEnabled(true);
                            BTEventBus.getInstance().post(new HideProgressDialogEvent());
                            if (ClickerStartFragment.this.getActivity() != null)
                                ClickerStartFragment.this.getActivity().onBackPressed();
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
                    mMessage.setBackgroundColor(getResources().getColor(R.color.bttendance_red_10));
                }
                return true;
            case R.id.action_start:
                if (mMessage != null && mChoice >= 2 && mChoice <= 5) {
                    item.setEnabled(false);
                    String message = mMessage.getText().toString();
                    if (message == null || message.length() == 0)
                        message = mMessage.getHint().toString();
                    BTEventBus.getInstance().post(new ShowProgressDialogEvent(getString(R.string.starting_clicker)));
                    getBTService().postStartClicker(mCourseID, message, mChoice, new Callback<PostJson>() {
                        @Override
                        public void success(PostJson postJson, Response response) {
                            item.setEnabled(true);
                            BTEventBus.getInstance().post(new HideProgressDialogEvent());
                            if (ClickerStartFragment.this.getActivity() != null)
                                ClickerStartFragment.this.getActivity().onBackPressed();

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
                    mChoiceView1.setBackgroundColor(getResources().getColor(R.color.bttendance_red_10));
                    mChoiceView2.setBackgroundColor(getResources().getColor(R.color.bttendance_red_10));
                    mInfoView.setTextColor(getResources().getColor(R.color.bttendance_red));
                }
                return true;
            case R.id.action_save:
                if (mMessage != null && mMessage.getText().toString().length() > 0 && mChoice >= 2 && mChoice <= 5) {
                    item.setEnabled(false);
                    BTEventBus.getInstance().post(new ShowProgressDialogEvent(getString(R.string.saving_question)));
                    getBTService().createQuestion(mMessage.getText().toString(), mChoice, new Callback<QuestionJson>() {
                        @Override
                        public void success(QuestionJson questionJson, Response response) {
                            item.setEnabled(true);
                            BTEventBus.getInstance().post(new HideProgressDialogEvent());
                            if (ClickerStartFragment.this.getActivity() != null)
                                ClickerStartFragment.this.getActivity().onBackPressed();
                        }

                        @Override
                        public void failure(RetrofitError retrofitError) {
                            item.setEnabled(true);
                            BTEventBus.getInstance().post(new HideProgressDialogEvent());
                        }
                    });
                } else if (mMessage != null && mMessage.getText().toString().length() > 0) {
                    Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(200);
                    mChoiceView1.setBackgroundColor(getResources().getColor(R.color.bttendance_red_10));
                    mChoiceView2.setBackgroundColor(getResources().getColor(R.color.bttendance_red_10));
                    mInfoView.setTextColor(getResources().getColor(R.color.bttendance_red));
                } else if (mChoice >= 2 && mChoice <= 5) {
                    Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(200);
                    mMessage.setBackgroundColor(getResources().getColor(R.color.bttendance_red_10));
                } else {
                    Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(200);
                    mMessage.setBackgroundColor(getResources().getColor(R.color.bttendance_red_10));
                    mChoiceView1.setBackgroundColor(getResources().getColor(R.color.bttendance_red_10));
                    mChoiceView2.setBackgroundColor(getResources().getColor(R.color.bttendance_red_10));
                    mInfoView.setTextColor(getResources().getColor(R.color.bttendance_red));
                }
                return true;
            case R.id.action_edit:
                if (mMessage != null && mMessage.getText().toString().length() > 0 && mChoice >= 2 && mChoice <= 5) {
                    item.setEnabled(false);
                    BTEventBus.getInstance().post(new ShowProgressDialogEvent(getString(R.string.updating_question)));
                    getBTService().editQuestion(mQuestion.id, mMessage.getText().toString(), mChoice, new Callback<QuestionJson>() {
                        @Override
                        public void success(QuestionJson questionJson, Response response) {
                            item.setEnabled(true);
                            BTEventBus.getInstance().post(new HideProgressDialogEvent());
                            if (ClickerStartFragment.this.getActivity() != null)
                                ClickerStartFragment.this.getActivity().onBackPressed();
                        }

                        @Override
                        public void failure(RetrofitError retrofitError) {
                            item.setEnabled(true);
                            BTEventBus.getInstance().post(new HideProgressDialogEvent());
                        }
                    });
                } else if (mMessage != null && mMessage.getText().toString().length() > 0) {
                    Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(200);
                    mChoiceView1.setBackgroundColor(getResources().getColor(R.color.bttendance_red_10));
                    mChoiceView2.setBackgroundColor(getResources().getColor(R.color.bttendance_red_10));
                    mInfoView.setTextColor(getResources().getColor(R.color.bttendance_red));
                } else if (mChoice >= 2 && mChoice <= 5) {
                    Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(200);
                    mMessage.setBackgroundColor(getResources().getColor(R.color.bttendance_red_10));
                } else {
                    Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(200);
                    mMessage.setBackgroundColor(getResources().getColor(R.color.bttendance_red_10));
                    mChoiceView1.setBackgroundColor(getResources().getColor(R.color.bttendance_red_10));
                    mChoiceView2.setBackgroundColor(getResources().getColor(R.color.bttendance_red_10));
                    mInfoView.setTextColor(getResources().getColor(R.color.bttendance_red));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

package com.bttendance.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bttendance.R;
import com.bttendance.activity.BTActivity;
import com.bttendance.activity.guide.GuideCourseAttendActivity;
import com.bttendance.activity.guide.GuideCourseCreateActivity;
import com.bttendance.event.AddFragmentEvent;
import com.bttendance.event.clicker.ClickerClickEvent;
import com.bttendance.event.dialog.ShowAlertDialogEvent;
import com.bttendance.fragment.BTDialogFragment;
import com.bttendance.fragment.SimpleWebViewFragment;
import com.bttendance.fragment.attendance.AttendanceDetailFragment;
import com.bttendance.fragment.clicker.ClickerDetailFragment;
import com.bttendance.fragment.notice.NoticeDetailFragment;
import com.bttendance.helper.DateHelper;
import com.bttendance.helper.PackagesHelper;
import com.bttendance.helper.ScreenHelper;
import com.bttendance.model.BTKey;
import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.BTUrl;
import com.bttendance.model.json.AttendanceJson;
import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.PostJson;
import com.bttendance.model.json.UserJson;
import com.bttendance.view.Bttendance;
import com.bttendance.view.Clicker;
import com.bttendance.view.TimeredTextView;
import com.squareup.otto.BTEventBus;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;

import static com.bttendance.helper.DipPixelHelper.getPixel;

/**
 * Created by TheFinestArtist on 2013. 12. 3..
 */
public class FeedAdapter extends CursorAdapter implements View.OnClickListener {

    Context mContext;
    int mCourseID;
    UserJson mUser;
    boolean mAuth;
    CourseJson mCourse;

    public enum Type {UPDATE, TIPS, GUIDE_CLICKER, GUIDE_ATTENDANCE, GUIDE_NOTICE, CLICKER, ATTENDANCE, NOTICE, CHOICE}

    private Type getType(Cursor cursor) {
        int id = cursor.getInt(0);
        if (id < 0) {
            switch (id) {
                case -1:
                    return Type.TIPS;
                case -2:
                    return Type.GUIDE_CLICKER;
                case -3:
                    return Type.GUIDE_ATTENDANCE;
                case -4:
                    return Type.GUIDE_NOTICE;
            }
        } else {
            PostJson post = BTTable.PostTable.get(id);
            if ("clicker".equals(post.type)) {
                if (!mAuth && post.clicker.getChoiceInt(mUser.id) == 6 && (post.clicker.progress_time + 5) * 1000 - System.currentTimeMillis() + DateHelper.getTime(post.createdAt) > 0)
                    return Type.CHOICE;
                return Type.CLICKER;
            }

            if ("attendance".equals(post.type))
                return Type.ATTENDANCE;

            if ("notice".equals(post.type))
                return Type.NOTICE;
        }
        return Type.UPDATE;
    }

    public FeedAdapter(Context context, Cursor cursor, int courseID) {
        super(context, cursor, android.widget.CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        mContext = context;
        mCourseID = courseID;
        mUser = BTPreference.getUser(context);
        mAuth = mUser.supervising(mCourseID);
        mCourse = BTTable.MyCourseTable.get(mCourseID);
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        mCourse = BTTable.MyCourseTable.get(mCourseID);
        return super.swapCursor(newCursor);
    }

    @Override
    public long getItemId(int position) {
        Cursor cursor = (Cursor) getItem(position);
        if (cursor == null)
            return -1;

        int id = cursor.getInt(0);
        return id;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        Cursor cursor = (Cursor) getItem(position);

        switch (getType(cursor)) {
            case CLICKER:
            case ATTENDANCE:
            case NOTICE:
                return 0;
            case CHOICE:
                return 1;
            case UPDATE:
            case TIPS:
            case GUIDE_CLICKER:
            case GUIDE_ATTENDANCE:
            case GUIDE_NOTICE:
            default:
                return 2;
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);

        switch (getType(cursor)) {
            case CLICKER:
            case ATTENDANCE:
            case NOTICE:
                return inflater.inflate(R.layout.feed_item, null);
            case CHOICE:
                return inflater.inflate(R.layout.choice_item, null);
            case UPDATE:
            case TIPS:
            case GUIDE_CLICKER:
            case GUIDE_ATTENDANCE:
            case GUIDE_NOTICE:
            default:
                return inflater.inflate(R.layout.guide_item, null);
        }
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        switch (getType(cursor)) {
            case UPDATE:
                drawGuide(view, context, Type.UPDATE);
                break;
            case TIPS:
                drawGuide(view, context, Type.TIPS);
                break;
            case GUIDE_CLICKER:
                drawGuide(view, context, Type.GUIDE_CLICKER);
                break;
            case GUIDE_ATTENDANCE:
                drawGuide(view, context, Type.GUIDE_ATTENDANCE);
                break;
            case GUIDE_NOTICE:
                drawGuide(view, context, Type.GUIDE_NOTICE);
                break;
            case CLICKER: {
                PostJson post = BTTable.PostTable.get(cursor.getInt(0));
                drawClicker(view, context, post);
                break;
            }
            case ATTENDANCE: {
                PostJson post = BTTable.PostTable.get(cursor.getInt(0));
                drawAttendance(view, context, post);
                break;
            }
            case NOTICE: {
                PostJson post = BTTable.PostTable.get(cursor.getInt(0));
                drawNotice(view, context, post);
                break;
            }
            case CHOICE: {
                PostJson post = BTTable.PostTable.get(cursor.getInt(0));
                drawChoice(view, context, post);
                break;
            }
        }
    }

    private void drawChoice(View view, Context context, PostJson post) {

        View margin_a = view.findViewById(R.id.margin_a);
        Clicker choice_a = (Clicker) view.findViewById(R.id.choice_a);
        View margin_b = view.findViewById(R.id.margin_b);
        Clicker choice_b = (Clicker) view.findViewById(R.id.choice_b);
        View margin_c = view.findViewById(R.id.margin_c);
        Clicker choice_c = (Clicker) view.findViewById(R.id.choice_c);
        View margin_d = view.findViewById(R.id.margin_d);
        Clicker choice_d = (Clicker) view.findViewById(R.id.choice_d);
        View margin_e = view.findViewById(R.id.margin_e);
        Clicker choice_e = (Clicker) view.findViewById(R.id.choice_e);

        margin_a.setVisibility(View.VISIBLE);
        margin_b.setVisibility(View.VISIBLE);
        margin_c.setVisibility(View.VISIBLE);
        margin_d.setVisibility(View.VISIBLE);
        margin_e.setVisibility(View.VISIBLE);

        choice_a.setVisibility(View.VISIBLE);
        choice_b.setVisibility(View.VISIBLE);
        choice_c.setVisibility(View.VISIBLE);
        choice_d.setVisibility(View.VISIBLE);
        choice_e.setVisibility(View.VISIBLE);

        choice_a.setTag(R.id.post_id, post.id);
        choice_b.setTag(R.id.post_id, post.id);
        choice_c.setTag(R.id.post_id, post.id);
        choice_d.setTag(R.id.post_id, post.id);
        choice_e.setTag(R.id.post_id, post.id);

        choice_a.setOnClickListener(this);
        choice_b.setOnClickListener(this);
        choice_c.setOnClickListener(this);
        choice_d.setOnClickListener(this);
        choice_e.setOnClickListener(this);

        choice_a.setClickable(true);
        choice_b.setClickable(true);
        choice_c.setClickable(true);
        choice_d.setClickable(true);
        choice_e.setClickable(true);

        long currentTime = System.currentTimeMillis();
        int progressTime = (post.clicker.progress_time + 5) * 1000;
        int progress = (int) (100.0f * (float) (progressTime - currentTime + DateHelper.getTime(post.createdAt)) / (float) progressTime);
        choice_a.startClicker(progress, progressTime);
        choice_b.startClicker(progress, progressTime);
        choice_c.startClicker(progress, progressTime);
        choice_d.startClicker(progress, progressTime);
        choice_e.startClicker(progress, progressTime);

        switch (post.clicker.choice_count) {
            case 2:
                margin_c.setVisibility(View.GONE);
                choice_c.setVisibility(View.GONE);
            case 3:
                margin_d.setVisibility(View.GONE);
                choice_d.setVisibility(View.GONE);
            case 4:
                margin_e.setVisibility(View.GONE);
                choice_e.setVisibility(View.GONE);
            case 5:
                break;
        }

        // Title, Message, Time
        TimeredTextView title = (TimeredTextView) view.findViewById(R.id.choice_title);
        TextView message = (TextView) view.findViewById(R.id.choice_message);
        TextView guide = (TextView) view.findViewById(R.id.clicker_option_guide);
        TextView time = (TextView) view.findViewById(R.id.time);

        title.setVisibility(View.VISIBLE);
        message.setVisibility(View.VISIBLE);
        time.setVisibility(View.VISIBLE);

        title.setTextColor(context.getResources().getColor(R.color.bttendance_silver));
        title.setText(context.getString(R.string.clicker));
        message.setText(post.message);

        String guideText;
        if ("all".equals(post.clicker.detail_privacy))
            guideText = mContext.getString(R.string.clicker_guide_detail_privacy_all);
        else if ("none".equals(post.clicker.detail_privacy))
            guideText = mContext.getString(R.string.clicker_guide_detail_privacy_none);
        else
            guideText = mContext.getString(R.string.clicker_guide_detail_privacy_professor);

        guide.setText(Html.fromHtml(guideText));
        time.setText(DateHelper.getPostFormatString(post.createdAt));
        title.setTimeredTextView(TimeredTextView.Type.Clicker, post.id, mUser.id, mAuth);
    }

    private void drawClicker(View view, Context context, PostJson post) {

        RelativeLayout clicker = (RelativeLayout) view.findViewById(R.id.clicker);
        Bttendance bttendance = ((Bttendance) view.findViewById(R.id.bttendance));
        View notice = view.findViewById(R.id.notice);

        if (clicker == null)
            return;

        clicker.setVisibility(View.VISIBLE);
        bttendance.setVisibility(View.GONE);
        notice.setVisibility(View.GONE);

        DefaultRenderer renderer = post.clicker.getRenderer(context);
        GraphicalView chartView;
        if (!mAuth && !post.clicker.show_info_on_select && (post.clicker.progress_time + 5) * 1000 - System.currentTimeMillis() + DateHelper.getTime(post.createdAt) > 0)
            chartView = ChartFactory.getPieChartView(context, post.clicker.getEmptySeries(), renderer);
        else
            chartView = ChartFactory.getPieChartView(context, post.clicker.getSeries(), renderer);
        clicker.addView(chartView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        View ring = new View(context);
        ring.setBackgroundResource(R.drawable.ic_clicker_ring);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) getPixel(context, 52), (int) getPixel(context, 52));
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        clicker.addView(ring, params);

        // Title, Message, Time
        TimeredTextView title = (TimeredTextView) view.findViewById(R.id.timered_title);
        TextView message = (TextView) view.findViewById(R.id.message);
        TextView time = (TextView) view.findViewById(R.id.time);

        title.setVisibility(View.VISIBLE);
        message.setVisibility(View.VISIBLE);
        time.setVisibility(View.VISIBLE);

        title.setTextColor(context.getResources().getColor(R.color.bttendance_silver));
        title.setText(context.getString(R.string.clicker));
        if (!mAuth && !post.clicker.show_info_on_select && (post.clicker.progress_time + 5) * 1000 - System.currentTimeMillis() + DateHelper.getTime(post.createdAt) > 0)
            message.setText(post.message + "\n" + context.getString(R.string.clicker_show_info_on_selelct_guide));
        else
            message.setText(post.message + "\n" + post.clicker.getDetail());
        time.setText(DateHelper.getPostFormatString(post.createdAt));

        title.setTimeredTextView(TimeredTextView.Type.Clicker, post.id, mUser.id, mAuth);

        // Selector Events
        View selector = view.findViewById(R.id.item_selector);
        selector.setTag(R.id.post_id, post.id);
        selector.setOnClickListener(this);
    }

    private void drawAttendance(View view, Context context, PostJson post) {

        RelativeLayout clicker = (RelativeLayout) view.findViewById(R.id.clicker);
        Bttendance bttendance = ((Bttendance) view.findViewById(R.id.bttendance));
        View notice = view.findViewById(R.id.notice);

        clicker.setVisibility(View.GONE);
        bttendance.setVisibility(View.VISIBLE);
        notice.setVisibility(View.GONE);

        // Title, Message, Time
        TimeredTextView title = (TimeredTextView) view.findViewById(R.id.timered_title);
        TextView message = (TextView) view.findViewById(R.id.message);
        TextView time = (TextView) view.findViewById(R.id.time);

        title.setVisibility(View.VISIBLE);
        message.setVisibility(View.VISIBLE);
        time.setVisibility(View.VISIBLE);

        title.setTextColor(context.getResources().getColor(R.color.bttendance_silver));
        title.setText(context.getString(R.string.attendance));
        time.setText(DateHelper.getPostFormatString(post.createdAt));

        title.setTimeredTextView(TimeredTextView.Type.Attendance, post.id, mUser.id, mAuth);

        long currentTime = DateHelper.getCurrentGMTTimeMillis();

        String message1 = context.getString(R.string.attendance_message_present);
        String message2 = context.getString(R.string.attendance_message_absent);
        String message3 = context.getString(R.string.attendance_message_tardy);
        String message4 = context.getString(R.string.attendance_message_ongoing);

        if (mAuth) {
            int totalStudents = 0;
            if (mCourse != null)
                totalStudents = mCourse.students_count;
            int rate = 0;
            if (totalStudents != 0)
                rate = Math.round((float) post.attendance.getAttendedCount() / (float) totalStudents * 100.0f);
            bttendance.setBttendance(Bttendance.STATE.GRADE, rate);
            String message0 = context.getString(R.string.attendance_message_post_prof);
            message.setText(String.format(message0, post.attendance.getAttendedCount(), totalStudents, rate));
        } else {
            if (post.attendance.getStateInt(mUser.id) == 0) {
                if (AttendanceJson.TYPE_AUTO.equals(post.attendance.type)
                        && Bttendance.PROGRESS_DURATION - System.currentTimeMillis() + DateHelper.getTime(post.createdAt) > 0) {
                    long timeLeft = currentTime - DateHelper.getTime(post.createdAt);
                    int progress = (int) (100.0f * (float) (Bttendance.PROGRESS_DURATION - timeLeft) / (float) Bttendance.PROGRESS_DURATION);
                    bttendance.setBttendance(Bttendance.STATE.CHECKING, progress);
                    message.setText(message4);
                } else {
                    bttendance.setBttendance(Bttendance.STATE.ABSENT, 0);
                    message.setText(message2);
                }
            } else if (post.attendance.getStateInt(mUser.id) == 1) {
                bttendance.setBttendance(Bttendance.STATE.PRESENT, 0);
                message.setText(message1);
            } else {
                bttendance.setBttendance(Bttendance.STATE.TARDY, 0);
                message.setText(message3);
            }
        }

        // Attendance Icon Param
        Rect bounds = new Rect();
        Paint paint = new Paint();
        paint.setTextSize(12.0f);
        paint.getTextBounds(post.message, 0, post.message.length(), bounds);
        float width = getPixel(context, (float) Math.ceil(bounds.width()));
        float textViewWidth = ScreenHelper.getWidth(context) - getPixel(context, 116);

        int pix_18 = (int) getPixel(context, 18);
        int pix_26 = (int) getPixel(context, 26);
        int pix_52 = (int) getPixel(context, 52);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(pix_52, pix_52);
        if (width > textViewWidth)
            params.setMargins(pix_18, pix_26, 0, 0);
        else
            params.setMargins(pix_18, pix_18, 0, 0);
        bttendance.setLayoutParams(params);

        // Selector Events
        View selector = view.findViewById(R.id.item_selector);
        selector.setTag(R.id.post_id, post.id);
        selector.setOnClickListener(this);
    }

    private void drawNotice(View view, Context context, PostJson post) {

        RelativeLayout clicker = (RelativeLayout) view.findViewById(R.id.clicker);
        Bttendance bttendance = ((Bttendance) view.findViewById(R.id.bttendance));
        View notice = view.findViewById(R.id.notice);

        clicker.setVisibility(View.GONE);
        bttendance.setVisibility(View.GONE);
        notice.setVisibility(View.VISIBLE);

        // Title, Message, Time
        TimeredTextView title = (TimeredTextView) view.findViewById(R.id.timered_title);
        TextView message = (TextView) view.findViewById(R.id.message);
        TextView time = (TextView) view.findViewById(R.id.time);

        title.setVisibility(View.VISIBLE);
        message.setVisibility(View.VISIBLE);
        time.setVisibility(View.VISIBLE);

        title.setTimeredTextView(TimeredTextView.Type.Notice, post.id, mUser.id, mAuth);
        if (mAuth) {
            int studentCount = 0;
            int seenStudent = 0;
            if (mCourse != null)
                studentCount = mCourse.students_count;
            if (post.notice != null && post.notice.seen_students != null)
                seenStudent = post.notice.seen_students.length;
            title.setText(String.format(context.getString(R.string.notice_d_d_read), seenStudent, studentCount));
            title.setTextColor(context.getResources().getColor(R.color.bttendance_silver));
        } else if (post.notice.seen(mUser.id)) {
            title.setText(context.getString(R.string.notice));
            title.setTextColor(context.getResources().getColor(R.color.bttendance_silver));
        } else {
            title.setText(context.getString(R.string.unread_notice));
            title.setTextColor(context.getResources().getColor(R.color.bttendance_red));
        }

        message.setText(post.message);
        time.setText(DateHelper.getPostFormatString(post.createdAt));

        // Notice Icon Param
        Rect bounds = new Rect();
        Paint paint = new Paint();
        paint.setTextSize(12.0f);
        paint.getTextBounds(post.message, 0, post.message.length(), bounds);
        float width = getPixel(context, (float) Math.ceil(bounds.width()));
        float textViewWidth = ScreenHelper.getWidth(context) - getPixel(context, 116);

        int pix_18 = (int) getPixel(context, 18);
        int pix_26 = (int) getPixel(context, 26);
        int pix_52 = (int) getPixel(context, 52);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(pix_52, pix_52);
        if (width > textViewWidth || post.message.contains("\n"))
            params.setMargins(pix_18, pix_26, 0, 0);
        else
            params.setMargins(pix_18, pix_18, 0, 0);
        notice.setLayoutParams(params);

        // Selector Events
        View selector = view.findViewById(R.id.item_selector);
        selector.setTag(R.id.post_id, post.id);
        selector.setOnClickListener(this);
    }

    private void drawGuide(View view, Context context, Type type) {

        View guide = view.findViewById(R.id.guide);
        TextView guideMessage = (TextView) view.findViewById(R.id.guide_message);

        switch (type) {
            case UPDATE:
                guideMessage.setText(context.getString(R.string.post_guide_update));
                guide.setBackgroundResource(R.drawable.ic_bttendance_big);
                break;
            case TIPS:
                guideMessage.setText(context.getString(R.string.post_guide_tips));
                guide.setBackgroundResource(R.drawable.ic_bttendance_big);
                break;
            case GUIDE_CLICKER:
                guideMessage.setText(context.getString(R.string.post_guide_clicker));
                guide.setBackgroundResource(R.drawable.ic_clicker_big);
                break;
            case GUIDE_ATTENDANCE:
                guideMessage.setText(context.getString(R.string.post_guide_attendance));
                guide.setBackgroundResource(R.drawable.ic_attendance_big);
                break;
            case GUIDE_NOTICE:
                guideMessage.setText(context.getString(R.string.post_guide_notice));
                guide.setBackgroundResource(R.drawable.ic_notice_big);
                break;
        }

        // Selector Events
        View selector = view.findViewById(R.id.item_selector);
        selector.setTag(R.id.guide_type, type);
        selector.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_selector:
                if (v.getTag(R.id.post_id) == null) {
                    Type type = (Type) v.getTag(R.id.guide_type);
                    switch (type) {
                        case UPDATE:
                            String title = mContext.getString(R.string.update_app_title);
                            String message = mContext.getString(R.string.update_app_message);
                            BTEventBus.getInstance().post(new ShowAlertDialogEvent(BTDialogFragment.DialogType.CONFIRM, title, message, new BTDialogFragment.OnDialogListener() {
                                @Override
                                public void onConfirmed(String edit) {
                                    PackagesHelper.updateApp(mContext);
                                }

                                @Override
                                public void onCanceled() {
                                }
                            }));
                            break;
                        case TIPS:
                            if (mAuth) {
                                String code = "";
                                if (mCourse != null)
                                    code = mCourse.code;
                                Intent intent = new Intent(mContext, GuideCourseCreateActivity.class);
                                intent.putExtra(GuideCourseCreateActivity.EXTRA_CLASS_CODE, code);
                                mContext.startActivity(intent);
                                if (mContext instanceof BTActivity)
                                    ((BTActivity) mContext).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            } else {
                                Intent intent = new Intent(mContext, GuideCourseAttendActivity.class);
                                mContext.startActivity(intent);
                                if (mContext instanceof BTActivity)
                                    ((BTActivity) mContext).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }
                            break;
                        case GUIDE_CLICKER: {
                            SimpleWebViewFragment fragment = new SimpleWebViewFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString(SimpleWebViewFragment.EXTRA_URL, BTUrl.getTutorialClicker(mContext));
                            fragment.setArguments(bundle);
                            BTEventBus.getInstance().post(new AddFragmentEvent(fragment));
                            break;
                        }
                        case GUIDE_ATTENDANCE: {
                            SimpleWebViewFragment fragment = new SimpleWebViewFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString(SimpleWebViewFragment.EXTRA_URL, BTUrl.getTutorialAttendance(mContext));
                            fragment.setArguments(bundle);
                            BTEventBus.getInstance().post(new AddFragmentEvent(fragment));
                            break;
                        }
                        case GUIDE_NOTICE: {
                            SimpleWebViewFragment fragment = new SimpleWebViewFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString(SimpleWebViewFragment.EXTRA_URL, BTUrl.getTutorialNotice(mContext));
                            fragment.setArguments(bundle);
                            BTEventBus.getInstance().post(new AddFragmentEvent(fragment));
                            break;
                        }
                    }
                } else {
                    PostJson post = BTTable.PostTable.get((Integer) v.getTag(R.id.post_id));
                    if (post == null)
                        return;

                    if ("attendance".equals(post.type)) {
                        AttendanceDetailFragment frag = new AttendanceDetailFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt(BTKey.EXTRA_POST_ID, post.id);
                        frag.setArguments(bundle);
                        BTEventBus.getInstance().post(new AddFragmentEvent(frag));
                    } else if ("clicker".equals(post.type)) {
                        ClickerDetailFragment frag = new ClickerDetailFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt(BTKey.EXTRA_POST_ID, post.id);
                        frag.setArguments(bundle);
                        BTEventBus.getInstance().post(new AddFragmentEvent(frag));
                    } else if ("notice".equals(post.type)) {
                        NoticeDetailFragment frag = new NoticeDetailFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt(BTKey.EXTRA_POST_ID, post.id);
                        frag.setArguments(bundle);
                        BTEventBus.getInstance().post(new AddFragmentEvent(frag));
                    } else {
                    }
                }
                break;
            case R.id.choice_a:
                BTEventBus.getInstance().post(new ClickerClickEvent((Integer) v.getTag(R.id.post_id), 1));
                break;
            case R.id.choice_b:
                BTEventBus.getInstance().post(new ClickerClickEvent((Integer) v.getTag(R.id.post_id), 2));
                break;
            case R.id.choice_c:
                BTEventBus.getInstance().post(new ClickerClickEvent((Integer) v.getTag(R.id.post_id), 3));
                break;
            case R.id.choice_d:
                BTEventBus.getInstance().post(new ClickerClickEvent((Integer) v.getTag(R.id.post_id), 4));
                break;
            case R.id.choice_e:
                BTEventBus.getInstance().post(new ClickerClickEvent((Integer) v.getTag(R.id.post_id), 5));
                break;
            default:
                break;
        }
    }
}

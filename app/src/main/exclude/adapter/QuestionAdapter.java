package com.bttendance.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.bttendance.R;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.QuestionJson;

/**
 * Created by TheFinestArtist on 2013. 12. 3..
 */
public class QuestionAdapter extends CursorAdapter implements View.OnClickListener {

    public QuestionAdapter(Context context, Cursor cursor) {
        super(context, cursor, android.widget.CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
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
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.question_item, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        QuestionJson question = BTTable.MyQuestionTable.get(cursor.getInt(0));
        drawQuestion(view, question);
    }

    private void drawQuestion(View view, QuestionJson question) {

        if (question != null) {
            switch (question.choice_count) {
                case 2:
                    view.findViewById(R.id.choice).setBackgroundResource(R.drawable.ql_2);
                    break;
                case 3:
                    view.findViewById(R.id.choice).setBackgroundResource(R.drawable.ql_3);
                    break;
                case 4:
                    view.findViewById(R.id.choice).setBackgroundResource(R.drawable.ql_4);
                    break;
                case 5:
                    view.findViewById(R.id.choice).setBackgroundResource(R.drawable.ql_5);
                    break;
            }
            ((TextView) view.findViewById(R.id.message)).setText(question.message);
        }

        String guideText;
        if ("all".equals(question.detail_privacy))
            guideText = mContext.getString(R.string.clicker_guide_detail_privacy_all);
        else if ("none".equals(question.detail_privacy))
            guideText = mContext.getString(R.string.clicker_guide_detail_privacy_none);
        else
            guideText = mContext.getString(R.string.clicker_guide_detail_privacy_professor);

        if (question.show_info_on_select)
            guideText = guideText + "<br>" + mContext.getString(R.string.clicker_guide_show_info_on_select_true);
        else
            guideText = guideText + "<br>" + mContext.getString(R.string.clicker_guide_show_info_on_select_false);

        guideText = guideText + "<br>" + String.format(mContext.getString(R.string.clicker_guide_progress_time), question.progress_time / 60);

        TextView guide = (TextView) view.findViewById(R.id.question_option_guide);
        guide.setText(Html.fromHtml(guideText));

        // Selector Events
        View selector = view.findViewById(R.id.item_selector);
        selector.setTag(R.id.question_id, question.id);
        selector.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_selector:
                ((ListView) v.getParent().getParent()).performItemClick(v, 0, 0);
                break;
            default:
                break;
        }
    }
}

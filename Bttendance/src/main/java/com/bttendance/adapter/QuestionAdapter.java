package com.bttendance.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bttendance.BTDebug;
import com.bttendance.R;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.QuestionJson;
import com.bttendance.view.Bttendance;

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
        drawQuestion(view, context, question);
    }

    private void drawQuestion(View view, Context context, QuestionJson question) {

        RelativeLayout clicker = (RelativeLayout) view.findViewById(R.id.clicker);
        Bttendance bttendance = ((Bttendance) view.findViewById(R.id.bttendance));
        View notice = view.findViewById(R.id.notice);

        clicker.setVisibility(View.VISIBLE);
        bttendance.setVisibility(View.GONE);
        notice.setVisibility(View.GONE);

//        DefaultRenderer renderer = post.clicker.getRenderer(context);
//        CategorySeries series = post.clicker.getSeries();
//        GraphicalView chartView = ChartFactory.getPieChartView(context, series, renderer);
//        clicker.addView(chartView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//
//        View ring = new View(context);
//        ring.setBackgroundResource(R.drawable.ic_clicker_ring);
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) getPixel(context, 52), (int) getPixel(context, 52));
//        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//        clicker.addView(ring, params);
//
//        // Title, Message, Time
//        TextView title = (TextView) view.findViewById(R.id.title);
//        TextView message = (TextView) view.findViewById(R.id.message);
//        TextView time = (TextView) view.findViewById(R.id.time);
//
//        title.setVisibility(View.VISIBLE);
//        message.setVisibility(View.VISIBLE);
//        time.setVisibility(View.VISIBLE);
//
//        title.setTextColor(context.getResources().getColor(R.color.bttendance_silver));
//        title.setText(post.course.name);
//        message.setText(post.message + "\n" + post.clicker.getDetail());
//        time.setText(DateHelper.getBTFormatString(post.createdAt));

        // Selector Events
        View selector = view.findViewById(R.id.item_selector);
        selector.setTag(R.id.question_id, question.id);
        selector.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_selector:
                BTDebug.LogError(v.getParent().getParent().getClass().getSimpleName());
                QuestionJson question = BTTable.MyQuestionTable.get((Integer) v.getTag(R.id.question_id));
                if (question == null)
                    return;
                break;
            default:
                break;
        }
    }
}

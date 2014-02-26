package com.bttendance.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.bttendance.BTDebug;
import com.bttendance.R;
import com.bttendance.event.button.PlusClickedEvent;
import com.bttendance.helper.StringMatcher;
import com.bttendance.model.json.BTJson;
import com.bttendance.model.json.GradeJson;
import com.squareup.otto.BTEventBus;

import java.util.ArrayList;

/**
 * Created by TheFinestArtist on 2013. 12. 11..
 */
public class BTListAdapter extends ArrayAdapter<BTListAdapter.Item> implements SectionIndexer, View.OnClickListener {

    private Context mContext;
    private ArrayList<Item> mItems;
    private LayoutInflater mLayoutInflater;
    private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public BTListAdapter(Context context) {
        super(context, 0);
        mContext = context;
        mItems = new ArrayList<Item>();
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setItems(ArrayList<Item> items) {
        mItems = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        Item i = mItems.get(position);
        if (i != null) {
            if (i.isSection()) {
                v = mLayoutInflater.inflate(R.layout.simple_section, null);
                TextView title = (TextView) v.findViewById(R.id.section_text);
                title.setText(i.getTitle());
            } else {
                v = mLayoutInflater.inflate(R.layout.bt_list_item, null);
                TextView title = (TextView) v.findViewById(R.id.title);
                TextView message = (TextView) v.findViewById(R.id.message);
                ImageView image = (ImageView) v.findViewById(R.id.add_btn);
                if (i.joined()) {
                    image.setImageResource(R.drawable.ic_list_checked);
                    image.setClickable(false);
                    image.setOnClickListener(null);
                } else if (i.getJson() != null && i.getJson() instanceof GradeJson) {
                    image.setImageResource(R.drawable.ic_grade);
                    image.setClickable(false);
                    image.setOnClickListener(null);
                    TextView grade = (TextView) v.findViewById(R.id.grade);
                    TextView gradeTotal = (TextView) v.findViewById(R.id.grade_total);
                    grade.setVisibility(View.VISIBLE);
                    gradeTotal.setVisibility(View.VISIBLE);
                    if (((GradeJson) i.getJson()).grade == null) {
                        grade.setText("0");
                        gradeTotal.setText("0");
                    } else {
                        String[] gradeStrings = ((GradeJson) i.getJson()).grade.split("/");
                        grade.setText(gradeStrings[0]);
                        gradeTotal.setText(gradeStrings[1]);
                    }
                } else {
                    image.setImageResource(R.drawable.ic_list_add);
                    image.setClickable(true);
                    image.setOnClickListener(this);
                }
                image.setTag(R.id.json, i.getJson());
                image.setTag(R.id.id, i.getId());
                title.setText(i.getTitle());
                message.setText(i.getMessage());
            }
        }
        return v;
    }

    @Override
    public Item getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    // SectionIndexer
    @Override
    public Object[] getSections() {
        String[] sections = new String[mSections.length()];
        for (int i = 0; i < mSections.length(); i++)
            sections[i] = String.valueOf(mSections.charAt(i));
        return sections;
    }

    @Override
    public int getPositionForSection(int section) {
        for (int i = section; i >= 0; i--) {
            for (int j = 0; j < getCount(); j++) {
                if (i == 0) {
                    for (int k = 0; k <= 9; k++) {
                        if (StringMatcher.match(String.valueOf((getItem(j)).getTitle().charAt(0)), String.valueOf(k)))
                            return j;
                    }
                } else {
                    if (StringMatcher.match(String.valueOf((getItem(j)).getTitle().charAt(0)), String.valueOf(mSections.charAt(i))))
                        return j;
                }
            }
        }
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    // OnClickListener
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_btn:
                BTJson json = (BTJson) v.getTag(R.id.json);
                int id = (Integer) v.getTag(R.id.id);
                BTEventBus.getInstance().post(new PlusClickedEvent(json, id));
                break;
        }
    }

    public static class Item {

        private boolean mIsSection;
        private boolean mJoined;
        private String mTitle;
        private String mMessage;
        private Object mJson;
        private int mId;

        // Section
        public Item(String title) {
            mIsSection = true;
            mTitle = title;
        }

        // Entry
        public Item(boolean joined, String title, String message, Object json, int id) {
            mIsSection = false;
            mJoined = joined;
            mTitle = title;
            mMessage = message;
            mJson = json;
            mId = id;
        }

        public boolean isSection() {
            return mIsSection;
        }

        public boolean joined() {
            return mJoined;
        }

        public String getTitle() {
            return mTitle;
        }

        public String getMessage() {
            return mMessage;
        }

        public Object getJson() {
            return mJson;
        }

        public int getId() {
            return mId;
        }
    }
}

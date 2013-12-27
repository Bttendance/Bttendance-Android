package com.utopia.bttendance.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.squareup.otto.BTEventBus;
import com.utopia.bttendance.R;
import com.utopia.bttendance.event.JoinEvent;
import com.utopia.bttendance.helper.StringMatcher;
import com.utopia.bttendance.model.json.BTJson;

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
                v = mLayoutInflater.inflate(R.layout.listview_section_item, null);
                TextView title = (TextView) v.findViewById(R.id.title);
                title.setText(i.getTitle());
            } else {
                v = mLayoutInflater.inflate(R.layout.listview_entry_item, null);
                TextView title = (TextView) v.findViewById(R.id.title);
                TextView message = (TextView) v.findViewById(R.id.message);
                ImageView image = (ImageView) v.findViewById(R.id.add_btn);
                if (i.joined()) {
                    image.setImageResource(R.drawable.ic_bttendance_check_cyan_small);
                    image.setClickable(false);
                } else {
                    image.setImageResource(R.drawable.ic_bttendance_circle_cyan_small);
                    image.setClickable(true);
                }
                image.setTag(R.id.json, i.getJson());
                image.setOnClickListener(this);
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
                BTEventBus.getInstance().post(new JoinEvent(json));
                break;
        }
    }

    public static class Item {

        private boolean mIsSection;
        private boolean mJoined;
        private String mTitle;
        private String mMessage;
        private Object mJson;

        public Item(boolean isSection, boolean joined, String title, String message, Object json) {
            mIsSection = isSection;
            mJoined = joined;
            mTitle = title;
            mMessage = message;
            mJson = json;
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
    }
}

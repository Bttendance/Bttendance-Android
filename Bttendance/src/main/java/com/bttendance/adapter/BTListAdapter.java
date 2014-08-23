package com.bttendance.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bttendance.R;
import com.bttendance.model.json.BTJsonSimple;
import com.bttendance.model.json.UserJsonSimple;

import java.util.ArrayList;

/**
 * Created by TheFinestArtist on 2013. 12. 11..
 */
public class BTListAdapter extends ArrayAdapter<BTListAdapter.Item> {

    private ArrayList<Item> mItems;
    private LayoutInflater mLayoutInflater;

    public BTListAdapter(Context context) {
        super(context, 0);
        mItems = new ArrayList<Item>();
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setItems(ArrayList<Item> items) {
        mItems = items;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View v;
        final Item item = mItems.get(position);

        v = mLayoutInflater.inflate(R.layout.bt_list_item, null);
        TextView title = (TextView) v.findViewById(R.id.title);
        TextView message = (TextView) v.findViewById(R.id.message);
        ImageView image = (ImageView) v.findViewById(R.id.add_btn);

        title.setText(item.getTitle());
        message.setText(item.getMessage());
        image.setTag(R.id.json, item.getJson());
        image.setTag(R.id.type, item.getType());

        switch (item.getType()) {
            case EMPTY: {
                TextView status = (TextView) v.findViewById(R.id.status);
                status.setVisibility(View.GONE);
                image.setImageDrawable(null);

                TextView grade = (TextView) v.findViewById(R.id.grade);
                TextView gradeTotal = (TextView) v.findViewById(R.id.grade_total);
                grade.setVisibility(View.GONE);
                gradeTotal.setVisibility(View.GONE);

                v.findViewById(R.id.attendance_selector).setVisibility(View.GONE);

                break;
            }
            case GRADE: {
                TextView status = (TextView) v.findViewById(R.id.status);
                status.setVisibility(View.GONE);
                image.setImageResource(R.drawable.ic_grade);

                TextView grade = (TextView) v.findViewById(R.id.grade);
                TextView gradeTotal = (TextView) v.findViewById(R.id.grade_total);
                grade.setVisibility(View.VISIBLE);
                gradeTotal.setVisibility(View.VISIBLE);

                String[] gradeStrings = ((UserJsonSimple) item.getJson()).grade.split("/");
                grade.setText(gradeStrings[0]);
                gradeTotal.setText(gradeStrings[1]);

                v.findViewById(R.id.attendance_selector).setVisibility(View.GONE);
                break;
            }
            case CLICKER: {
                TextView status = (TextView) v.findViewById(R.id.status);
                status.setVisibility(View.GONE);

                TextView grade = (TextView) v.findViewById(R.id.grade);
                TextView gradeTotal = (TextView) v.findViewById(R.id.grade_total);
                grade.setVisibility(View.GONE);
                gradeTotal.setVisibility(View.GONE);

                v.findViewById(R.id.attendance_selector).setVisibility(View.GONE);

                switch (item.getStatus()) {
                    case CHOICE_A:
                        image.setImageResource(R.drawable.s_a);
                        break;
                    case CHOICE_B:
                        image.setImageResource(R.drawable.s_b);
                        break;
                    case CHOICE_C:
                        image.setImageResource(R.drawable.s_c);
                        break;
                    case CHOICE_D:
                        image.setImageResource(R.drawable.s_d);
                        break;
                    case CHOICE_E:
                        image.setImageResource(R.drawable.s_e);
                        break;
                    case CHOICE_NONE:
                        image.setImageResource(R.drawable.ic_absent);
                        break;
                }
                break;
            }
            case ATTENDANCE: {
                TextView status = (TextView) v.findViewById(R.id.status);
                status.setVisibility(View.VISIBLE);

                TextView grade = (TextView) v.findViewById(R.id.grade);
                TextView gradeTotal = (TextView) v.findViewById(R.id.grade_total);
                grade.setVisibility(View.GONE);
                gradeTotal.setVisibility(View.GONE);

                View selector = v.findViewById(R.id.attendance_selector);
                selector.setVisibility(View.VISIBLE);
                final View finalConvertView = convertView;
                selector.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((ListView) parent).performItemClick(finalConvertView, position, item.getJson().id);
                    }
                });

                switch (item.getStatus()) {
                    case ABSENT:
                        image.setImageResource(R.drawable.ic_absent);
                        status.setText(getContext().getString(R.string.absent));
                        selector.setBackgroundResource(R.drawable.attendance_selector_silver);
                        break;
                    case TARDY:
                        image.setImageResource(R.drawable.ic_tardy);
                        status.setText(getContext().getString(R.string.tardy));
                        selector.setBackgroundResource(R.drawable.attendance_selector_cyan);
                        break;
                    case PRESENT:
                        image.setImageResource(R.drawable.ic_present);
                        status.setText(getContext().getString(R.string.present));
                        selector.setBackgroundResource(R.drawable.attendance_selector_navy);
                        break;
                }
                break;
            }
            case NOTICE: {
                TextView status = (TextView) v.findViewById(R.id.status);
                status.setVisibility(View.VISIBLE);

                TextView grade = (TextView) v.findViewById(R.id.grade);
                TextView gradeTotal = (TextView) v.findViewById(R.id.grade_total);
                grade.setVisibility(View.GONE);
                gradeTotal.setVisibility(View.GONE);

                v.findViewById(R.id.attendance_selector).setVisibility(View.GONE);

                switch (item.getStatus()) {
                    case UNREAD:
                        image.setImageResource(R.drawable.close_eye);
                        status.setText(getContext().getString(R.string.unread));
                        break;
                    case READ:
                        image.setImageResource(R.drawable.eye);
                        status.setText(getContext().getString(R.string.read));
                        break;
                }
                break;
            }
            default:
                break;
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

    public static class Item {

        private Type mType;
        private String mTitle;
        private String mMessage;
        private BTJsonSimple mJson;
        private Status mStatus;

        // Entry #1
        public Item(Type type, String title, String message, BTJsonSimple json) {
            mType = type;
            mTitle = title;
            mMessage = message;
            mJson = json;
        }

        // Entry #2
        public Item(Type type, String title, String message, BTJsonSimple json, Status status) {
            mType = type;
            mTitle = title;
            mMessage = message;
            mJson = json;
            mStatus = status;
        }

        public Type getType() {
            return mType;
        }

        public String getTitle() {
            return mTitle;
        }

        public String getMessage() {
            return mMessage;
        }

        public BTJsonSimple getJson() {
            return mJson;
        }

        public Status getStatus() {
            return mStatus;
        }

        public enum Type {GRADE, EMPTY, CLICKER, ATTENDANCE, NOTICE}

        public enum Status {ABSENT, TARDY, PRESENT, CHOICE_A, CHOICE_B, CHOICE_C, CHOICE_D, CHOICE_E, CHOICE_NONE, UNREAD, READ}
    }
}

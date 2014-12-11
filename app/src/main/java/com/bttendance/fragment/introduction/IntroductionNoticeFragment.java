package com.bttendance.fragment.introduction;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bttendance.R;
import com.bttendance.activity.guide.GuideActivity;
import com.bttendance.fragment.BTFragment;
import com.bttendance.model.BTKey;
import com.bttendance.model.BTUrl;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by TheFinestArtist on 2014. 8. 13..
 */
public class IntroductionNoticeFragment extends BTFragment {

    @InjectView(R.id.see_more)
    Button mSeeMore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_introduction_notice, container, false);
        ButterKnife.inject(this, view);

        mSeeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GuideActivity.class);
                intent.putExtra(BTKey.EXTRA_URL, BTUrl.getGuideNotice(getActivity()));
                intent.putExtra(BTKey.EXTRA_TITLE, getString(R.string.guide));
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.modal_activity_open_enter, R.anim.modal_activity_open_exit);
            }
        });
        return view;
    }
}

package com.bttendance.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.bttendance.R;
import com.bttendance.helper.DipPixelHelper;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.PostJson;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;

/**
 * Created by TheFinestArtist on 2013. 12. 30..
 */
public class PostClickerFragment extends BTFragment {

    private PostJson mPost;
    private GraphicalView mChartView;

    public PostClickerFragment(int postId) {
        mPost = BTTable.PostTable.get(postId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_clicker, container, false);

        RelativeLayout clicker = (RelativeLayout) view.findViewById(R.id.clicker);

        DefaultRenderer renderer = mPost.clicker.getRenderer(getActivity());
        CategorySeries series = mPost.clicker.getSeries();
        mChartView = ChartFactory.getPieChartView(getActivity(), series, renderer);
        clicker.addView(mChartView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        View ring = new View(getActivity());
        ring.setBackgroundResource(R.drawable.ic_polledge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) DipPixelHelper.getPixel(getActivity(), 243), (int) DipPixelHelper.getPixel(getActivity(), 243));
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        clicker.addView(ring, params);

        // Title, Message, Detail
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView message = (TextView) view.findViewById(R.id.message);
        TextView detail = (TextView) view.findViewById(R.id.detail);

        title.setText(mPost.course.name);
        message.setText(mPost.message);
        detail.setText(mPost.clicker.getDetail());

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setTitle(mPost.course.name);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.abs__home:
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
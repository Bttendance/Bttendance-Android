package com.bttendance.activity.guide;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.bttendance.R;
import com.bttendance.activity.BTActivity;
import com.bttendance.fragment.SimpleWebViewFragment;
import com.bttendance.model.BTKey;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by TheFinestArtist on 2014. 08. 14..
 */
public class GuideActivity extends BTActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);

        String url = getIntent().getStringExtra(BTKey.EXTRA_URL);
        String title = getIntent().getStringExtra(BTKey.EXTRA_TITLE);

        SimpleWebViewFragment frag = new SimpleWebViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BTKey.EXTRA_URL, url);
        bundle.putString(BTKey.EXTRA_TITLE, title);
        frag.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(
                R.anim.no_anim,
                R.anim.no_anim,
                R.anim.no_anim,
                R.anim.no_anim);
        ft.add(R.id.content, frag, ((Object) frag).getClass().getSimpleName());
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.modal_activity_close_enter, R.anim.modal_activity_close_exit);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

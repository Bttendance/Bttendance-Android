package com.utopia.bttendance.activity.sign;

import com.actionbarsherlock.view.MenuItem;
import com.utopia.bttendance.R;
import com.utopia.bttendance.activity.BTActivity;

/**
 * Created by TheFinestArtist on 2013. 11. 27..
 */
public class ProfessorSerial extends BTActivity {


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return (super.onOptionsItemSelected(item));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out_right);
    }
}

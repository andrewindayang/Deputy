package deputyapp.deputyapp.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FragmentActivity extends android.support.v4.app.FragmentActivity
{

    // Overridden for action bar style pre lollipop
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    // Overridden for Butterknife view-injection library
    @Override
    public void setContentView(int layoutResID)
    {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    // Overridden for Calligraphy font-styling library
    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    // Overridden for Material-like transitions
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    // Overridden for Material-like transitions
    @Override
    public void startActivity(Intent intent)
    {
        super.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent upIntent = NavUtils.getParentActivityIntent(this);
            if (upIntent == null){
                return super.onOptionsItemSelected(item);
            }
            upIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(upIntent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

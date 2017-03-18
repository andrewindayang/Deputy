package deputyapp.deputyapp.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import deputyapp.deputyapp.Fragment.PreviousShiftFragment;
import deputyapp.deputyapp.Fragment.StartEndShiftFragment;

public class TabAdapter extends FragmentStatePagerAdapter {

    public TabAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new StartEndShiftFragment();
            case 1:
                return new PreviousShiftFragment();
            default:
                return null;
        }
    }


    @Override
    public int getCount() {
        return 2;
    }
}
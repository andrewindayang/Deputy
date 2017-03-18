package deputyapp.deputyapp.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import de.greenrobot.event.EventBus;
import deputyapp.deputyapp.Activity.MainActivity;
import deputyapp.deputyapp.Activity.ShiftDetailsActivity;
import deputyapp.deputyapp.Adapter.PreviousShiftAdapter;
import deputyapp.deputyapp.Network.ModelManager;
import deputyapp.deputyapp.Network.NetworkManager;
import deputyapp.deputyapp.R;
import deputyapp.deputyapp.Util.BasicEvent;
import deputyapp.deputyapp.dao.Business;
import deputyapp.deputyapp.dao.PrevShiftsData;

public class PreviousShiftFragment extends Fragment {

    private boolean isFragmentLoaded = false;

    @BindView(R.id.prevShifts)
    ListView prevShiftsListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.previous_shift_fragment, container, false);
        ButterKnife.bind(this, v);
        NetworkManager.getInstance().getPrevShifts();
        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(BasicEvent event) {
        if(event == BasicEvent.PREV_SHIFTS_LOADED_NULL || event == BasicEvent.PREV_SHIFTS_LOADED){
            PreviousShiftAdapter prevShiftsData = new PreviousShiftAdapter(getActivity());
            prevShiftsListView.setAdapter(prevShiftsData);
            int totalHeight2 = 0;
            for (int size = 0; size < prevShiftsData.getCount(); size++) {
                View listItem = prevShiftsData.getView(size, null, prevShiftsListView);
                listItem.measure(0, 0);
                totalHeight2 += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params2 = prevShiftsListView.getLayoutParams();
            params2.height = totalHeight2 + (prevShiftsListView.getDividerHeight() * (prevShiftsData.getCount() - 1));
            prevShiftsListView.setLayoutParams(params2);
        }
    }

    @OnItemClick(R.id.prevShifts)
    public void onPrevItemClickListener(AdapterView<?> adapterView, View view, int i, long l){
        Intent intent = new Intent(getContext(), ShiftDetailsActivity.class);
        intent.putExtra("ITEM_POSITION",i);
        startActivity(intent);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !isFragmentLoaded ) {
            NetworkManager.getInstance().getPrevShifts();
            isFragmentLoaded = true;
        }
    }


}

package deputyapp.deputyapp.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import deputyapp.deputyapp.Network.ModelManager;
import deputyapp.deputyapp.dao.PrevShiftsData;


public class PreviousShiftAdapter extends BasePreviousShiftAdapter {

    List<PrevShiftsData> prevShiftsDataList;

    public PreviousShiftAdapter(Context context){
        this.context = context;
        reloadData();
    }

    @Override
    protected void reloadData() {
         prevShiftsDataList = ModelManager.getInstance().getPreviousShiftData();
    }

    @Override
    public int getCount() {
        return prevShiftsDataList.size();
    }

    @Override
    public Object getItem(int i) {
        return prevShiftsDataList == null ? null : prevShiftsDataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(convertView, parent);
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        PrevShiftsData prevShiftsData = prevShiftsDataList.get(position);
        populateAllPrevShiftData(parent.getContext(), viewHolder, prevShiftsData);
        return view;
    }
}
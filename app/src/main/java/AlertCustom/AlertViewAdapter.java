package AlertCustom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import deputyapp.deputyapp.R;


/**
 * Created by Sai on 15/8/9.
 */
public class AlertViewAdapter extends BaseAdapter {
    private List<String> mDatas;
    private List<String> mDestructive;
    private boolean isSingleButton;
    public AlertViewAdapter(List<String> datas, List<String> destructive, boolean isSingleButton){
        this.mDatas =datas;
        this.isSingleButton = isSingleButton;
        this.mDestructive =destructive;
    }
    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String data= mDatas.get(position);
        Holder holder=null;
        View view =convertView;
        if(view==null){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view=inflater.inflate(R.layout.item_alertbutton, null);
            holder=creatHolder(view,isSingleButton);
            view.setTag(holder);
        }
        else{
            holder=(Holder) view.getTag();
        }
        holder.UpdateUI(parent.getContext(),data,position,isSingleButton);
        return view;
    }
    public Holder creatHolder(View view, boolean isSingleButton){
        return new Holder(view,isSingleButton);
    }
    class Holder {
        private TextView tvAlert;
        private Button okButton;

        public Holder(View view, boolean isSingleButton){
            tvAlert = (TextView) view.findViewById(R.id.tvAlert);
            okButton = (Button) view.findViewById(R.id.okButton);
            if(isSingleButton){
                tvAlert.setVisibility(View.GONE);
                okButton.setVisibility(View.VISIBLE);
            } else{
                tvAlert.setVisibility(View.VISIBLE);
                okButton.setVisibility(View.GONE);
            }

        }
        public void UpdateUI(Context context, String data, int position, boolean isSingleButton){
            if(!isSingleButton){
                tvAlert.setText(data);
                if (mDestructive!= null && mDestructive.contains(data)){
                    tvAlert.setTextColor(context.getResources().getColor(R.color.textColor_alert_button_destructive));
                }
                else{
                    tvAlert.setTextColor(context.getResources().getColor(R.color.textColor_alert_button_others));
                }
            } else{
                okButton.setTextColor(context.getResources().getColor(R.color.textColor_alert_button_others));
            }
        }
    }
}
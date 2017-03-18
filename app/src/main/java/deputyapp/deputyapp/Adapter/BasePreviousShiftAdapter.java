package deputyapp.deputyapp.Adapter;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import deputyapp.deputyapp.R;
import deputyapp.deputyapp.Util.Util;
import deputyapp.deputyapp.dao.PrevShiftsData;

public abstract class BasePreviousShiftAdapter extends BaseAdapter {

    Context context;
    private GoogleMap googleMap;
    private GoogleApiClient googleApiClient;
    MapView mMapView;
    Location location;
    View rootView;

    protected final View getView(View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        convertView = inflater.inflate(R.layout.listview_item, parent, false);
        ViewHolder holder = loadHolder(new ViewHolder(), convertView);
        convertView.setTag(holder);
        return convertView;
    }

    protected final void populateAllPrevShiftData(final Context context, final ViewHolder holder, PrevShiftsData prevShiftsData) {
        Picasso.with(context).load(prevShiftsData.getImage()).into(holder.shiftImage);
        holder.startLatLon.setText("Start LATLON : " +prevShiftsData.getStartLatitude() + ", " + prevShiftsData.getStartLongitude());
        if(prevShiftsData.getEndLongitude().equalsIgnoreCase("0.00000") && prevShiftsData.getEndLatitude().equalsIgnoreCase("0.00000") ){
            //still on progress
            holder.endLatLon.setText("Still on progress");
        }else{
            holder.endLatLon.setText("End LATLON : " +prevShiftsData.getEndLatitude() + ", " + prevShiftsData.getEndLongitude());
        }

        if(prevShiftsData.getEnd().equalsIgnoreCase("")){
            holder.startEnd.setText("From : " + Util.convertDate(prevShiftsData.getStart()));
        }else {
            holder.startEnd.setText("From : " + Util.convertDate(prevShiftsData.getStart()) + " to " + Util.convertDate(prevShiftsData.getEnd()));
        }

    }


    protected abstract void reloadData();

    private ViewHolder loadHolder(ViewHolder holder, View convertView)
    {
        ButterKnife.bind(holder, convertView);
        return holder;
    }

    protected static class ViewHolder {

        @BindView(R.id.shiftImage)
        ImageView shiftImage;

        @BindView(R.id.startLatLon)
        TextView startLatLon;

        @BindView(R.id.endLatLon)
        TextView endLatLon;

        @BindView(R.id.startEnd)
        TextView startEnd;

    }
}
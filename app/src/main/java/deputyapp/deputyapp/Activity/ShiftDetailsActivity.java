package deputyapp.deputyapp.Activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import deputyapp.deputyapp.Fragment.FragmentActivity;
import deputyapp.deputyapp.Network.ModelManager;
import deputyapp.deputyapp.R;
import deputyapp.deputyapp.Util.Util;
import deputyapp.deputyapp.dao.PrevShiftsData;

public class ShiftDetailsActivity extends FragmentActivity implements LocationListener, GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {

    private GoogleMap googleMap;
    private GoogleApiClient googleApiClient;
    MapView mMapView;
    Location location;
    ModelManager manager = ModelManager.getInstance();
    int itemPosition = 0;

    @BindView(R.id.setting)
    RelativeLayout setting;

    @BindView(R.id.shiftImage)
    ImageView shiftImage;

    @BindView(R.id.date)
    TextView date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_detail);
        ButterKnife.bind(this);
        itemPosition = getIntent().getIntExtra("ITEM_POSITION",0);
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();
        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }


        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                startDemo();
            }
        });
        setLocationPermissionlayout();
    }

    public void startDemo(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            setting.setVisibility(View.VISIBLE);
            checkGPS();
        } else {
            LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                checkGPS();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-33.866906, 151.208896), 10));
            } else {
                googleMap.getUiSettings().setRotateGesturesEnabled(false);
                googleMap.setIndoorEnabled(false);
                googleMap.getUiSettings().setIndoorLevelPickerEnabled(false);
                googleMap.getUiSettings().setZoomControlsEnabled(false);
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                LocationManager lms = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                if(lms != null) {
                    if (lms.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null) {
                        final Location location = lms.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        this.location = location;

                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
                    }
                }else{
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-33.866906,151.208896), 10));
                }
            }
        }

        putMarker();


        List<PrevShiftsData> prevShiftsDataList = manager.getPreviousShiftData();
        if(prevShiftsDataList.get(itemPosition) != null){
            PrevShiftsData prevShiftsData = prevShiftsDataList.get(itemPosition);
            if(prevShiftsData.getStartLongitude() != null && !prevShiftsData.getStartLongitude().equalsIgnoreCase("") && !prevShiftsData.getStartLongitude().equalsIgnoreCase("0.00000")) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(prevShiftsData.getStartLatitude()), Double.parseDouble(prevShiftsData.getStartLongitude())), 15));
            }
        }
    }

    public void putMarker(){
        List<PrevShiftsData> prevShiftsDataList = manager.getPreviousShiftData();
        if(prevShiftsDataList.get(itemPosition) != null){
            PrevShiftsData prevShiftsData = prevShiftsDataList.get(itemPosition);
            Picasso.with(this).load(prevShiftsData.getImage()).into(shiftImage);
            if(prevShiftsData.getEnd().equalsIgnoreCase("")){
                date.setText("From : " + Util.convertDate(prevShiftsData.getStart()));
            }else {
                date.setText("From : " + Util.convertDate(prevShiftsData.getStart()) + " to " + Util.convertDate(prevShiftsData.getEnd()));
            }

            if(prevShiftsData.getStartLongitude() != null && !prevShiftsData.getStartLongitude().equalsIgnoreCase("") && !prevShiftsData.getStartLongitude().equalsIgnoreCase("0.00000")) {
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(prevShiftsData.getStartLatitude()), Double.parseDouble(prevShiftsData.getStartLongitude())))
                        .title("Start shift"));
            }
            if(prevShiftsData.getEndLatitude() != null && !prevShiftsData.getEndLatitude().equalsIgnoreCase("") && !prevShiftsData.getEndLatitude().equalsIgnoreCase("0.00000")) {
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(prevShiftsData.getEndLatitude()), Double.parseDouble(prevShiftsData.getEndLongitude())))
                        .title("End shift"));
            }
        }
    }

    @OnClick(R.id.backButton)
    public void onBackButtonClicked(){
        finish();
    }


    @OnClick(R.id.hiddenButton)
    public void onSettingTurnOnLocationClicked(){
        alertOpenSetting();
    }

    private void alertOpenSetting() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Location, Storage and Telephone permissions are required to use this app.");
        alertDialogBuilder.setMessage("Please enable these permissions in Permissions under app settings.");
        alertDialogBuilder.setNegativeButton("Go to setting", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                goToSettings();
            }
        });
        alertDialogBuilder.create();
        alertDialogBuilder.show();
    }

    private void goToSettings() {
        Intent viewIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(viewIntent);
    }

    @Override
    public void onResume(){
        super.onResume();
        setLocationPermissionlayout();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    public void setLocationPermissionlayout()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            setting.setVisibility(View.VISIBLE);
        } else{
            setting.setVisibility(View.GONE);
        }
    }

    public void checkGPS() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            googleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);
            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                status.startResolutionForResult(ShiftDetailsActivity.this, 1000);
                            } catch (IntentSender.SendIntentException e) {
                                Log.d("TESTT","ex : "+ e.toString());
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            });
        }
    }

}

package deputyapp.deputyapp.Fragment;

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
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

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
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import AlertCustom.OnItemClickListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import deputyapp.deputyapp.Activity.MainActivity;
import deputyapp.deputyapp.Activity.SplashActivity;
import deputyapp.deputyapp.Model.PostShift;
import deputyapp.deputyapp.Network.ModelManager;
import deputyapp.deputyapp.Network.NetworkManager;
import deputyapp.deputyapp.R;
import deputyapp.deputyapp.Util.BasicEvent;
import deputyapp.deputyapp.Util.Util;
import deputyapp.deputyapp.dao.Business;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class StartEndShiftFragment extends android.support.v4.app.Fragment implements LocationListener, GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;
    Location location;
    View rootView;

    @BindView(R.id.setting)
    RelativeLayout setting;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.start_end_fragment, container, false);
        this.rootView = rootView;
        ButterKnife.bind(this, rootView);
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .build();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setLocationPermissionlayout();
        return rootView;
    }

    @OnClick(R.id.setting)
    public void onSettingTurnOnLocationClicked(){
        alertOpenSetting();
    }

    private void tellUserLocationNotTurnOn(String endOrStart){
        deputyapp.deputyapp.Util.AlertDialog.showDialogWithoutAlertHeader(getActivity(), "Please turn on your location to " + endOrStart + " your shift.", new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {

            }
        });
    }
    private void alertOpenSetting() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
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
        Intent myAppSettings = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getActivity().getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(myAppSettings, 0);

    }

    @Override
    public void onResume(){
        super.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
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
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            setting.setVisibility(View.VISIBLE);
        } else{
            setting.setVisibility(View.GONE);
            startDemo();
        }
    }

    @OnClick(R.id.startShift)
    public void onStartShiftClicked(){
//        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            setting.setVisibility(View.VISIBLE);
//            checkGPS();
//            View view = rootView.findViewById(R.id.activity_main);
//            assert view != null;
//            Snackbar.make(view, "Please enable your GPS", Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                }
//            }).show();
//        } else {
            if (location != null) {
                PostShift postShift = new PostShift(Util.getCurrentTime(), location.getLatitude() + "", location.getLongitude() + "");
                NetworkManager.getInstance().postStartShift(postShift, new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {
                        showToast(s);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        showToast(error.getMessage());
                    }
                });
            } else {
                if (isNetworkAvailable()) {
                    checkGPS();
                    setLocationPermissionlayout();
                } else {
                    View view = rootView.findViewById(R.id.activity_main);
                    assert view != null;
                    Snackbar.make(view, "No internet connection.", Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
                            startActivity(intent);
                        }
                    }).show();
                }
            }
//        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @OnClick(R.id.endShift)
    public void onEndShiftClicked(){
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            setting.setVisibility(View.VISIBLE);
            checkGPS();
            setLocationPermissionlayout();
        } else {
            if (location != null) {
                PostShift postShift = new PostShift(Util.getCurrentTime(),location.getLatitude()+"",location.getLongitude()+"");
                NetworkManager.getInstance().postEndShift(postShift, new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {
                        showToast(s);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        showToast(error.getMessage());
                    }
                });
            } else {
                if (isNetworkAvailable()) {
                    checkGPS();
                    setLocationPermissionlayout();
                } else {
                    View view = rootView.findViewById(R.id.activity_main);
                    assert view != null;
                    Snackbar.make(view, "No internet connection.", Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
                            startActivity(intent);
                        }
                    }).show();
                }
            }
        }
    }

    public void startDemo(){
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            setting.setVisibility(View.VISIBLE);
            checkGPS();
            setLocationPermissionlayout();
        } else {
            LocationManager lms = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if(lms != null) {
                if (lms.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null) {
                    final Location location = lms.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    this.location = location;
                }
                if (lms.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                } else {
                    alertGPSNeedTurnOn();
                }
            }else{
            }
        }
    }

    public void alertGPSNeedTurnOn(){
        deputyapp.deputyapp.Util.AlertDialog.showDialogWithHeaderTwoButton(getActivity(), "GPS not available", "To start or end shift GPS need to be turn on", new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                if(position == -1){
                    Intent viewIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(viewIntent);
                }else{

                }
            }
        });
    }

    public void checkGPS() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getActivity())
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
                                status.startResolutionForResult(getActivity(), 1000);
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

    public void showToast(String message){
        View view = rootView.findViewById(R.id.fragmentHeader);
        assert view != null;
        Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        }).show();
    }
}

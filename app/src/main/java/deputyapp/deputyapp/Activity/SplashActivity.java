package deputyapp.deputyapp.Activity;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;

import AlertCustom.OnItemClickListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import deputyapp.deputyapp.Network.ModelManager;
import deputyapp.deputyapp.Network.NetworkManager;
import deputyapp.deputyapp.R;
import deputyapp.deputyapp.Util.BasicEvent;
import deputyapp.deputyapp.dao.AuthorisationSha1;
import deputyapp.deputyapp.dao.Business;

public class SplashActivity extends Activity {

    private static NetworkCapabilities capabilities;

    @BindView(R.id.splashImage)
    ImageView splashImage;

    @BindView(R.id.labelApp)
    TextView labelApp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        if(isNetworkAvailable()){
            generateSHA1();
        }else{
            View view = findViewById(R.id.activity_main);
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

    public void generateSHA1() {
        if (ModelManager.getInstance().getSHA1().size() > 0 && !ModelManager.getInstance().getSHA1().get(0).getSha1().equals("")) {
            //not do anything
        } else {
            final HashCode hashCode = Hashing.sha1().hashString("andrew", Charset.defaultCharset());
            AuthorisationSha1 authorisationSha1 = new AuthorisationSha1();
            authorisationSha1.setSha1(hashCode.toString());
            ModelManager.getInstance().insertSha1ToDB(authorisationSha1);
        }
        NetworkManager.getInstance().getBusiness();
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void onEventMainThread(BasicEvent event) {
        if (event == BasicEvent.BUSINESS_LOADED) {
            Business business = ModelManager.getInstance().getBusiness();
            Picasso.with(this).load(business.getLogo()).into(splashImage);
            labelApp.setText(business.getName());
            new CountDownTimer(2000,10000) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    NetworkManager.getInstance().getPrevShifts();
                }
            }.start();

        } else if(event == BasicEvent.PREV_SHIFTS_LOADED_NULL || event == BasicEvent.PREV_SHIFTS_LOADED){
            startActivity(new Intent(this,MainActivity.class));
        } else if(event == BasicEvent.BUSINESS_LOADED_ERROR || event == BasicEvent.PREV_SHIFTS_LOADED_ERROR){
            //error
            deputyapp.deputyapp.Util.AlertDialog.showDialogWithoutAlertHeader(this, getString(R.string.server_1_technical_message), new OnItemClickListener() {
                @Override
                public void onItemClick(Object o, int position) {

                }
            });
        }
    }
}
package deputyapp.deputyapp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import deputyapp.deputyapp.Network.ModelManager;
import deputyapp.deputyapp.Network.NetworkManager;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class DeputyApplication extends MultiDexApplication {


    private static Context context;
    protected ModelManager modelManager;
    private NetworkManager networkManager;
    private Boolean wasInBackground = false;

    private String stateOfLifeCycle = "";
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.regular_font_path))
                .setFontAttrId(R.attr.fontPath)
                .build());

        modelManager = ModelManager.getInstance();
        networkManager = NetworkManager.getInstance();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static Context getContext() {
        return context;
    }
}

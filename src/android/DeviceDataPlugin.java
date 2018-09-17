package com.algoadtech.devicedata;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.os.Build;
import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageManager;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DeviceDataPlugin extends CordovaPlugin {
    private static final String ACTION_GET_DEVICE_DATA = "getInfo"; 
    private static final String SHARED_KEY_ADVERTISING_ID = "DEVICE_ADVERTISING_ID";
    private static String TAG = "CORDOVA_PLUGIN_DEVICE_DATA";
    
    /**
     * Called after plugin construction and fields have been initialized.
    */
    protected void pluginInitialize() {
        Log.v(TAG, "Plugin Initializing");
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.cordova.getActivity());
        String deviceAdvertiserId = sharedPrefs.getString(SHARED_KEY_ADVERTISING_ID, null);

        if(deviceAdvertiserId == null) {
            deviceAdvertiserId = getAdId(this.cordova.getActivity());
            SharedPreferences.Editor edit = sharedPrefs.edit();
            edit.putString(SHARED_KEY_ADVERTISING_ID, deviceAdvertiserId);
            edit.commit();
        }

        if(action.equals(ACTION_GET_DEVICE_DATA)) {
            Map<String, String> deviceData = new HashMap<String, String>();
            deviceData.put("deviceId", deviceAdvertiserId);
            deviceData.put("os", "Android");
            deviceData.put("osVersion", Build.VERSION.RELEASE);
            deviceData.put("locale", Locale.getDefault().toString());
            deviceData.put("deviceModel", Build.MODEL);
            deviceData.put("buildId", Build.ID);
            deviceData.put("appVersion", getAppVersion());
            
            JSONObject result = new JSONObject(deviceData);
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, result.toString()));

            return true;
        }
        else {
            String errorMessage = "Unknown action: " + action;
            Log.e(TAG, errorMessage );
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, errorMessage));
            return false;
        }
    }

    private String getAdId(Context context) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
            return "";
        }

        AdvertisingIdClient.Info idInfo = null;
        try {
            idInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, "Advertising Client Google Play Unavailable Error: " + e.toString());
        } catch (GooglePlayServicesRepairableException e) {
            Log.e(TAG, "Advertising Client Google Play Needs Repair Error: " + e.toString());
        } catch (IOException e) {
            Log.e(TAG, "Advertising Client Generic Error: " + e.toString());
        }

        String advertId = "";
        if(idInfo != null) {
            advertId = idInfo.getId();
        }

        return advertId;
    }

    private String getAppVersion() {
        String appVersion = "N/A";
        try {
            PackageManager packageManager = this.cordova.getActivity().getPackageManager();
            appVersion = packageManager.getPackageInfo(this.cordova.getActivity().getPackageName(), 0).versionName;
        }
        catch(NameNotFoundException e) {
            Log.e(TAG, "App Version Error: " + e.toString());
        }

        return appVersion;
    }
}
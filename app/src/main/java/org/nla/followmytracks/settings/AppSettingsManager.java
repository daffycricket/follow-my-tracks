package org.nla.followmytracks.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.nla.followmytracks.core.model.AppSettings;

import javax.inject.Singleton;

@Singleton
public class AppSettingsManager {

    public static final String LOCATION_UPDATE_MIN_INTERVAL = "pref_location_update_min_interval";

    private final Context context;
    private final AppSettings appSettings;

    public AppSettingsManager(Context context) {
        this.context = context;
        this.appSettings = new AppSettings();
    }

    private void initSettings() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String minIntervalAsString = prefs.getString(LOCATION_UPDATE_MIN_INTERVAL, "30000");
        appSettings.setLocationUpdateMinInterval(Integer.parseInt(minIntervalAsString));
    }

    public AppSettings getAppSettings() {
        initSettings();
        return appSettings;
    }
}

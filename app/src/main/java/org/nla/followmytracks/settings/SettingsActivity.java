package org.nla.followmytracks.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import org.nla.followmytracks.FollowMyTracksApplication;

import javax.inject.Inject;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    protected AppSettingsManager appSettingsManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        FollowMyTracksApplication.get(this).getComponent().inject(this);
		this.getFragmentManager().beginTransaction()
            .replace(android.R.id.content, new SettingsFragment()).commit();
	}

   @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//       if (key.equals(AppSettingsManager.LOCATION_UPDATE_MIN_INTERVAL)) {
//            appSettings.setLocationUpdateMinInterval(sharedPreferences.getInt(key, 120));
//        }
    }
}

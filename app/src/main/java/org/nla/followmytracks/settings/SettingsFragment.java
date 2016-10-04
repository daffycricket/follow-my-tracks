package org.nla.followmytracks.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import org.nla.followmytracks.R;

public class SettingsFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		this.addPreferencesFromResource(R.xml.preferences);
	}

}

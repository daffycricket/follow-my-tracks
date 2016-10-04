package org.nla.followmytracks.core.model;

import javax.inject.Singleton;

@Singleton
public class AppSettings {

	private int locationUpdateMinInterval;

    public AppSettings(final int locationUpdateMinInterval) {
        this.locationUpdateMinInterval = locationUpdateMinInterval;
    }

    public AppSettings() {
		locationUpdateMinInterval = 300;
	}

	public int getLocationUpdateMinInterval() {
		return this.locationUpdateMinInterval;
	}

	public void setLocationUpdateMinInterval(int locationUpdateMinInterval) {
		this.locationUpdateMinInterval = locationUpdateMinInterval;
	}

	@Override
	public String toString() {
		return "AppSettings{" +
				"locationUpdateMinInterval=" + locationUpdateMinInterval +
				'}';
	}
}

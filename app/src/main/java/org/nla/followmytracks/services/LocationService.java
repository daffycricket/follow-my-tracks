package org.nla.followmytracks.services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.nla.followmytracks.BuildConfig;
import org.nla.followmytracks.FollowMyTracksApplication;
import org.nla.followmytracks.core.WorkoutManager;
import org.nla.followmytracks.core.common.Utils;
import org.nla.followmytracks.core.events.GoogleApiClientConnectedEvent;
import org.nla.followmytracks.core.events.GoogleApiClientConnectionFailedEvent;
import org.nla.followmytracks.core.events.GoogleApiClientConnectionSuspendedEvent;
import org.nla.followmytracks.core.events.LocationReceivedEvent;
import org.nla.followmytracks.core.model.AppSettings;
import org.nla.followmytracks.settings.AppSettingsManager;

import javax.inject.Inject;

public class LocationService extends Service implements LocationListener {

    @Inject protected WorkoutManager workoutManager;
    @Inject protected AppSettingsManager appSettingsManager;
    @Inject protected GoogleApiClient googleApiClient;
    @Inject protected Bus bus;

    private Location currentBestLocation = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

    private boolean checkPermission() {
        boolean isFineLocationGranted = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean isCoarseLocationGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        return !(Build.VERSION.SDK_INT >= 23 && !(isFineLocationGranted || isCoarseLocationGranted));
    }

    @Override
    public void onDestroy() {
        Log.d(Utils.getLogTag(this), "LocationService - onDestroy");
        super.onDestroy();
        bus.unregister(this);
        googleApiClient.disconnect();
    }

	@Override
	public void onCreate() {
		super.onCreate();
        FollowMyTracksApplication.get(this).getComponent().inject(this);
        bus.register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(Utils.getLogTag(this), "LocationService - onStart");
        Log.d(Utils.getLogTag(this), "LocationService - instance" + this.toString());
        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }
        return START_STICKY;
    }

    @Subscribe
    public void onConnected(GoogleApiClientConnectedEvent event) {
        Log.d(Utils.getLogTag(this), "LocationService - connected");
        if (checkPermission()) {
            currentBestLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            publishNewLocation();

            AppSettings appSettings = appSettingsManager.getAppSettings();
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            if (BuildConfig.FAST_LOCATION_UPDATES_FOR_DEBUG) {
                locationRequest.setInterval(1000);
            } else {
                locationRequest.setInterval(appSettings.getLocationUpdateMinInterval());
                locationRequest.setFastestInterval(appSettings.getLocationUpdateMinInterval());

            }

            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                                                                     locationRequest,
                                                                     this);
        }
    }

    @Subscribe
    public void onConnectionSuspended(GoogleApiClientConnectionSuspendedEvent event) {
        Log.d(Utils.getLogTag(this), "LocationService - connection suspended");
    }


    @Subscribe
    public void onConnectionFailed(GoogleApiClientConnectionFailedEvent event) {
        Log.d(Utils.getLogTag(this), "LocationService - connection failed" + event.connectionResult.getErrorMessage());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(Utils.getLogTag(this), "LocationService - location changed");
        currentBestLocation = location;
        publishNewLocation();
    }

    private void publishNewLocation() {
        if (currentBestLocation != null) {
            workoutManager.handleNewLocation(currentBestLocation);
            bus.post(new LocationReceivedEvent(currentBestLocation));
        }
    }
}
package org.nla.followmytracks.services;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.squareup.otto.Bus;

import org.nla.followmytracks.FollowMyTracksApplication;
import org.nla.followmytracks.core.WorkoutManager;
import org.nla.followmytracks.core.common.Utils;
import org.nla.followmytracks.core.events.LocationReceivedEvent;
import org.nla.followmytracks.core.model.AppSettings;
import org.nla.followmytracks.settings.AppSettingsManager;

import javax.inject.Inject;

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    @Inject protected WorkoutManager workoutManager;
    @Inject protected AppSettingsManager appSettingsManager;
    @Inject protected Bus bus;

    private GoogleApiClient googleApiClient;
    private Location mCurrentBestLocation = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		Log.d(Utils.getLogTag(this), "LocationService - onDestroy");
        super.onDestroy();
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
	}

    private boolean checkPermission() {
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

	@Override
	public void onCreate() {
		super.onCreate();
        FollowMyTracksApplication.get(this).getComponent().inject(this);
	}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(Utils.getLogTag(this), "LocationService - onStart");

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            googleApiClient.connect();
        }

        return START_STICKY;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(Utils.getLogTag(this), "LocationService - connected");
        if (checkPermission()) {

            mCurrentBestLocation = LocationServices.FusedLocationApi.getLastLocation(
                    googleApiClient);
            publishNewLocation();

            AppSettings appSettings = appSettingsManager.getAppSettings();
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(appSettings.getLocationUpdateMinInterval());
            locationRequest.setFastestInterval(appSettings.getLocationUpdateMinInterval());
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                                                                     locationRequest,
                                                                     this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(Utils.getLogTag(this), "LocationService - connection suspended: " + i);
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(Utils.getLogTag(this), "LocationService - connection failed: " + connectionResult.getErrorMessage());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(Utils.getLogTag(this), "LocationService - location changed");
        mCurrentBestLocation = location;
        publishNewLocation();
    }

    private void publishNewLocation() {
        if (mCurrentBestLocation != null) {
            workoutManager.handleNewLocation(mCurrentBestLocation);
            bus.post(new LocationReceivedEvent(mCurrentBestLocation));
        }
    }
}
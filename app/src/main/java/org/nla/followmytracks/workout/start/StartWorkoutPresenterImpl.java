package org.nla.followmytracks.workout.start;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.nla.followmytracks.core.JobManager;
import org.nla.followmytracks.core.WorkoutManager;
import org.nla.followmytracks.core.common.Utils;
import org.nla.followmytracks.core.model.Workout;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class StartWorkoutPresenterImpl implements StartWorkoutPresenter, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final WorkoutManager mWorkoutManager;
    private final StartWorkoutView mView;
    private final Bus mBus;
    private final JobManager mJobManager;
    private final List<String> mRecipients = new ArrayList<>();
    private final String mWorkoutName;
    private final Location mDestination;
    private final GoogleApiClient googleApiClient;
    private String mDestinationAddress;

    public StartWorkoutPresenterImpl(
            final WorkoutManager workoutManager,
            final StartWorkoutView view,
            final Bus bus,
            final JobManager jobManager,
            final Context context
    ) {
        this.mWorkoutManager = workoutManager;
        this.mView = view;
        this.mBus = bus;
        this.mWorkoutName = Utils.buildRandomName();
        this.mDestination = new Location("dummy");
        this.mJobManager = jobManager;

        this.googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        this.googleApiClient.connect();
    }

    @Override
    public void start() {
        mBus.register(this);
        googleApiClient.connect();
    }

    @Override
    public void stop() {
        mBus.unregister(this);
        googleApiClient.disconnect();
    }

    @Override
    public void startWorkout(List<String> recipients, double latitude, double longitude, double minDistanceBetweenTwoPoints) {
        mRecipients.addAll(recipients);
        mWorkoutManager.setWorkout(new Workout(mWorkoutName,
                                               TimeZone.getDefault().getID(),
                                               mRecipients,
                                               latitude,
                                               longitude,
                                               mDestinationAddress,
                                               minDistanceBetweenTwoPoints));
        mView.startWorkout();
    }

    @Override
    public String getWorkoutName() {
        return mWorkoutName;
    }

    @Override
    public void reverseGeocodePosition(LatLng position) {
        mJobManager.execute(new ReverseGeocodeLocationRunnable(JobManager.Priority.NORMAL, position));
    }

    @Subscribe
    public void onReverseGeocodedFailed(ReverseGeocodeLocationRunnable.ErrorEvent event) {
        mView.displayReverseGeocodeError();
    }

    @Subscribe
    public void onReverseGeocodedFoundNoAddress(ReverseGeocodeLocationRunnable.NoAddressFoundEvent event) {
        mView.displayReverseGeocodeFoundNoAddress();
    }

    @Subscribe
    public void onReverseGeocodedFoundAddress(ReverseGeocodeLocationRunnable.AddressFoundEvent event) {
        mDestinationAddress = event.address;
        mView.displayAddress(event.address);
    }

    @Override
    public void onConnected(Bundle bundle) {
        try {
            Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            mView.moveToPosition(new LatLng(location.getLatitude(), location.getLongitude()));
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }
}

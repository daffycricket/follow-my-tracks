package org.nla.followmytracks.workout.run;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.nla.followmytracks.R;
import org.nla.followmytracks.core.BaseActivity;
import org.nla.followmytracks.core.model.AppSettings;
import org.nla.followmytracks.core.model.Workout;
import org.nla.followmytracks.core.model.WorkoutPoint;
import org.nla.followmytracks.services.LocationService;
import org.nla.followmytracks.settings.AppSettingsManager;

import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class WorkoutActivity extends BaseActivity implements WorkoutView, OnMapReadyCallback {

    private static final SimpleDateFormat DATE_FORMATER = new SimpleDateFormat("hh:mm:ss",
                                                                               Locale.FRANCE);
    private static final int NOTIFICATION_ID = 42329494;

    @BindView(R.id.btn_start_stop_geo) protected Button btnStartStop;
    @BindView(R.id.txt_points) protected TextView txtPoints;
    @BindView(R.id.txt_time) protected TextView txtTime;
    @BindView(R.id.txt_min_distance) protected TextView txtMinDistance;
    @BindView(R.id.txt_min_interval) protected TextView txtMinInterval;
    @Inject protected WorkoutPresenter workoutPresenter;
    @Inject protected AppSettingsManager appSettingsManager;

    private GoogleMap googleMap;
    private Handler mCustomHandler = new Handler();
    private Intent mLocationServiceIntent;
    private long mStartTime = 0L;
    private long mTimeInMilliseconds = 0L;
    private long mUpdatedTime = 0L;

    private Runnable mUpdateTimerThread = new Runnable() {

        @Override
        public void run() {
            mTimeInMilliseconds = System.currentTimeMillis() - mStartTime;
            mUpdatedTime = mTimeInMilliseconds;

            int secs = (int) (mUpdatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            // int milliseconds = (int) (mUpdatedTime % 1000);
            txtTime.setText("0" + mins + ":" + String.format("%02d", secs));
            // + ":" + String.format("%03d", milliseconds));
            mCustomHandler.postDelayed(this, 0);
        }
    };

    @Override
    public void init(Workout workout) {
        setTitle(workout.getName());
        AppSettings appSettings = appSettingsManager.getAppSettings();
        txtMinDistance.setText(getResources().getString(R.string.txt_min_distance, workout.getMinDistanceBetweenTwoPoints()+""));
        txtMinInterval.setText(getResources().getString(R.string.txt_min_interval,
                                                        appSettings.getLocationUpdateMinInterval()+""));

        if (workout.isInProgress()) {
            restoreWorkoutInProgress(workout);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    @Override
    public void startWorkoutProgress() {
        mStartTime = System.currentTimeMillis();
        mCustomHandler.postDelayed(mUpdateTimerThread, 0);
        btnStartStop.setText("Stop");
    }

    @Override
    public void stopWorkoutProgress() {
        mCustomHandler.removeCallbacks(mUpdateTimerThread);
        stopLocationService();
        finish();
    }

    @Override
    public void pinWorkoutMarkers(Workout workout) {
        googleMap.clear();
        for (WorkoutPoint workoutPoint : workout.getPoints()) {
            googleMap.addMarker(
                    new MarkerOptions()
                            .position(new LatLng(workoutPoint.getLatitude(),
                                                 workoutPoint.getLongitude()))
                            .title(DATE_FORMATER.format(workoutPoint.getTime()))
                            .snippet("I was here !")
            );
            txtPoints.setText(workout.getPoints().size() + "");
        }
    }

    @Override
    public void moveToPosition(Location location) {
        moveToPosition(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startLocationService();
        WorkoutComponent.Initializer.init(this).inject(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        workoutPresenter.init();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        workoutPresenter.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        workoutPresenter.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCustomHandler.removeCallbacks(mUpdateTimerThread);
        stopLocationService();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_map;
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.btn_hide)
    protected void onClickOnHideButton() {
        Intent resultIntent = new Intent(this, WorkoutActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this,
                                                                      0,
                                                                      resultIntent,
                                                                      PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher)
                                                                                 .setContentTitle(
                                                                                         "Follow My Tracks")
                                                                                 .setAutoCancel(true)
                                                                                 .setTicker(
                                                                                         "Follow My Tracks")
                                                                                 .setOngoing(true)
                                                                                 .setContentText(
                                                                                         "Follow My Tracks is running!");
        builder.setContentIntent(resultPendingIntent);

        NotificationManager notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notifyMgr.notify(NOTIFICATION_ID, builder.build());
        moveTaskToBack(true);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.btn_start_stop_geo)
    protected void onClickOnStartStopButton() {
        workoutPresenter.startStop();
    }

    private void startLocationService() {
        mLocationServiceIntent = new Intent(this, LocationService.class);
        startService(mLocationServiceIntent);
    }

    private void stopLocationService() {
        if (mLocationServiceIntent != null) {
            stopService(mLocationServiceIntent);
        }
    }

    private void moveToPosition(LatLng latLng) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(16), 3000, null);
    }

    private void restoreWorkoutInProgress(Workout workout) {
        // move camera to last point and display workout markers
        if (workout.hasPoints()) {
            pinWorkoutMarkers(workout);
            WorkoutPoint workoutPoint = workout.getPoints().get(workout.getPoints().size() - 1);
            moveToPosition(new LatLng(workoutPoint.getLatitude(), workoutPoint.getLongitude()));
        }

        // restart counter
        mStartTime = workout.getTime();
        mCustomHandler.postDelayed(mUpdateTimerThread, 0);
        txtPoints.setText(workout.getPoints().size() + "");
        btnStartStop.setText("Stop");

    }
}
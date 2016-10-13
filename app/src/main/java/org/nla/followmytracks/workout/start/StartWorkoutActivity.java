package org.nla.followmytracks.workout.start;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.nla.followmytracks.BuildConfig;
import org.nla.followmytracks.R;
import org.nla.followmytracks.core.BaseActivity;
import org.nla.followmytracks.core.WorkoutManager;
import org.nla.followmytracks.core.common.Utils;
import org.nla.followmytracks.settings.SettingsActivity;
import org.nla.followmytracks.workout.run.WorkoutActivity;

import java.util.Arrays;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class StartWorkoutActivity extends BaseActivity implements StartWorkoutView {

    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 33;
    private static final int PLACE_PICKER_REQUEST = 3648;

    private final PlacePicker.IntentBuilder placePickerIntentbuilder = new PlacePicker.IntentBuilder();
    protected @BindView(R.id.btn_start) Button btnStart;
    protected @BindView(R.id.txt_recipient) EditText txtRecipient;
    protected @BindView(R.id.txt_destination) TextView txtDestination;

    protected
    @BindView(R.id.txt_min_distance_between_two_points) TextView txtMinDistanceBetweenTwoPoints;
    @Inject WorkoutManager workoutManager;

    @Inject StartWorkoutPresenter presenter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.stop();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.btn_start)
    void onClickOnStartWorkout() {

        final boolean isAccessFineLocationGranted =
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;
        final boolean isSendSMSGranted =
                ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                        == PackageManager.PERMISSION_GRANTED;

        if (! (isAccessFineLocationGranted || isSendSMSGranted)) {
            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                                              new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                              PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            String minDistanceAsString = TextUtils.isEmpty(txtMinDistanceBetweenTwoPoints.getText())
                    ? "1000"
                    : txtMinDistanceBetweenTwoPoints.getText().toString();

            presenter.startWorkout(
                    Arrays.asList(txtRecipient.getText().toString()),
                    (double) Integer.parseInt(minDistanceAsString)
            );

            Intent intent = new Intent(this, WorkoutActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String permissions[], int[] grantResults
    ) {
        switch (requestCode) {
            case PERMISSION_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // TODO : do something interesting...
                    Log.d(Utils.getLogTag(this), "Permission granted");

                } else {
                    Log.d(Utils.getLogTag(this), "Permission not granted");
                }
                return;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StartWorkoutComponent.Initializer.init(this).inject(this);
        txtRecipient.setText(BuildConfig.DEFAULT_PHONE_NUMBER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                presenter.setDestinationPlace(place);
                txtDestination.setText(place.getAddress());
                btnStart.setEnabled(true);
            }
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_test;
    }

    @OnClick(R.id.txt_destination)
    protected void onClickOnPickDestination() {
        try {
            startActivityForResult(placePickerIntentbuilder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        this.startActivity(intent);
    }
}

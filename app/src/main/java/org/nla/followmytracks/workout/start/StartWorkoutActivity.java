package org.nla.followmytracks.workout.start;

import android.app.ActionBar.LayoutParams;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.nla.followmytracks.R;
import org.nla.followmytracks.core.BaseActivity;
import org.nla.followmytracks.core.WorkoutManager;
import org.nla.followmytracks.settings.SettingsActivity;
import org.nla.followmytracks.workout.run.WorkoutActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class StartWorkoutActivity extends BaseActivity implements StartWorkoutView {

    @BindView(R.id.edtRecipient1) protected EditText mEdtRecipient1;
    @BindView(R.id.layoutRecipients) protected ViewGroup mLayoutRecipients;
    @BindView(R.id.txt_address) protected TextView mAddress;
    @BindView(R.id.txt_min_distance_between_two_points) protected TextView mMinDistanceBetweenTwoPoints;
    @BindView(R.id.scrollview) protected ScrollView scrollView;

	@Inject WorkoutManager workoutManager;
    @Inject StartWorkoutPresenter presenter;

    private GoogleMap googleMap;
	private ProgressDialog progressDialog;

    @Override
    public void startWorkout() {
        Intent intent = new Intent(this, WorkoutActivity.class);
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        this.startActivity(intent);
    }

    @Override
    public void displayReverseGeocodeError() {
        mAddress.setText("Unable to retrieve address");
    }

    @Override
    public void displayReverseGeocodeFoundNoAddress() {
        mAddress.setText("No address found at this location");
    }

    @Override
    public void displayAddress(String address) {
        mAddress.setText(address);
    }

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
    @OnClick(R.id.btn_add_recipient)
    void onClickOnAddRecipient() {
        EditText edtRecipient = new EditText(this);
        edtRecipient.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        edtRecipient.setHint("06 07 08 09 10");
        edtRecipient.setInputType(InputType.TYPE_CLASS_PHONE);
        edtRecipient.setText("06 71 59 69 47");
        edtRecipient.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        mLayoutRecipients.addView(edtRecipient);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.btn_remove_last_recipient)
    void onClickOnRemoveLastRecipient() {
        if (mLayoutRecipients.getChildCount() > 1) {
            mLayoutRecipients.removeViewAt(mLayoutRecipients.getChildCount() - 1);
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.btn_start_workout)
    void onClickOnStartWorkout() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("Creating workout " + presenter.getWorkoutName());
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        String minDistanceAsString = TextUtils.isEmpty(mMinDistanceBetweenTwoPoints.getText())
                        ? "500"
                        : mMinDistanceBetweenTwoPoints.getText().toString();

        presenter.startWorkout(
                listRecipients(),
                googleMap.getCameraPosition().target.latitude,
                googleMap.getCameraPosition().target.longitude,
                (double)Integer.parseInt(minDistanceAsString)
        );
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		StartWorkoutComponent.Initializer.init(this).inject(this);
        initUi();
	}

    private void initUi() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);
        mEdtRecipient1.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMapAsync(
                onMapReadyCallback);
    }

    @Override
    public void moveToPosition(LatLng latLng) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(16), 3000, null);
    }

    private OnMapReadyCallback onMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            StartWorkoutActivity.this.googleMap = googleMap;
            StartWorkoutActivity.this.onMapReady();
        }
    };

    private void onMapReady() {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
//        googleMap.addMarker(new MarkerOptions().position(googleMap.getCameraPosition().target));
        googleMap.setOnCameraChangeListener(onCameraChangeListener);

        ((WorkaroundMapFragment) getFragmentManager().findFragmentById(R.id.map))
                .setListener(new WorkaroundMapFragment.OnTouchListener() {
                    @Override
                    public void onTouch() {
                        scrollView.requestDisallowInterceptTouchEvent(true);
                    }
                });
    }

    private GoogleMap.OnCameraChangeListener onCameraChangeListener = new GoogleMap.OnCameraChangeListener() {
        @Override
        public void onCameraChange(CameraPosition cameraPosition) {
            presenter.reverseGeocodePosition(cameraPosition.target);
        }
    };


    @Override
	protected int getLayoutResId() {
		return R.layout.activity_start_workout;
	}

	private void openSettings() {
		Intent intent = new Intent(this, SettingsActivity.class);
		this.startActivity(intent);
	}

	private List<String> listRecipients() {
		List<String> recepients = new ArrayList<>();
		for (int i = 0; i < mLayoutRecipients.getChildCount(); ++i) {
			EditText edtRecipient = (EditText) mLayoutRecipients.getChildAt(i);
			String phoneNumber = edtRecipient.getText().toString();
			recepients.add(phoneNumber.trim());
		}
		return recepients;
	}
}

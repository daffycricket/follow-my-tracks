package org.nla.followmytracks.workout.start;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.nla.followmytracks.R;
import org.nla.followmytracks.core.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity
//        extends BaseActivity
{

//    private static final int PLACE_PICKER_REQUEST = 1;
//    private final PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//
//    protected @BindView(R.id.txtDestination) TextView txtDestination;
//    protected @BindView(R.id.btnStart) Button btnStart;
//    protected @BindView(R.id.txt_recipient) EditText txtRecipient;
//    protected @BindView(R.id.txt_address) TextView txtAddress;
//    protected @BindView(R.id.txt_min_distance_between_two_points) TextView txtMinDistanceBetweenTwoPoints;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    protected int getLayoutResId() {
//        return R.layout.activity_test;
//    }
//
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == PLACE_PICKER_REQUEST) {
//            if (resultCode == RESULT_OK) {
//                Place place = PlacePicker.getPlace(data, this);
////                String toastMsg = String.format("Place: %s", place.getName());
////                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
//                txtDestination.setText(place.getAddress());
//                btnStart.setEnabled(true);
//            }
//        }
//    }
//
//    @OnClick(R.id.txtDestination)
//    protected void onClickOnPickDestination() {
//        try {
//            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
//        } catch (GooglePlayServicesRepairableException e) {
//            e.printStackTrace();
//        } catch (GooglePlayServicesNotAvailableException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @OnClick(R.id.btnStart)
//    protected void onClickOnStart() {
//        Toast.makeText(this, "Start !", Toast.LENGTH_LONG).show();
//
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setTitle("Please wait...");
//        progressDialog.setMessage("Creating workout " + presenter.getWorkoutName());
//        progressDialog.setCancelable(false);
//        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.show();
//
//        String minDistanceAsString = TextUtils.isEmpty(mMinDistanceBetweenTwoPoints.getText())
//                ? "500"
//                : mMinDistanceBetweenTwoPoints.getText().toString();
//
//        presenter.startWorkout(
//                listRecipients(),
//                googleMap.getCameraPosition().target.latitude,
//                googleMap.getCameraPosition().target.longitude,
//                (double)Integer.parseInt(minDistanceAsString)
//        );
//
//    }
}

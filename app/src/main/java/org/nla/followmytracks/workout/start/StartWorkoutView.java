package org.nla.followmytracks.workout.start;

import com.google.android.gms.maps.model.LatLng;

public interface StartWorkoutView {
    void startWorkout();
    void displayReverseGeocodeError();
    void displayReverseGeocodeFoundNoAddress();
    void displayAddress(String address);
    void moveToPosition(LatLng latLng);
}

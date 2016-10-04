package org.nla.followmytracks.workout.start;

import com.google.android.gms.maps.model.LatLng;

import org.nla.followmytracks.core.BusPresenter;

import java.util.List;

public interface StartWorkoutPresenter extends BusPresenter {

    void startWorkout(
            List<String> strings,
            double latitude,
            double longitude,
            double minDistanceBetweenTwoPoints
    );
    String getWorkoutName();
    void reverseGeocodePosition(LatLng position);
}

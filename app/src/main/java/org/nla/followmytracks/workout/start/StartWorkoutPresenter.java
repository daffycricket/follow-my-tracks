package org.nla.followmytracks.workout.start;

import com.google.android.gms.location.places.Place;

import org.nla.followmytracks.core.BusPresenter;

import java.util.List;

public interface StartWorkoutPresenter extends BusPresenter {

    void startWorkout(
            List<String> strings,
            double minDistanceBetweenTwoPoints
    );
    String getWorkoutName();

    void setDestinationPlace(Place place);
}

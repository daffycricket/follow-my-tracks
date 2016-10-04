package org.nla.followmytracks.workout.run;

import android.location.Location;

import org.nla.followmytracks.core.model.Workout;

public interface WorkoutView {

    void pinWorkoutMarkers(Workout workout);

    void moveToPosition(Location location);

    void startWorkoutProgress();

    void stopWorkoutProgress();

    void init(Workout workout);
}

package org.nla.followmytracks.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.google.gson.Gson;

import org.nla.followmytracks.common.NotificationHelper;
import org.nla.followmytracks.core.model.Workout;
import org.nla.followmytracks.core.model.WorkoutPoint;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WorkoutManager {

    private static final String SHARED_PREFERENCES_NAME = "workout-manager";
    private static final String PREFS_WORKOUT = "prefs_workout";

    private final SharedPreferences sharedPreferences;
    private final Gson gson;
    private final NotificationHelper notificationHelper;
    private Workout workout;
    private Location lastLocation;

    @SuppressWarnings("unused")
    @Inject
    public WorkoutManager(
            final Context context,
            final Gson gson,
            final NotificationHelper notificationHelper
    ) {
        this.sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME,
                                                              Context.MODE_PRIVATE);
        this.gson = gson;
        this.notificationHelper = notificationHelper;
    }

    public Workout getWorkout() {
        if (workout == null) {
            readWorkoutFromPrefs();
        }
        return workout;
    }

    public void setWorkout(final Workout workout) {
        if (workout == null) {
            throw new IllegalArgumentException("workout is null");
        }
        if (this.workout != workout) {
            this.workout = workout;
            storeWorkoutToPrefs();
        }
    }

    public void clearWorkout() {
        if (workout != null) {
            workout.deleteObservers();
            workout = null;
            storeWorkoutToPrefs();
        }
        lastLocation = null;
    }

    private void readWorkoutFromPrefs() {
        if (sharedPreferences.contains(PREFS_WORKOUT)) {
            String workoutAsString = sharedPreferences.getString(PREFS_WORKOUT, null);
            workout = gson.fromJson(workoutAsString, Workout.class);
        }
    }

    public void handleNewLocation(Location newLocation) {
        if (!workout.isInProgress()) {
            return;
        }

        if (isNewLocationToAddToWorkout(newLocation)) {
            addPointToWorkout(newLocation);
            notifyRecipientsForNewLocation();
            storeWorkoutToPrefs();
            lastLocation = newLocation;
        }
    }

    private void notifyRecipientsForNewLocation() {
        if (workout.getPoints().size() == 1) {
            notificationHelper.notifyRecipientsWorkoutStarts();
        } else {
            notificationHelper.notifyRecipientsForNewLocation();
        }
    }

    private void addPointToWorkout(Location newLocation) {
        if (workout != null) {
            WorkoutPoint workoutPoint = new WorkoutPoint(
                    newLocation.getLatitude(),
                    newLocation.getLongitude(),
                    newLocation.getTime(),
                    newLocation.getAccuracy(),
                    newLocation.getSpeed(),
                    newLocation.getProvider()
            );
            workout.addPoint(workoutPoint);
        }
    }

    private boolean isNewLocationToAddToWorkout(Location newLocation) {
        return workout != null && (
                lastLocation == null ||
                        newLocation.distanceTo(lastLocation) >= workout.getMinDistanceBetweenTwoPoints()
        );
    }

    private void storeWorkoutToPrefs() {
        if (workout != null) {
            sharedPreferences.edit().putString(PREFS_WORKOUT, gson.toJson(workout)).apply();
        } else {
            sharedPreferences.edit().remove(PREFS_WORKOUT).apply();
        }
    }
}

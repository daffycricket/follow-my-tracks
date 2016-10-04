package org.nla.followmytracks.workout.run;

import android.location.Location;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.nla.followmytracks.core.WorkoutManager;
import org.nla.followmytracks.core.events.LocationReceivedEvent;
import org.nla.followmytracks.core.model.Workout;

public class WorkoutPresenterImpl implements WorkoutPresenter {

    private final WorkoutManager workoutManager;
    private final WorkoutView view;
    private final Bus bus;
    private final Workout workout;

    public WorkoutPresenterImpl(
            WorkoutManager workoutManager,
            WorkoutView view,
            Bus bus
    ) {
        this.workoutManager = workoutManager;
        this.view = view;
        this.bus = bus;
        this.workout = workoutManager.getWorkout();
    }

    @Override
    public void start() {
        bus.register(this);
        if (workout.isInProgress()) {
            view.pinWorkoutMarkers(workout);
        }
    }

    @Override
    public void stop() {
        bus.unregister(this);
    }

    @Subscribe
    public void onReceivedNewLocation(LocationReceivedEvent currentLocationReceivedEvent) {
        Location currentLocation = currentLocationReceivedEvent.location;
        if (workoutManager.getWorkout().isInProgress()) {
            view.pinWorkoutMarkers(workout);
        }
        view.moveToPosition(currentLocation);
    }

    @Override
    public void init() {
        view.init(workout);
    }

    @Override
    public void startStop() {
        if (workout.isNotYetStarted()) {
            startWorkout();
        } else {
            stopWorkout();
        }
    }

    private void stopWorkout() {
        view.stopWorkoutProgress();
        workoutManager.clearWorkout();
    }

    private void startWorkout() {
        workout.setTime(System.currentTimeMillis());
        workout.setStatus(Workout.Status.InProgress);
        view.startWorkoutProgress();

        // TODO
        //new NotificationHelper(this).notifyRecipientsWorkoutStarts();
    }
}

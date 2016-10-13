package org.nla.followmytracks.workout.start;

import android.content.Context;

import com.google.android.gms.location.places.Place;
import com.squareup.otto.Bus;

import org.nla.followmytracks.core.JobManager;
import org.nla.followmytracks.core.WorkoutManager;
import org.nla.followmytracks.core.common.Utils;
import org.nla.followmytracks.core.model.Workout;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class StartWorkoutPresenterImpl implements StartWorkoutPresenter {

    private final WorkoutManager workoutManager;
    private final StartWorkoutView view;
    private final Bus bus;
    private final JobManager jobManager;
    private final List<String> recipients = new ArrayList<>();
    private final String workoutName;
    private Place destinationPlace;

    public StartWorkoutPresenterImpl(
            final WorkoutManager workoutManager,
            final StartWorkoutView view,
            final Bus bus,
            final JobManager jobManager
    ) {
        this.workoutManager = workoutManager;
        this.view = view;
        this.bus = bus;
        this.workoutName = Utils.buildRandomName();
        this.jobManager = jobManager;
    }

    @Override
    public void start() {
        bus.register(this);
    }

    @Override
    public void stop() {
        bus.unregister(this);
    }

    @Override
    public void startWorkout(List<String> recipients, double minDistanceBetweenTwoPoints) {
        this.recipients.addAll(recipients);
        workoutManager.setWorkout(new Workout(workoutName,
                                              TimeZone.getDefault().getID(),
                                              this.recipients,
                                              destinationPlace.getLatLng().latitude,
                                              destinationPlace.getLatLng().longitude,
                                              destinationPlace.getAddress().toString(),
                                              minDistanceBetweenTwoPoints));
    }

    @Override
    public String getWorkoutName() {
        return workoutName;
    }

    public void setDestinationPlace(Place place) {
        destinationPlace = place;
    }

}

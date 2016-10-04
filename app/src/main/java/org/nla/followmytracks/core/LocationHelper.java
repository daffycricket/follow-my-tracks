package org.nla.followmytracks.core;

import com.squareup.otto.Bus;

public class LocationHelper {

    private final Bus bus;
    private final WorkoutManager workoutManager;

    public LocationHelper(Bus bus, WorkoutManager workoutManager) {
        this.bus = bus;
        this.workoutManager = workoutManager;
    }

    void start() {

    }

    void stop() {

    }
}

package org.nla.followmytracks.workout.run;

import org.nla.followmytracks.core.BusPresenter;

public interface WorkoutPresenter extends BusPresenter {

    void startStop();

    void init();
}

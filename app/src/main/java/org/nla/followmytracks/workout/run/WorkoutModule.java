package org.nla.followmytracks.workout.run;

import com.squareup.otto.Bus;

import org.nla.followmytracks.core.ActivityScope;
import org.nla.followmytracks.core.WorkoutManager;

import dagger.Module;
import dagger.Provides;

@Module
public class WorkoutModule {

    private final WorkoutActivity activity;

    public WorkoutModule(WorkoutActivity activity) {this.activity = activity;}

    @Provides
    @ActivityScope
    public WorkoutView providesWorkoutView() {
        return activity;
    }

    @Provides
    @ActivityScope
    public WorkoutPresenter providesWorkoutPresenter(
            final WorkoutManager workoutManager,
            final WorkoutView view,
            final Bus bus
    ) {
        return new WorkoutPresenterImpl(workoutManager, view, bus);
    }
}

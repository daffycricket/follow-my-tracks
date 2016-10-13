package org.nla.followmytracks.workout.start;

import android.content.Context;

import com.squareup.otto.Bus;

import org.nla.followmytracks.core.ActivityScope;
import org.nla.followmytracks.core.JobManager;
import org.nla.followmytracks.core.WorkoutManager;

import dagger.Module;
import dagger.Provides;

@Module
public class StartWorkoutModule {

    private final StartWorkoutActivity activity;

    public StartWorkoutModule(StartWorkoutActivity activity) {this.activity = activity;}

    @Provides
    @ActivityScope
    public StartWorkoutView providesView() {
        return activity;
    }

    @Provides
    @ActivityScope
    public StartWorkoutPresenter providesPresenter(
            final WorkoutManager workoutManager,
            final StartWorkoutView view,
            final Bus bus,
            final JobManager jobManager,
            final Context context
    ) {
        return new StartWorkoutPresenterImpl(workoutManager, view, bus, jobManager);
    }
}

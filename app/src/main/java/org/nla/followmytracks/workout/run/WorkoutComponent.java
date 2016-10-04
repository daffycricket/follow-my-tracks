package org.nla.followmytracks.workout.run;

import org.nla.followmytracks.FollowMyTracksApplication;
import org.nla.followmytracks.FollowMyTracksComponent;
import org.nla.followmytracks.core.ActivityScope;

import dagger.Component;

@ActivityScope
@Component(
        dependencies = FollowMyTracksComponent.class,
        modules = WorkoutModule.class
)
public interface WorkoutComponent {
    final class Initializer {
        static WorkoutComponent init(final WorkoutActivity activity) {
            return DaggerWorkoutComponent
                    .builder()
                    .followMyTracksComponent(FollowMyTracksApplication.get(activity).getComponent())
                    .workoutModule(new WorkoutModule(activity))
                    .build();
        }

        private Initializer() {
            // No instances
        }
    }

    void inject(WorkoutActivity activity);
}

package org.nla.followmytracks.workout.start;

import org.nla.followmytracks.FollowMyTracksApplication;
import org.nla.followmytracks.FollowMyTracksComponent;
import org.nla.followmytracks.core.ActivityScope;

import dagger.Component;

@ActivityScope
@Component(
        dependencies = FollowMyTracksComponent.class,
        modules = StartWorkoutModule.class
)
public interface StartWorkoutComponent {
    final class Initializer {
        static StartWorkoutComponent init(final StartWorkoutActivity activity) {
            return DaggerStartWorkoutComponent
                    .builder()
                    .followMyTracksComponent(FollowMyTracksApplication.get(activity).getComponent())
                    .startWorkoutModule(new StartWorkoutModule(activity))
                    .build();
        }

        private Initializer() {
            // No instances
        }
    }

    void inject(StartWorkoutActivity activity);
}

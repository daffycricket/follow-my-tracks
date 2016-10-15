package org.nla.followmytracks;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.squareup.otto.Bus;

import org.nla.followmytracks.core.JobManager;
import org.nla.followmytracks.core.WorkoutManager;
import org.nla.followmytracks.services.LocationService;
import org.nla.followmytracks.settings.AppSettingsManager;
import org.nla.followmytracks.settings.SettingsActivity;
import org.nla.followmytracks.workout.run.NewLocationNotificationRunnable;
import org.nla.followmytracks.workout.start.CreateWorkoutRunnable;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {FollowMyTracksModule.class}
)
public interface FollowMyTracksComponent {

    void inject(FollowMyTracksApplication followMyTracksApplication);

    void inject(SettingsActivity settingsActivity);

    void inject(LocationService locationService);

    void inject(CreateWorkoutRunnable createWorkoutRunnable);

    void inject(NewLocationNotificationRunnable newLocationNotificationRunnable);

    Application application();

    JobManager jobManager();

    Bus bus();

    AppSettingsManager appSettingsManager();

    WorkoutManager workoutManager();

    Gson gson();

    Context context();

    GoogleApiClient googleApiClient();

    final class Initializer {

        private Initializer() {
            // static class
        }

        static FollowMyTracksComponent init(final FollowMyTracksApplication app) {
            return DaggerFollowMyTracksComponent
                    .builder()
                    .followMyTracksModule(new FollowMyTracksModule(app))
                    .build();
        }
    }
}
package org.nla.followmytracks.workout.run;

import android.util.Log;

import org.nla.followmytracks.FollowMyTracksComponent;
import org.nla.followmytracks.core.JobManager;
import org.nla.followmytracks.core.WorkoutManager;
import org.nla.followmytracks.core.common.SmsNotifier;
import org.nla.followmytracks.core.common.Utils;
import org.nla.followmytracks.core.model.Workout;
import org.nla.followmytracks.core.model.WorkoutPoint;
import org.nla.followmytracks.workout.run.model.Direction;

import java.io.IOException;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class NewLocationNotificationRunnable extends JobManager.PriorityRunnable {

    private static final int MAX_ADDRESS_RESULT = 1;
    @Inject WorkoutManager workoutManager;
    @Inject Retrofit retrofit;
    @Inject SmsNotifier smsNotifier;
    private String distance;
    private String remainingTime;
    private String origin;
    private boolean isStartNotification;

    public NewLocationNotificationRunnable(
            JobManager.Priority priority,
            final boolean isStartNotification
    ) {
        super(priority);
        this.isStartNotification = isStartNotification;
    }

    @Override
    public void inject(final FollowMyTracksComponent component) {
        component.inject(this);
    }

    @Override
    public void run() {
//        String currentAddress = identifyCurrentAddress();
        identifyRemainingTimeAndDistance();

        for (String recipient : workoutManager.getWorkout().getRecipients()) {
            smsNotifier.notify(recipient, isStartNotification, distance, remainingTime, origin);
        }
    }

    private void identifyRemainingTimeAndDistance() {
        Workout workout = workoutManager.getWorkout();
        WorkoutPoint workoutPoint = workout.getLastPoint();
        // TODO Check null value
        origin = workoutPoint.getLatitude() + "," + workoutPoint.getLongitude();
        String destination = workout.getLatitude() + "," + workout.getLongitude();

        Direction direction = null;
        boolean success = false;
        try {
            direction = retrofit.create(Service.class)
                                .getRoute(origin, destination)
                                .execute()
                                .body();
            success = true;
        } catch (IOException e) {
            Log.e(Utils.getLogTag(this), e.getMessage());
        }

        if (success) {
            remainingTime = direction.getRouteList()
                                     .get(0)
                                     .getLegList()
                                     .get(0)
                                     .getDuration()
                                     .text();
            distance = direction.getRouteList().get(0).getLegList().get(0).getDistance().text();
        }
    }

    interface Service {

        @GET("/maps/api/directions/json")
        Call<Direction> getRoute(
                @Query("origin") final String origin,
                @Query("destination") final String destination
        );
    }
}

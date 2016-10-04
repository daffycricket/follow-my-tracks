package org.nla.followmytracks.core.common;

import android.content.Context;

import org.nla.followmytracks.core.JobManager;
import org.nla.followmytracks.workout.run.NewLocationNotificationRunnable;

import javax.inject.Singleton;

@Singleton
public class NotificationHelper {

    private final Context context;
    private final JobManager jobManager;

    public NotificationHelper(final Context context, final JobManager jobManager) {
        this.context = context;
        this.jobManager = jobManager;
    }

    public void notifyRecipientsWorkoutStarts() {
        jobManager.execute(new NewLocationNotificationRunnable(JobManager.Priority.NORMAL, true));
	}

    public void notifyRecipientsForNewLocation() {
        jobManager.execute(new NewLocationNotificationRunnable(JobManager.Priority.NORMAL, false));
    }
}

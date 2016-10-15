package org.nla.followmytracks.common;

import org.nla.followmytracks.core.JobManager;
import org.nla.followmytracks.workout.run.NewLocationNotificationRunnable;

import javax.inject.Singleton;

@Singleton
public class NotificationHelper {

    private final JobManager jobManager;

    public NotificationHelper(final JobManager jobManager) {
        this.jobManager = jobManager;
    }

    public void notifyRecipientsWorkoutStarts() {
        jobManager.execute(new NewLocationNotificationRunnable(JobManager.Priority.NORMAL, true));
    }

    public void notifyRecipientsForNewLocation() {
        jobManager.execute(new NewLocationNotificationRunnable(JobManager.Priority.NORMAL, false));
    }
}

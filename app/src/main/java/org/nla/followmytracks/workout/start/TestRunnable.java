package org.nla.followmytracks.workout.start;

import com.squareup.otto.Bus;

import org.nla.followmytracks.FollowMyTracksComponent;
import org.nla.followmytracks.core.JobManager;

import javax.inject.Inject;

public class TestRunnable extends JobManager.PriorityRunnable {

    @Inject Bus bus;

    public TestRunnable(JobManager.Priority priority) {
        super(priority);
    }

    @Override
    public void inject(final FollowMyTracksComponent component) {
        component.inject(this);
    }

    @Override
    public void run() {
//        Log.d("TestRunnable", "entering run()");
//        SystemClock.sleep(5000);
//        Log.d("TestRunnable", "exiting run()");
//        bus.post(new ResponseEvent());
    }

    public static class ResponseEvent {
    }
}

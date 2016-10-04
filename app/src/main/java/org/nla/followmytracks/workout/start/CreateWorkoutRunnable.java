package org.nla.followmytracks.workout.start;

import com.squareup.otto.Bus;

import org.nla.followmytracks.FollowMyTracksComponent;
import org.nla.followmytracks.core.JobManager;

import javax.inject.Inject;

public class CreateWorkoutRunnable extends JobManager.PriorityRunnable {

    @Inject Bus bus;

    public CreateWorkoutRunnable(JobManager.Priority priority) {
        super(priority);
    }

    @Override
    public void inject(final FollowMyTracksComponent component) {
        component.inject(this);
    }

    @Override
    public void run() {
//        Registration registration = null;
//        if(registration == null) {  // Only do this once
//            Registration.Builder builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(),
//                                                      new AndroidJsonFactory(), null)
//                    // options for running against local devappserver
//                    // - 10.0.2.2 is localhost's IP address in Android emulator
//                    // - turn off compression when running against local devappserver
//                    .setRootUrl("http://10.0.2.2:8080/_ah/api/")
//                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
//                        @Override
//                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
//                            abstractGoogleClientRequest.setDisableGZipContent(true);
//                        }
//                    });
//            // end options for devappserver
//
//            registration = builder.build();
//
//            List<RegistrationRecord> res = null;
//            try {
//                res = registration.listDevices(1).execute().getItems();
//            } catch (IOException e) {
//                Log.e("ko", "ko");
//            } finally {
//                bus.post(res != null ? new ResponseEvent(2l) : new ErrorEvent());
//            }
//        }
    }

    public static class ResponseEvent {

        private final long workoutId;

        public ResponseEvent(final long workoutId) {
            this.workoutId = workoutId;
        }

        public long getWorkoutId() {
            return workoutId;
        }
    }

    public static class ErrorEvent {

    }
}

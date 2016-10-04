package org.nla.followmytracks.workout.start;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.otto.Bus;

import org.nla.followmytracks.FollowMyTracksComponent;
import org.nla.followmytracks.core.JobManager;
import org.nla.followmytracks.core.common.Utils;

import java.util.List;

import javax.inject.Inject;

public class ReverseGeocodeLocationRunnable extends JobManager.PriorityRunnable {

    private static final int MAX_ADDRESS_RESULT = 1;

    @Inject protected Bus bus;
    @Inject protected Geocoder geocoder;

    private final LatLng latLng;

    public ReverseGeocodeLocationRunnable(JobManager.Priority priority, LatLng latLng) {
        super(priority);
        this.latLng = latLng;
    }

    @Override
    public void inject(final FollowMyTracksComponent component) {
        component.inject(this);
    }

    @Override
    public void run() {
        boolean success = false;
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude,
                                                     latLng.longitude,
                                                     MAX_ADDRESS_RESULT);
            success = true;
        } catch (Exception e) {
            Log.e(Utils.getLogTag(this), e.toString());
        } finally {

            if (!success) {
                bus.post(new ErrorEvent());
            }
            else {
                if (addresses == null || addresses.size() == 0) {
                    bus.post(new NoAddressFoundEvent());
                } else {
                    bus.post(new AddressFoundEvent(Utils.transformAddressToSingleLine(addresses.get(0))));
                }
            }
        }
    }

    public static class AddressFoundEvent {
        public final String address;

        public AddressFoundEvent(final String address) {
            this.address = address;
        }
    }

    public static class NoAddressFoundEvent {
    }

    public static class ErrorEvent {
    }
}

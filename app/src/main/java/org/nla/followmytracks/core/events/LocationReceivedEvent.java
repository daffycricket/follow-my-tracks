package org.nla.followmytracks.core.events;

import android.location.Location;

public class LocationReceivedEvent {

    public final Location location;

    public LocationReceivedEvent(Location location) {
        this.location = location;
    }
}

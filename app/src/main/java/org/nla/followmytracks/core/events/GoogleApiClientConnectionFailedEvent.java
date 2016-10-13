package org.nla.followmytracks.core.events;

import com.google.android.gms.common.ConnectionResult;

public class GoogleApiClientConnectionFailedEvent {

    public final ConnectionResult connectionResult;

    public GoogleApiClientConnectionFailedEvent(ConnectionResult connectionResult) {
        this.connectionResult = connectionResult;
    }
}

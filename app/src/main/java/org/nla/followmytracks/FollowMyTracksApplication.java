package org.nla.followmytracks;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class FollowMyTracksApplication extends MultiDexApplication {

    protected FollowMyTracksComponent component;

    public static FollowMyTracksApplication get(final Context context) {
        return ((FollowMyTracksApplication) context.getApplicationContext());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        component = FollowMyTracksComponent.Initializer.init(this);
        component.inject(this);

        Fabric.with(this, new Crashlytics());
    }

    public FollowMyTracksComponent getComponent() {
        return component;
    }
}

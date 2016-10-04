package org.nla.followmytracks.core;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

public class AndroidBus extends Bus {

    private final Handler mainThread = new Handler(Looper.getMainLooper());

    @Override
    public void register(final Object object) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.register(object);
        } else {
            mainThread.post(new Runnable() {
                @Override
                public void run() {
                    AndroidBus.super.register(object);
                }
            });
        }
    }

    @Override
    public void unregister(final Object object) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.unregister(object);
        } else {
            mainThread.post(new Runnable() {
                @Override
                public void run() {
                    AndroidBus.super.unregister(object);
                }
            });
        }
    }

    @Override
    public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            mainThread.post(new Runnable() {
                @Override
                public void run() {
                    AndroidBus.super.post(event);
                }
            });
        }
    }
}
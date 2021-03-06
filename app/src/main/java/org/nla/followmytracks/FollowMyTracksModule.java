package org.nla.followmytracks;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.squareup.otto.Bus;

import org.nla.followmytracks.core.AndroidBus;
import org.nla.followmytracks.core.JobManager;
import org.nla.followmytracks.core.WorkoutManager;
import org.nla.followmytracks.core.common.NotificationHelper;
import org.nla.followmytracks.core.common.SmsNotifier;
import org.nla.followmytracks.core.events.GoogleApiClientConnectedEvent;
import org.nla.followmytracks.core.events.GoogleApiClientConnectionFailedEvent;
import org.nla.followmytracks.core.events.GoogleApiClientConnectionSuspendedEvent;
import org.nla.followmytracks.settings.AppSettingsManager;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Module
public class FollowMyTracksModule  {

    private static final int OK_HTTP_CACHE_SIZE = 10 * 1024 * 1024;

    private final FollowMyTracksApplication app;

    FollowMyTracksModule(final FollowMyTracksApplication app) {
        this.app = app;
    }

    @Provides
    @Singleton
    public Application providesApplication() {
        return app;
    }

    @Provides
    @Singleton
    public Context providesContext() {
        return app.getApplicationContext();
    }

    @Provides
    @Singleton
    public Bus providesBus() {
        return new AndroidBus();
    }

    @Provides
    @Singleton
    public Gson providesGson() {
        return new Gson();
    }

    @Provides
    @Singleton
    public AppSettingsManager providesAppSettingsManager(
            final Context context
    ) {
        return new AppSettingsManager(context);
    }

    @Provides
    @Singleton
    public NotificationHelper providesNotificationHelper(
            final JobManager jobManager
    ) {
        return new NotificationHelper(jobManager);
    }


    @Provides
    @Singleton
    public WorkoutManager providesWorkoutManager(
            final Context context,
            final Gson gson,
            final NotificationHelper notificationHelper
    ) {
        return new WorkoutManager(context, gson, notificationHelper);
    }

    @Provides
    @Singleton
    public SmsNotifier providesSmsNotifier(
            final Context context
    ) {
        return new SmsNotifier(context);
    }

    @Provides
    @Singleton
    public Retrofit providesRetrofit(
            final Context context
    ) {
        final File cacheDirectory = new File(context.getCacheDir().getAbsolutePath(), "http-cache");
        final OkHttpClient.Builder builder = new OkHttpClient.Builder();
        final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        final OkHttpClient client = builder.cache(new Cache(cacheDirectory, OK_HTTP_CACHE_SIZE))
                                           .addInterceptor(logging)
                                           .build();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return new Retrofit.Builder()
                .baseUrl(BuildConfig.GOOGLE_DIRECTIONS_API_BASE_URL)
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();
    }

    @Provides
    @Singleton
    public GoogleApiClient providesGoogleApiClient(
            final Context context,
            final Bus bus) {
        return new GoogleApiClient
                .Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        bus.post(new GoogleApiClientConnectedEvent());
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        bus.post(new GoogleApiClientConnectionSuspendedEvent());
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        bus.post(new GoogleApiClientConnectionFailedEvent(connectionResult));
                    }
                })
                .build();
    }
}

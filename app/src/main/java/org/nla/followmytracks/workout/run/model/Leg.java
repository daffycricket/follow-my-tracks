package org.nla.followmytracks.workout.run.model;

import android.support.annotation.VisibleForTesting;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import auto.parcel.AutoParcel;

@AutoParcel
@JsonDeserialize(builder = AutoParcel_Leg.Builder.class)
public abstract class Leg {

    @VisibleForTesting
    public static Builder builder() {
        return new AutoParcel_Leg.Builder();
    }

    public abstract TextValuePair getDistance();

    public abstract TextValuePair getDuration();

    @AutoParcel.Builder
    public interface Builder {

        @JsonProperty("duration")
        Builder setDuration(final TextValuePair duration);

        @JsonProperty("distance")
        Builder setDistance(final TextValuePair distance);

        Leg build();
    }
}

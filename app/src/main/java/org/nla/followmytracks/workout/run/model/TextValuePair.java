package org.nla.followmytracks.workout.run.model;


import android.support.annotation.VisibleForTesting;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import auto.parcel.AutoParcel;

@AutoParcel
@JsonDeserialize(builder = AutoParcel_TextValuePair.Builder.class)
public abstract class TextValuePair {

    @VisibleForTesting
    public static Builder builder() {
        return new AutoParcel_TextValuePair.Builder();
    }

    public abstract String text();

    public abstract String value();

    @AutoParcel.Builder
    public interface Builder {

        @JsonProperty("text")
        Builder setText(final String text);

        @JsonProperty("value")
        Builder setValue(final String value);

        TextValuePair build();
    }
}

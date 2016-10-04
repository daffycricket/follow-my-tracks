package org.nla.followmytracks.workout.run.model;

import android.support.annotation.VisibleForTesting;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

import auto.parcel.AutoParcel;

@AutoParcel
@JsonDeserialize(builder = AutoParcel_Route.Builder.class)
public abstract class Route {

        @VisibleForTesting
        public static Builder builder() {
            return new AutoParcel_Route.Builder();
        }

        public abstract List<Leg> getLegList();

        @AutoParcel.Builder
        public interface Builder {

            @JsonProperty("legs")
            Builder setLegList(final List<Leg> legList);

            Route build();
        }
}

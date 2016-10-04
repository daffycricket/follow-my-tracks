package org.nla.followmytracks.workout.run.model;

import android.support.annotation.VisibleForTesting;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

import auto.parcel.AutoParcel;

@AutoParcel
@JsonDeserialize(builder = AutoParcel_Direction.Builder.class)
public abstract class Direction {

        @VisibleForTesting
        public static Builder builder() {
            return new AutoParcel_Direction.Builder();
        }

        public abstract String getStatus();

        public abstract List<Route> getRouteList();

        @AutoParcel.Builder
        public interface Builder {

            @JsonProperty("status")
            Builder setStatus(final String status);

            @JsonProperty("routes")
            Builder setRouteList(final List<Route> routeList);

            Direction build();
        }
}

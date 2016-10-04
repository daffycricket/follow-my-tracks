package org.nla.followmytracks.core.model;

public class WorkoutPoint {

	private final double latitude;
	private final double longitude;
	private final long time;
	private final float accuracy;
    private final float speed;
    private final String provider;

    public WorkoutPoint(
            double latitude,
            double longitude,
            long time,
            float accuracy,
            float speed,
            String provider
    ) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
        this.accuracy = accuracy;
        this.speed = speed;
        this.provider = provider;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public long getTime() {
        return time;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public float getSpeed() {
        return speed;
    }

    public String getProvider() {
        return provider;
    }

    @Override
    public String toString() {
        return "WorkoutPoint{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", time=" + time +
                ", accuracy=" + accuracy +
                ", speed=" + speed +
                ", provider='" + provider + '\'' +
                '}';
    }
}
package org.nla.followmytracks.core.model;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Workout extends Observable {

	public enum Status {
		Finished, InProgress, NotYetStarted
	}

	private String id;
	private String name;
	private long time;
	private Status status;
	private String timeZone;
	private List<WorkoutPoint> points = new ArrayList<>();
	private List<String> recipients;
	private double latitude;
    private double longitude;
    private String destination;
    private double minDistanceBetweenTwoPoints;

	public Workout(String name, String timeZone, List<String> recipients, double latitude, double longitude, String destination, double minDistanceBetweenTwoPoints) {
		this.name = name;
		this.timeZone = timeZone;
		this.recipients = new ArrayList<>(recipients);
		this.points = new ArrayList<>();
        this.destination = destination;
		this.latitude = latitude;
		this.longitude = longitude;
		this.status = Status.NotYetStarted;
        this.minDistanceBetweenTwoPoints = minDistanceBetweenTwoPoints;
	}

	public double getMinDistanceBetweenTwoPoints() {
		return minDistanceBetweenTwoPoints;
	}

	public void addPoint(WorkoutPoint workoutPoint) {
        if (workoutPoint == null) {
            throw new IllegalArgumentException("workoutPoint is null");
        }
		points.add(workoutPoint);
        notifyObservers();
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<WorkoutPoint> getPoints() {
		return points;
	}

	public boolean hasPoints() {
		return points.size() > 0;
	}

	public List<String> getRecipients() {
		return recipients;
	}

	public long getTime() {
		return time;
	}

	public Status getStatus() {
		return status;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public boolean isFinished() {
		switch (status) {
		case Finished:
			return true;
		default:
			return false;
		}
	}

	public boolean isInProgress() {
		switch (status) {
		case InProgress:
			return true;
		default:
			return false;
		}
	}

	public boolean isNotYetStarted() {
		switch (status) {
		case NotYetStarted:
			return true;
		default:
			return false;
		}
	}

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setId(String id) {
		this.id = id;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

    public @Nullable WorkoutPoint getLastPoint() {
        if (points.size() == 0) {
            return null;
        }
        else {
            return points.get(points.size() - 1);
        }
    }

    public String getDestination() {
        return destination;
    }

    @Override
    public String toString() {
        return "Workout{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", time=" + time +
                ", status=" + status +
                ", timeZone='" + timeZone + '\'' +
                ", points=" + points +
                ", recipients=" + recipients +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", destination='" + destination + '\'' +
                ", minDistanceBetweenTwoPoints=" + minDistanceBetweenTwoPoints +
                '}';
    }
}
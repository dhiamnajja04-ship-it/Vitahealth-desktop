package com.vitahealth.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Event {
    private int id;
    private String title;
    private String description;
    private LocalDateTime date;
    private double latitude;
    private double longitude;
    private List<Participation> participations = new ArrayList<>();

    public Event() {
        this.date = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public List<Participation> getParticipations() { return participations; }
    public void setParticipations(List<Participation> participations) { this.participations = participations; }

    public void addParticipation(Participation participation) {
        if (!this.participations.contains(participation)) {
            this.participations.add(participation);
            participation.setEvent(this);
        }
    }

    public void removeParticipation(Participation participation) {
        if (this.participations.remove(participation)) {
            if (participation.getEvent() == this) {
                participation.setEvent(null);
            }
        }
    }
}

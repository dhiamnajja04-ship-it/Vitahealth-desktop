package com.vitahealth.entity;

import com.vitahealth.util.Constants;
import java.time.LocalDateTime;

public class Appointment {
    private int id;
    private User patient;
    private User doctor;
    private LocalDateTime date;
    private String reason;
    private String status;

    public Appointment() {
        this.date = LocalDateTime.now();
        this.status = Constants.STATUS_PENDING;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public User getPatient() { return patient; }
    public void setPatient(User patient) { this.patient = patient; }

    public User getDoctor() { return doctor; }
    public void setDoctor(User doctor) { this.doctor = doctor; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

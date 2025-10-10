package com.example.cinema.cinema_app;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "halls")
public class Hall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hall_id")
    private int hallId;

    @Column(name = "number_seats", nullable = false)
    private int numberSeats;

    @Column(name = "hall_type", nullable = false)
    private String hallType;

    @Column(name = "description")
    private String description;

    @Column(name = "has_3d")
    private boolean has3d;

    @Column(name = "has_dolby")
    private boolean hasDolby;

    @Column(name = "screen_size")
    private double screenSize;

    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Session> sessions = new ArrayList<>();

    public Hall() {}

    public Hall(int numberSeats, String hallType, String description,
                boolean has3d, boolean hasDolby, double screenSize) {
        this.numberSeats = numberSeats;
        this.hallType = hallType;
        this.description = description;
        this.has3d = has3d;
        this.hasDolby = hasDolby;
        this.screenSize = screenSize;
    }

    public int getHallId() { return hallId; }
    public void setHallId(int hallId) { this.hallId = hallId; }

    public int getNumberSeats() { return numberSeats; }
    public void setNumberSeats(int numberSeats) { this.numberSeats = numberSeats; }

    public String getHallType() { return hallType; }
    public void setHallType(String hallType) { this.hallType = hallType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isHas3d() { return has3d; }
    public void setHas3d(boolean has3d) { this.has3d = has3d; }

    public boolean isHasDolby() { return hasDolby; }
    public void setHasDolby(boolean hasDolby) { this.hasDolby = hasDolby; }

    public double getScreenSize() { return screenSize; }
    public void setScreenSize(double screenSize) { this.screenSize = screenSize; }

    public List<Session> getSessions() { return sessions; }
    public void setSessions(List<Session> sessions) { this.sessions = sessions; }
}

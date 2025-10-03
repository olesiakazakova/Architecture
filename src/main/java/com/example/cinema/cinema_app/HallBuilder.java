package com.example.cinema.cinema_app;

abstract class HallBuilder {
    protected Hall hall;
    public abstract void buildBase();
    public abstract void buildType();
    public abstract void buildAmenities();
    public abstract void buildScreen();
    public abstract Hall getHall();
}
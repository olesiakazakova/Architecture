package com.example.cinema.cinema_app;

public class DeluxeHallBuilder extends HallBuilder {

    public DeluxeHallBuilder() {
        hall = new Hall();
    }

    @Override
    public Hall buildHall() {
        hall.buildBase(120);
        hall.buildHallType("DELUXE");
        hall.buildDescription("Улучшенный зал Deluxe");
        hall.build3d(true);
        hall.buildDolby(false);
        hall.buildScreenSize(18.0);
        return hall;
    }
}
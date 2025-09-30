package com.example.cinema.cinema_app;

// Строитель для стандартного зала
public class StandardHallBuilder extends HallBuilder {

    public StandardHallBuilder() {
        hall = new Hall();
    }

    @Override
    public Hall buildHall() {
        hall.buildBase(100);
        hall.buildHallType("STANDARD");
        hall.buildDescription("Стандартный кинозал");
        hall.build3d(false);
        hall.buildDolby(false);
        hall.buildScreenSize(10.0);
        return hall;
    }
}


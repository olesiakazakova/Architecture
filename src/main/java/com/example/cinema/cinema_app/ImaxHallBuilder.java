package com.example.cinema.cinema_app;

// Строитель для IMAX зала
public class ImaxHallBuilder extends HallBuilder {

    public ImaxHallBuilder() {
        hall = new Hall();
    }

    @Override
    public Hall buildHall() {
        hall.buildBase(200);
        hall.buildHallType("IMAX");
        hall.buildDescription("Зал с технологией IMAX");
        hall.build3d(true);
        hall.buildDolby(true);
        hall.buildScreenSize(22.0);
        return hall;
    }
}

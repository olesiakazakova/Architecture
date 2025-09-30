package com.example.cinema.cinema_app;

// Строитель для премиум зала
public class PremiumHallBuilder extends HallBuilder {

    public PremiumHallBuilder() {
        hall = new Hall();
    }

    @Override
    public Hall buildHall() {
        hall.buildBase(80);
        hall.buildHallType("PREMIUM");
        hall.buildDescription("Премиальный кинозал");
        hall.build3d(true);
        hall.buildDolby(true);
        hall.buildScreenSize(15.0);
        return hall;
    }
}

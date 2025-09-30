package com.example.cinema.cinema_app;

// Строитель для VIP зала
public class VipHallBuilder extends HallBuilder {

    public VipHallBuilder() {
        hall = new Hall();
    }

    @Override
    public Hall buildHall() {
        hall.buildBase(50);
        hall.buildHallType("VIP");
        hall.buildDescription("VIP зал с повышенным комфортом");
        hall.build3d(true);
        hall.buildDolby(true);
        hall.buildScreenSize(12.0);
        return hall;
    }
}

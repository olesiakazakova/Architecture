package com.example.cinema.cinema_app;

// Строитель для IMAX зала
public class ImaxHallBuilder extends HallBuilder {
    private Hall hall;

    public ImaxHallBuilder() {
        this.hall = new Hall();
    }

    @Override
    public Hall buildHall() {
        hall.setNumberSeats(200);
        hall.setHallType("IMAX");
        hall.setDescription("Зал с передовой технологией IMAX для максимального погружения");
        hall.setHas3d(true);
        hall.setHasDolby(true);
        hall.setScreenSize(22.0);
        return hall;
    }
}

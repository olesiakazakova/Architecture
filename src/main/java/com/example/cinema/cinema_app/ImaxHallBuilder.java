package com.example.cinema.cinema_app;

// Строитель для IMAX зала
public class ImaxHallBuilder extends HallBuilder {
    private Hall hall;

    public ImaxHallBuilder() {
        this.hall = new Hall();
    }

    @Override
    public void buildBase() {
        hall.setNumberSeats(200);
    }

    @Override
    public void buildType() {
        hall.setHallType("IMAX");
        hall.setDescription("Зал с передовой технологией IMAX для максимального погружения");
    }

    @Override
    public void buildAmenities() {
        hall.setHas3d(true);
        hall.setHasDolby(true);
    }

    @Override
    public void buildScreen() {
        hall.setScreenSize(22.0);
    }

    @Override
    public Hall getHall() {
        return hall;
    }
}

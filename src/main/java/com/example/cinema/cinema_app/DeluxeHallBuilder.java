package com.example.cinema.cinema_app;

public class DeluxeHallBuilder extends HallBuilder {
    private Hall hall;

    public DeluxeHallBuilder() {
        this.hall = new Hall();
    }

    @Override
    public void buildBase() {
        hall.setNumberSeats(120);
    }

    @Override
    public void buildType() {
        hall.setHallType("DELUXE");
        hall.setDescription("Улучшенный зал Deluxe с оптимальным соотношением цены и качества");
    }

    @Override
    public void buildAmenities() {
        hall.setHas3d(true);
        hall.setHasDolby(false);
    }

    @Override
    public void buildScreen() {
        hall.setScreenSize(18.0);
    }

    @Override
    public Hall getHall() {
        return hall;
    }
}
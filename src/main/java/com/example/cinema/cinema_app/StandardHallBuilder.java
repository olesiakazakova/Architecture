package com.example.cinema.cinema_app;

// Строитель для стандартного зала
public class StandardHallBuilder extends HallBuilder {
    private Hall hall;

    public StandardHallBuilder() {
        this.hall = new Hall();
    }

    @Override
    public void buildBase() {
        hall.setNumberSeats(100);
    }

    @Override
    public void buildType() {
        hall.setHallType("STANDARD");
        hall.setDescription("Стандартный кинозал");
    }

    @Override
    public void buildAmenities() {
        hall.setHas3d(false);
        hall.setHasDolby(false);
    }

    @Override
    public void buildScreen() {
        hall.setScreenSize(10.0);
    }

    @Override
    public Hall getHall() {
        return hall;
    }
}


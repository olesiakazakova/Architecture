package com.example.cinema.cinema_app;

// Строитель для VIP зала
public class VipHallBuilder extends HallBuilder {
    private Hall hall;

    public VipHallBuilder() {
        this.hall = new Hall();
    }

    @Override
    public void buildBase() {
        hall.setNumberSeats(50);
    }

    @Override
    public void buildType() {
        hall.setHallType("VIP");
        hall.setDescription("VIP зал с повышенным комфортом");
    }

    @Override
    public void buildAmenities() {
        hall.setHas3d(true);
        hall.setHasDolby(true);
    }

    @Override
    public void buildScreen() {
        hall.setScreenSize(12.0);
    }

    @Override
    public Hall getHall() {
        return hall;
    }
}

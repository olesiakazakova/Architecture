package com.example.cinema.cinema_app;

// Строитель для премиум зала
public class PremiumHallBuilder extends HallBuilder {
    private Hall hall;

    public PremiumHallBuilder() {
        this.hall = new Hall();
    }

    @Override
    public void buildBase() {
        hall.setNumberSeats(80);
    }

    @Override
    public void buildType() {
        hall.setHallType("PREMIUM");
        hall.setDescription("Премиальный кинозал с улучшенным комфортом и обслуживанием");
    }

    @Override
    public void buildAmenities() {
        hall.setHas3d(true);
        hall.setHasDolby(true);
    }

    @Override
    public void buildScreen() {
        hall.setScreenSize(15.0);
    }

    @Override
    public Hall getHall() {
        return hall;
    }
}
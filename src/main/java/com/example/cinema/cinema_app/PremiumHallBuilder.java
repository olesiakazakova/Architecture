package com.example.cinema.cinema_app;

// Строитель для премиум зала
public class PremiumHallBuilder extends HallBuilder {
    private Hall hall;

    public PremiumHallBuilder() {
        this.hall = new Hall();
    }

    @Override
    public Hall buildHall() {
        hall.setNumberSeats(80);
        hall.setHallType("PREMIUM");
        hall.setDescription("Премиальный кинозал с улучшенным комфортом и обслуживанием");
        hall.setHas3d(true);
        hall.setHasDolby(true);
        hall.setScreenSize(15.0);
        return hall;
    }
}
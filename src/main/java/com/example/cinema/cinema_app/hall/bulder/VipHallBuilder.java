package com.example.cinema.cinema_app.hall.bulder;

import com.example.cinema.cinema_app.hall.Hall;

// Строитель для VIP зала
public class VipHallBuilder extends HallBuilder {
    private Hall hall;

    public VipHallBuilder() {
        this.hall = new Hall();
    }

    @Override
    public Hall buildHall() {
        hall.setNumberSeats(50);
        hall.setHallType("VIP");
        hall.setDescription("VIP зал с повышенным комфортом");
        hall.setHas3d(true);
        hall.setHasDolby(true);
        hall.setScreenSize(12.0);
        return hall;
    }
}

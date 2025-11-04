package com.example.cinema.cinema_app.hall.bulder;

import com.example.cinema.cinema_app.hall.Hall;

public class DeluxeHallBuilder extends HallBuilder {
    private Hall hall;

    public DeluxeHallBuilder() {
        this.hall = new Hall();
    }

    @Override
    public Hall buildHall() {
        hall.setNumberSeats(120);
        hall.setHallType("DELUXE");
        hall.setDescription("Улучшенный зал Deluxe с оптимальным соотношением цены и качества");
        hall.setHas3d(true);
        hall.setHasDolby(false);
        hall.setScreenSize(18.0);
        return hall;
    }
}
package com.example.cinema.cinema_app.hall.bulder;

import com.example.cinema.cinema_app.hall.Hall;

// Строитель для стандартного зала
public class StandardHallBuilder extends HallBuilder {
    private Hall hall;

    public StandardHallBuilder() {
        this.hall = new Hall();
    }

    @Override
    public Hall buildHall() {
        hall.setNumberSeats(100);
        hall.setHallType("STANDARD");
        hall.setDescription("Стандартный кинозал");
        hall.setHas3d(false);
        hall.setHasDolby(false);
        hall.setScreenSize(10.0);
        return hall;
    }
}


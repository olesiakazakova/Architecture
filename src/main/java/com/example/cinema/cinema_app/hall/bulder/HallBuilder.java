package com.example.cinema.cinema_app.hall.bulder;

import com.example.cinema.cinema_app.hall.Hall;

abstract class HallBuilder {
    protected Hall hall;

    public abstract Hall buildHall();
}
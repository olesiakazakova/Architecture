package com.example.cinema.cinema_app;

public class HallDirector {
    private HallBuilder builder;

    public HallDirector(int hallType) {
        switch (hallType) {
            case 1: builder = new StandardHallBuilder(); break;
            case 2: builder = new VipHallBuilder(); break;
            case 3: builder = new ImaxHallBuilder(); break;
            case 4: builder = new PremiumHallBuilder(); break;
            case 5: builder = new DeluxeHallBuilder(); break;
            default: builder = new StandardHallBuilder();
        }
    }

    public Hall buildHall() {
        return builder.buildHall();
    }
}
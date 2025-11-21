package com.example.cinema.cinema_app.tests;

import com.example.cinema.cinema_app.hall.bulder.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// --- ТЕСТИРОВАНИЕ ДЛЯ Hall ---

public class HallUnitTest {
    // --- ТЕСТИРОВАНИЕ ДЛЯ HallDirector ---

    // --- ТЕСТЫ ДЛЯ HallDirector ---

    @Test
    void testBuilderSelection_Standard() {
        HallDirector director = new HallDirector(1);
        assertTrue(director.getBuilder() instanceof StandardHallBuilder);
    }

    @Test
    void testBuilderSelection_Vip() {
        HallDirector director = new HallDirector(2);
        assertTrue(director.getBuilder() instanceof VipHallBuilder);
    }

    @Test
    void testBuilderSelection_Imax() {
        HallDirector director = new HallDirector(3);
        assertTrue(director.getBuilder() instanceof ImaxHallBuilder);
    }

    @Test
    void testBuilderSelection_Premium() {
        HallDirector director = new HallDirector(4);
        assertTrue(director.getBuilder() instanceof PremiumHallBuilder);
    }

    @Test
    void testBuilderSelection_Deluxe() {
        HallDirector director = new HallDirector(5);
        assertTrue(director.getBuilder() instanceof DeluxeHallBuilder);
    }

    @Test
    void testBuilderSelection_Default_WhenTypeLessThan1() {
        HallDirector director = new HallDirector(0);
        assertTrue(director.getBuilder() instanceof StandardHallBuilder);
    }

    // если больше 5 или меньше 1, то возвращается к стандартному

    @Test
    void testBuilderSelection_Default_WhenTypeGreaterThan5() {
        HallDirector director = new HallDirector(6);
        assertTrue(director.getBuilder() instanceof StandardHallBuilder);
    }

    @Test
    void testBuilderSelection_Default_NegativeType() {
        HallDirector director = new HallDirector(-1);
        assertTrue(director.getBuilder() instanceof StandardHallBuilder);
    }
}
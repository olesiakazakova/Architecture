package com.example.cinema.cinema_app.hall;

import com.example.cinema.cinema_app.hall.bulder.HallDirector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class HallService {

    @Autowired
    private HallRepository hallRepository;

    public List<Hall> findAll() {
        return hallRepository.findAll();
    }

    public Hall findById(int id) {
        return hallRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid hall Id:" + id));
    }

    public Hall save(Hall hall) {
        return hallRepository.save(hall);
    }

    public void delete(int id) {
        hallRepository.deleteById(id);
    }

    public Hall createHallByType(int hallType) {
        HallDirector director = new HallDirector(hallType);
        Hall hall = director.buildHall();
        return hallRepository.save(hall);
    }

    public Hall updateHall(int id, Hall hallData) {
        Hall existingHall = findById(id);

        existingHall.setNumberSeats(hallData.getNumberSeats());
        existingHall.setHallType(hallData.getHallType());
        existingHall.setDescription(hallData.getDescription());
        existingHall.setHas3d(hallData.isHas3d());
        existingHall.setHasDolby(hallData.isHasDolby());
        existingHall.setScreenSize(hallData.getScreenSize());

        return hallRepository.save(existingHall);
    }

    public void deleteWithValidation(int id) {
        Hall hall = findById(id);

        if (!hall.getSessions().isEmpty()) {
            throw new IllegalStateException("Невозможно удалить зал, так как есть связанные сеансы");
        }

        hallRepository.deleteById(id);
    }

    public List<String> getAvailableHallTypes() {
        return Arrays.asList(
                "1 - STANDARD (100 мест, без 3D, экран 10м)",
                "2 - VIP (50 мест, 3D, Dolby, экран 12м)",
                "3 - IMAX (200 мест, 3D, Dolby, экран 22м)",
                "4 - PREMIUM (80 мест, 3D, Dolby, экран 15м)",
                "5 - DELUXE (120 мест, 3D, экран 18м)"
        );
    }
}
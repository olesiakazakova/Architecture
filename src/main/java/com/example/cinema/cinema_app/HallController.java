package com.example.cinema.cinema_app;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/halls")
public class HallController {

    @Autowired
    private HallRepository hallRepository;

    @GetMapping()
    public String getAllHalls(Model model) {
        List<Hall> halls = hallRepository.findAll();
        model.addAttribute("halls", halls);
        return "halls/listHalls";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("hallTypes", getHallTypes());
        return "halls/addHall";
    }

    @PostMapping("/add")
    public String addHall(@RequestParam int hallType, Model model) {
        try {
            // Создаем HallDirector напрямую, без Spring DI
            HallDirector director = new HallDirector(hallType);
            Hall hall = director.buildHall();

            hallRepository.save(hall);
            return "redirect:/halls?success";

        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при создании зала: " + e.getMessage());
            model.addAttribute("hallTypes", getHallTypes());
            return "halls/addHall";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model) {
        Hall hall = hallRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid hall Id:" + id));
        model.addAttribute("hall", hall);
        model.addAttribute("hallTypes", getHallTypes());
        return "halls/editHall";
    }

    @PostMapping("/edit/{id}")
    public String updateHall(@PathVariable int id, @ModelAttribute Hall hall,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("hallTypes", getHallTypes());
            return "halls/editHall";
        }

        try {
            Hall existingHall = hallRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid hall Id:" + id));

            // Обновляем поля
            existingHall.setNumberSeats(hall.getNumberSeats());
            existingHall.setHallType(hall.getHallType());
            existingHall.setDescription(hall.getDescription());
            existingHall.setHas3d(hall.isHas3d());
            existingHall.setHasDolby(hall.isHasDolby());
            existingHall.setScreenSize(hall.getScreenSize());

            hallRepository.save(existingHall);
            return "redirect:/halls?success";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при обновлении зала: " + e.getMessage());
            model.addAttribute("hallTypes", getHallTypes());
            return "halls/editHall";
        }
    }

    @GetMapping("/details/{id}")
    public String showHallDetails(@PathVariable int id, Model model) {
        Hall hall = hallRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid hall Id:" + id));
        model.addAttribute("hall", hall);
        return "halls/hallDetails";
    }

    @PostMapping("/delete/{id}")
    public String deleteHall(@PathVariable int id, Model model) {
        try {
            Hall hall = hallRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid hall Id:" + id));

            if (!hall.getSessions().isEmpty()) {
                model.addAttribute("error", "Невозможно удалить зал, так как есть связанные сеансы");
                return "redirect:/halls?error";
            }

            hallRepository.deleteById(id);
            return "redirect:/halls?deleteSuccess";
        } catch (Exception e) {
            return "redirect:/halls?error";
        }
    }

    // Вспомогательный метод для получения типов залов
    private List<String> getHallTypes() {
        return Arrays.asList(
                "1 - STANDARD (100 мест, без 3D, экран 10м)",
                "2 - VIP (50 мест, 3D, Dolby, экран 12м)",
                "3 - IMAX (200 мест, 3D, Dolby, экран 22м)",
                "4 - PREMIUM (80 мест, 3D, Dolby, экран 15м)",
                "5 - DELUXE (120 мест, 3D, экран 18м)"
        );
    }

    // Дополнительные методы для API
    @GetMapping("/api")
    @ResponseBody
    public List<Hall> getAllHallsApi() {
        return hallRepository.findAll();
    }

    @GetMapping("/api/type/{type}")
    @ResponseBody
    public List<Hall> getHallsByType(@PathVariable String type) {
        return hallRepository.findByHallType(type);
    }

    @GetMapping("/api/3d")
    @ResponseBody
    public List<Hall> get3DHalls() {
        return hallRepository.findByHas3dTrue();
    }

    // API метод для создания зала через Builder
    @PostMapping("/api/create")
    @ResponseBody
    public ResponseEntity<?> createHallApi(@RequestParam int type) {
        try {
            HallDirector director = new HallDirector(type);
            Hall hall = director.buildHall();
            Hall savedHall = hallRepository.save(hall);
            return ResponseEntity.ok(savedHall);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Ошибка при создании зала: " + e.getMessage());
        }
    }
}
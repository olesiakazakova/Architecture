package com.example.cinema.cinema_app.hall;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/halls")
public class HallController {

    @Autowired
    private HallService hallService;

    @GetMapping()
    public String getAllHalls(Model model) {
        List<Hall> halls = hallService.findAll();
        model.addAttribute("halls", halls);
        return "hall/listHalls";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("hallTypes", hallService.getAvailableHallTypes());
        return "hall/addHall";
    }

    @PostMapping("/add")
    public String addHall(@RequestParam int hallType, Model model) {
        try {
            hallService.createHallByType(hallType);
            return "redirect:/halls?success";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при создании зала: " + e.getMessage());
            model.addAttribute("hallTypes", hallService.getAvailableHallTypes());
            return "hall/addHall";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model) {
        Hall hall = hallService.findById(id);
        model.addAttribute("hall", hall);
        model.addAttribute("hallTypes", hallService.getAvailableHallTypes());
        return "hall/editHall";
    }

    @PostMapping("/edit/{id}")
    public String updateHall(@PathVariable int id, @ModelAttribute Hall hall,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("hallTypes", hallService.getAvailableHallTypes());
            return "hall/editHall";
        }

        try {
            hallService.updateHall(id, hall);
            return "redirect:/halls?success";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при обновлении зала: " + e.getMessage());
            model.addAttribute("hallTypes", hallService.getAvailableHallTypes());
            return "hall/editHall";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteHall(@PathVariable int id, Model model) {
        try {
            hallService.deleteWithValidation(id);
            return "redirect:/halls?deleteSuccess";
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/halls?error";
        } catch (Exception e) {
            return "redirect:/halls?error";
        }
    }
}
package com.example.cinema.cinema_app.director;

import com.example.cinema.cinema_app.film.FilmsDirectors;
import com.example.cinema.cinema_app.film.service.FilmsDirectorsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/directors")
public class DirectorController {
    @Autowired
    private DirectorService directorService;

    @Autowired
    private FilmsDirectorsService filmsDirectorsService;

    @GetMapping
    public String listDirectors(Model model) {
        List<Director> directors = directorService.getAllDirectors();
        model.addAttribute("directors", directors);
        return "director/listDirectors";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("director", new Director());
        return "director/addDirectors";
    }

    @PostMapping("/add")
    public String addDirector(@Valid @ModelAttribute Director director, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            System.out.println("Ошибки валидации: " + bindingResult.getAllErrors());
            return "film/error";
        }
        if (!directorService.findByName(director.getName()).isPresent())
            directorService.saveDirector(director);
        return "redirect:/directors";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("directorId") Long directorId, Model model) {
        Director director = directorService.getDirectorById(directorId);
        model.addAttribute("director", director);
        return "director/editDirectors";
    }

    @PostMapping("/edit")
    public String editDirector(@RequestParam Long directorId, @Valid @ModelAttribute Director director,  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            System.out.println("Ошибки валидации: " + bindingResult.getAllErrors());
            return "film/error";
        }
        directorService.updateDirector(directorId, director);
        return "redirect:/directors";
    }

    @PostMapping("/delete")
    public String deleteDirector(@RequestParam Long directorId, @ModelAttribute Director director) {
        if (director != null) {
            List<FilmsDirectors> films = director.getFilmsDirectors();
            if (films!=null) {
                for (FilmsDirectors film : films) {
                    Long filmId = film.getFilm().getFilmId();
                    filmsDirectorsService.deleteFilmsDirectors(directorId, filmId);
                }
            }
            directorService.deleteDirector(directorId);
        }
        return "redirect:/directors";
    }
}

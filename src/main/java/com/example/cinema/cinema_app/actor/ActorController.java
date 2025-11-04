package com.example.cinema.cinema_app.actor;

import com.example.cinema.cinema_app.film.FilmsActors;
import com.example.cinema.cinema_app.film.service.FilmsActorsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/actors")
public class ActorController {
    @Autowired
    private ActorService actorService;

    @Autowired
    private FilmsActorsService filmsActorsService;

    @GetMapping
    public String listActors(Model model) {
        List<Actor> actors = actorService.getAllActors();
        model.addAttribute("actors", actors);
        return "actor/listActors";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("actor", new Actor());
        return "actor/addActor";
    }

    @PostMapping("/add")
    public String addActor(@Valid @ModelAttribute Actor actor,  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            System.out.println("Ошибки валидации: " + bindingResult.getAllErrors());
            return "film/error";
        }
        if (!actorService.findByName(actor.getName()).isPresent())
            actorService.createActor(actor);
        return "redirect:/actors";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("actorId") Long actorId, Model model) {
        Optional<Actor> optionalActor = actorService.getActorById(actorId);
        if (optionalActor.isPresent()) {
            Actor actor = optionalActor.get();
            model.addAttribute("actor", actor);
            return "actor/editActor";
        }
        else return "film/error";
    }

    @PostMapping("/edit")
    public String editActor(@RequestParam Long actorId, @Valid @ModelAttribute Actor actor, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            System.out.println("Ошибки валидации: " + bindingResult.getAllErrors());
            return "film/error";
        }
        actorService.updateActor(actorId, actor);
        return "redirect:/actors";
    }

    @PostMapping("/delete")
    public String deleteActor(@RequestParam Long actorId, @ModelAttribute Actor actor) {
        if (actor != null) {
            List<FilmsActors> films = actor.getFilmsActors();
            if (films!=null) {
                for (FilmsActors film : films) {
                    Long filmId = film.getFilm().getFilmId();
                    filmsActorsService.deleteFilmsActors(actorId, filmId);
                }
            }
            actorService.deleteActor(actorId);
        }
        return "redirect:/actors?deleted=true";
    }

}



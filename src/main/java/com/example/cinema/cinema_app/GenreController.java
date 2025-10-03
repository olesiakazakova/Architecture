package com.example.cinema.cinema_app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/genres")
public class GenreController {
    @Autowired
    private GenreService genreService;

    @Autowired
    private FilmsGenresService filmsGenresService;

    @Autowired
    private GenreService genreRepository;

    @GetMapping
    public String listGenres(Model model) {
        List<Genre> genres = genreService.findAll();
        model.addAttribute("genres", genres);
        return "film/listGenres";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("genre", new Genre());
        return "film/addGenres";
    }

    @PostMapping("/add")
    public String addGenre(@RequestParam(required = false) String newGenre) {
        if (newGenre != null && !newGenre.trim().isEmpty()) {
            Genre genre = genreService.findByName(newGenre)
                    .orElseGet(() -> {
                        Genre newGenreObj = new Genre();
                        newGenreObj.setGenre(newGenre);
                        genreService.createGenre(newGenreObj);
                        return newGenreObj;
                    });
        } else return "film/error";
        return "redirect:/genres";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("genreId") Long genreId, Model model) {
        Optional<Genre> optionalGenre = genreService.getGenre(genreId);
        if (optionalGenre.isPresent()) {
            Genre genre = optionalGenre.get();
            model.addAttribute("genre", genre);
            return "film/editGenre";
        }
        return "films/error";
    }

    @PostMapping("/edit")
    public String editGenre(@RequestParam Long genreId, @RequestParam(required = false) String genre) {
        Optional<Genre> optionalGenre = genreService.getGenre(genreId);
        if (optionalGenre.isPresent()) {
            Genre newGenre = optionalGenre.get();
            newGenre.setGenre(genre);
            genreService.updateGenre(genreId, newGenre);
            return "redirect:/genres";
        }
        return "films/error";
    }

    @PostMapping("/delete")
    public String deleteGenre(@RequestParam Long genreId) {
        Optional<Genre> optionalGenre = genreService.getGenre(genreId);
        if (optionalGenre.isPresent()) {
            Genre genre = optionalGenre.get();

            filmsGenresService.deleteAllByGenre(genre);

            genreService.deleteGenre(genreId);
        }
        return "redirect:/genres";
    }
}





package com.example.cinema.cinema_app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class GenreService {
    @Autowired
    private GenreRepository genreRepository;

    public Genre createGenre(Genre genre) {
        return genreRepository.save(genre);
    }

    public Optional<Genre> getGenre(Long id) {
        return genreRepository.findById(id);
    }

    public void deleteGenre(Long id) {
        genreRepository.deleteById(id);
    }

    public List<Genre> findAll() {
        return genreRepository.findAll();
    }

    public Optional<Genre> findByName(String genre) {
        return genreRepository.findByGenre(genre);
    }

    public List<Genre> findAllById(List<Long> selectedGenres) {
        return genreRepository.findAllById(selectedGenres);
    }

    public Genre updateGenre(Long genreId,Genre genreDetails) {
        return genreRepository.findById(genreId)
                .map(genre -> {
                    genre.setGenre(genreDetails.getGenre());
                    genre.setFilmsGenres(genreDetails.getFilmsGenres());
                    return genreRepository.save(genre);
                }).orElse(null);
    }
}
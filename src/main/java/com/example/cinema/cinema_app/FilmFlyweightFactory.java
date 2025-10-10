package com.example.cinema.cinema_app;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class FilmFlyweightFactory {
    private final Map<Long, Film> filmCache = new ConcurrentHashMap<>();

    public Film getFilm(Long filmId) {
        return filmCache.get(filmId);
    }

    public void putFilm(Film film) {
        if (film != null && film.getFilmId() != null) {
            filmCache.put(film.getFilmId(), film);
        }
    }

    public Film getOrLoadFilm(Long filmId, FilmService filmService) {
        return filmCache.computeIfAbsent(filmId, id -> filmService.findById(id));
    }

    public void preloadFilms(List<Film> films) {
        films.forEach(film -> filmCache.put(film.getFilmId(), film));
    }

    public void removeFilm(Long filmId) {
        filmCache.remove(filmId);
    }

    public void clearCache() {
        filmCache.clear();
    }

    public int getCacheSize() {
        return filmCache.size();
    }
}
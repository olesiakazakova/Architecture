package com.example.cinema.cinema_app.film;

import com.example.cinema.cinema_app.actor.Actor;
import jakarta.persistence.*;

@Entity
@Table(name = "films_actors")
public class FilmsActors {

    @EmbeddedId
    private FilmsActorsId id;

    @ManyToOne
    @MapsId("actorId")
    @JoinColumn(name = "actor_id", nullable = false)
    private Actor actor;

    @ManyToOne
    @MapsId("filmId")
    @JoinColumn(name = "film_id", nullable = false)
    private Film film;

    public FilmsActorsId getId() {
        return id;
    }

    public void setId(FilmsActorsId id) {
        this.id = id;
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
        if (this.id == null) {
            this.id = new FilmsActorsId();
        }
        this.id.setActor(actor.getActorId());
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
        if (this.id == null) {
            this.id = new FilmsActorsId();
        }
        this.id.setFilm(film.getFilmId());
    }
}
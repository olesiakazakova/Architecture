package com.example.cinema.cinema_app.actor;

import com.example.cinema.cinema_app.film.FilmsActors;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "actors")
public class Actor {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "actor_seq", sequenceName = "actor_sequence", allocationSize = 1)
    private Long actorId;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "actor", cascade = CascadeType.PERSIST)
    private List<FilmsActors> filmsActors;

    public Long getActorId() {
        return actorId;
    }

    public String getName() {
        return name;
    }

    public List<FilmsActors> getFilmsActors() {
        return filmsActors;
    }

    public void setId(Long actorId) {
        this.actorId =actorId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFilmsActors(List<FilmsActors> filmsActors) {
        this.filmsActors = filmsActors;
    }
}


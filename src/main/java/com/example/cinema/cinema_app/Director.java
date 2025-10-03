package com.example.cinema.cinema_app;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "directors")
public class Director {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "director_seq", sequenceName = "director_sequence", allocationSize = 1)
    private Long directorId;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "director", cascade = CascadeType.PERSIST)
    private List<FilmsDirectors> filmsDirectors;

    public Long getDirectorId() {
        return directorId;
    }

    public void setDirectorId(Long directorId) {
        this.directorId = directorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FilmsDirectors> getFilmsDirectors() {
        return filmsDirectors;
    }

    public void setFilmsDirectors(List<FilmsDirectors> filmsDirectors) {
        this.filmsDirectors = filmsDirectors;
    }
}



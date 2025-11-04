package com.example.cinema.cinema_app.director;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DirectorService {

    @Autowired
    private DirectorRepository directorRepository;

    public Director saveDirector(Director director) {
        return directorRepository.save(director);
    }

    public List<Director> getAllDirectors() {
        return directorRepository.findAll();
    }

    public Director getDirectorById(Long id) {
        return directorRepository.findById(id).orElse(null);
    }

    public void deleteDirector(Long id) {
        directorRepository.deleteById(id);
    }

    public Optional<Director> findByName(String name) {
        return directorRepository.findByName(name);
    }

    public List<Director> findAllById(List<Long> selectedDirectors) {
        return directorRepository.findAllById(selectedDirectors);
    }

    public Director updateDirector(Long id, Director directorDetails) {
        return directorRepository.findById(id)
                .map(director -> {
                    director.setName(directorDetails.getName());
                    director.setFilmsDirectors(directorDetails.getFilmsDirectors());
                    return directorRepository.save(director);
                }).orElse(null);
    }
}


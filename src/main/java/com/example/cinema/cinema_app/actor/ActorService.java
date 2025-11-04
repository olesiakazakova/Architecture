package com.example.cinema.cinema_app.actor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ActorService {
    @Autowired
    private ActorRepository actorRepository;

    public List<Actor> getAllActors() {
        return actorRepository.findAll();
    }

    public Optional<Actor> getActorById(Long id) {
        return actorRepository.findById(id);
    }

    public Actor createActor(Actor actor) {
        return actorRepository.save(actor);
    }

    public Actor updateActor(Long id, Actor actorDetails) {
        return actorRepository.findById(id)
                .map(actor -> {
                    actor.setName(actorDetails.getName());
                    actor.setFilmsActors(actorDetails.getFilmsActors());
                    return actorRepository.save(actor);
                }).orElse(null);
    }

    public void deleteActor(Long id) {
        actorRepository.deleteById(id);
    }

    public Optional<Actor> findByName(String name) {
        return actorRepository.findByName(name);
    }

    public List<Actor> findAllById(List<Long> selectedActors) {
        return actorRepository.findAllById(selectedActors);
    }
}


package com.example.cinema.cinema_app.tests;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.util.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.validation.BindingResult;

import com.example.cinema.cinema_app.film.FilmController;
import com.example.cinema.cinema_app.film.FilmsActors;
import com.example.cinema.cinema_app.film.FilmsActorsId;
import com.example.cinema.cinema_app.actor.Actor;
import com.example.cinema.cinema_app.actor.ActorService;
import com.example.cinema.cinema_app.director.Director;
import com.example.cinema.cinema_app.director.DirectorService;
import com.example.cinema.cinema_app.film.Film;
import com.example.cinema.cinema_app.film.service.FilmService;
import com.example.cinema.cinema_app.film.service.FilmsActorsService;
import com.example.cinema.cinema_app.genre.Genre;
import com.example.cinema.cinema_app.genre.GenreService;

// --- ТЕСТИРОВАНИЕ Film ---

class FilmTest {
    private Film film;

    @BeforeEach
    void setUp() {
        film = new Film();
        film.setFilmId(1L);
        film.setName("Терминатор");
        film.setDescription("Культовый боевик о роботе-убийце.");
        film.setReleaseDate(new Date(89, 6, 3));
        film.setDuration(107);
        Genre genre = new Genre();
        genre.setGenre("Фантастика");
        Director director = new Director();
        director.setName("Джеймс Кэмерон");
        Actor actor = new Actor();
        actor.setName("Арнольд Шварценеггер");
    }

    // --- ТЕСТЫ ДЛЯ setDuration ---

    // Корректная установка положительной продолжительности фильма
    @Test
    void testSetDuration_Valid() {
        film.setDuration(90);
        assertEquals(90, film.getDuration());
    }

    // Валидация нулевой продолжительности
    @Test
    void testSetDuration_Zero_ThrowsException() {
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> film.setDuration(0)
        );
        assertEquals("Продолжительность должна быть больше 0", exception.getMessage());
    }

    // Валидация отрицательной продолжительности
    @Test
    void testSetDuration_Negative_ThrowsException() {
        Exception exception = assertThrows(
        IllegalArgumentException.class,
                () -> film.setDuration(-5)
        );
        assertEquals("Продолжительность должна быть больше 0", exception.getMessage());
    }
}

// --- ТЕСТИРОВАНИЕ FilmController ---

@ExtendWith(MockitoExtension.class)
class FilmControllerTest {

    @Mock
    private FilmService filmService;
    @Mock
    private GenreService genreService;
    @Mock
    private DirectorService directorService;
    @Mock
    private ActorService actorService;
    @Mock
    private FilmsActorsService filmsActorsService;
    @Mock
    private Model model;
    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private FilmController filmsController;

    private Film film;
    private List<Genre> genres;
    private List<Director> directors;
    private List<Actor> actors;

    @BeforeEach
    void setUp() {
        film = new Film();
        film.setFilmId(1L);
        film.setName("Терминатор");
        film.setDescription("Культовый боевик о роботе-убийце.");
        film.setReleaseDate(new Date(89, 6, 3));
        film.setDuration(107);

        Genre genre1 = new Genre();
        Genre genre2 = new Genre();
        genre1.setGenre("Фантастика");
        genre2.setGenre("Боевик");
        genres = Arrays.asList(genre1, genre2);

        Director director = new Director();
        director.setName("Джеймс Кэмерон");
        directors = Arrays.asList(director);

        Actor actor = new Actor();
        actor.setName("Арнольд Шварценеггер");
        actors = Arrays.asList(actor);
    }

    // --- ТЕСТЫ ДЛЯ listFilms ---

    //  фильмы найдены
    @Test
    void listFilms_WhenFilmsExist_ShouldReturnListViewWithFilms() {
        List<Film> films = Arrays.asList(film);
        when(filmService.findAll()).thenReturn(films);

        String viewName = filmsController.listFilms(model);

        assertEquals("film/list", viewName);
        verify(filmService).findAll();
        verify(model).addAttribute("films", films);
    }

    // пустой список
    @Test
    void listFilms_WhenNoFilms_ShouldReturnListViewWithEmptyList() {
        when(filmService.findAll()).thenReturn(Collections.emptyList());

        String viewName = filmsController.listFilms(model);

        assertEquals("film/list", viewName);
        verify(filmService).findAll();
        verify(model).addAttribute("films", Collections.emptyList());
    }

    // --- ТЕСТЫ ДЛЯ showAddFilmForm ---

    // загрузка формы добавления фильма
    @Test
    void showAddFilmForm_ShouldReturnAddViewWithAllData() {
        when(genreService.findAll()).thenReturn(genres);
        when(directorService.getAllDirectors()).thenReturn(directors);
        when(actorService.getAllActors()).thenReturn(actors);

        String viewName = filmsController.showAddFilmForm(model);

        assertEquals("film/add", viewName);
        verify(model).addAttribute(eq("film"), any(Film.class));
        verify(model).addAttribute("allGenres", genres);
        verify(model).addAttribute("allDirectors", directors);
        verify(model).addAttribute("allActors", actors);
    }

    // --- ТЕСТЫ ДЛЯ addFilm ---

    // успешное добавление фильма
    @Test
    void addFilm_WhenValidData_ShouldSaveAndRedirect() {
        Film newFilm = new Film();
        newFilm.setName("Чужой");
        newFilm.setDuration(90);

        List<Long> genreIds = Arrays.asList(1L);
        List<Long> actorIds = Arrays.asList(1L);
        List<Long> directorIds = Arrays.asList(1L);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(genreService.findAllById(genreIds)).thenReturn(genres.subList(0, 1));
        when(actorService.findAllById(actorIds)).thenReturn(actors.subList(0, 1));
        when(directorService.findAllById(directorIds)).thenReturn(directors.subList(0, 1));
        when(filmService.save(any(Film.class))).thenReturn(newFilm);

        String viewName = filmsController.addFilm(newFilm, bindingResult,
                genreIds, actorIds, directorIds, "", "", "");

        assertEquals("redirect:/films", viewName);
        verify(filmService).save(newFilm);
    }

    // ошибка валидации (пустое название фильма)
    @Test
    void addFilm_WhenValidationErrors_ShouldReturnErrorView() {
        Film newFilm = new Film();
        newFilm.setName("");
        newFilm.setDuration(90);
        List<Long> emptyList = Collections.emptyList();

        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = filmsController.addFilm(newFilm, bindingResult,
                emptyList, emptyList, emptyList, "", "", "");

        assertEquals("film/error", viewName);
        verify(filmService, never()).save(any());
    }

    // --- ТЕСТЫ ДЛЯ showEditFilmForm ---

    // успешная загрузка формы редактирования
    @Test
    void showEditFilmForm_WhenFilmExists_ShouldReturnEditViewWithFilm() {
        when(filmService.findById(1L)).thenReturn(film);
        when(genreService.findAll()).thenReturn(genres);
        when(directorService.getAllDirectors()).thenReturn(directors);
        when(actorService.getAllActors()).thenReturn(actors);

        String viewName = filmsController.showEditFilmForm(1L, model);

        assertEquals("film/edit", viewName);
        verify(model).addAttribute("film", film);
        verify(model).addAttribute("allGenres", genres);
        verify(model).addAttribute("allDirectors", directors);
        verify(model).addAttribute("allActors", actors);
    }

    // при несуществующем фильме
    @Test
    void showEditFilmForm_WhenFilmNotFound_ShouldRedirectToList() {
        when(filmService.findById(999L)).thenReturn(null);

        String viewName = filmsController.showEditFilmForm(999L, model);

        assertEquals("redirect:/films", viewName);
    }

    // --- ТЕСТЫ ДЛЯ editFilm ---

    // успешное редактирование фильма
    @Test
    void editFilm_WhenValidData_ShouldUpdateAndRedirect() {
        Long filmId = 1L;
        Film updatedFilm = new Film();
        updatedFilm.setName("Терминатор 2");
        updatedFilm.setDuration(137);

        List<Long> genreIds = Arrays.asList(1L);
        List<Long> actorIds = Arrays.asList(1L);
        List<Long> directorIds = Arrays.asList(1L);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(genreService.findAllById(genreIds)).thenReturn(genres.subList(0, 1));
        when(actorService.findAllById(actorIds)).thenReturn(actors.subList(0, 1));
        when(directorService.findAllById(directorIds)).thenReturn(directors.subList(0, 1));

        String viewName = filmsController.editFilm(filmId, updatedFilm, bindingResult,
                genreIds, actorIds, directorIds);


        assertEquals("redirect:/films", viewName);
        verify(filmService).save(argThat(film ->
                film.getFilmId().equals(filmId) &&
                        film.getName().equals("Терминатор 2") &&
                        film.getDuration() == 137
        ));
    }

    // ошибка при редактировании фильма
    @Test
    void editFilm_WhenValidationFails_ShouldReturnErrorView() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = filmsController.editFilm(
                1L, film, bindingResult,
                Arrays.asList(1L), Arrays.asList(1L), Arrays.asList(1L)
        );

        assertEquals("film/error", viewName);
        verify(model, never()).addAttribute(eq("film"), any());
    }


    // --- ТЕСТЫ ДЛЯ deleteFilm ---

    // успешное удаление фильма с очисткой связей
    @Test
    void deleteFilm_WhenFilmExists_ShouldDeleteAndRedirect() {
        Long filmId = 1L;
        Film filmWithRelations = new Film();
        filmWithRelations.setFilmId(filmId);
        filmWithRelations.setName("Терминатор");
        for (int i = 0; i < actors.size(); i++) {
            Actor actor = actors.get(i);
            actor.setId((long) (i + 1));
            filmWithRelations.addActors(actor);
        }

        when(filmService.findById(filmId)).thenReturn(filmWithRelations);

        String viewName = filmsController.deleteFilm(filmId);

        assertEquals("redirect:/films", viewName);
        verify(filmService).findById(filmId);
        for (Actor actor : actors) {
            verify(filmsActorsService).deleteFilmsActors(actor.getActorId(), filmId);
        }
        verify(filmService).delete(filmId);
    }

    // удаление несуществующего фильма
    @Test
    void deleteFilm_WhenFilmNotFound_ShouldRedirect() {
        Long filmId = 999L;

        when(filmService.findById(filmId)).thenReturn(null);

        String viewName = filmsController.deleteFilm(filmId);

        assertEquals("redirect:/films", viewName);
        verify(filmService).findById(filmId);
        verify(filmService, never()).delete(anyLong());
    }
}

// --- ТЕСТИРОВАНИЕ ДЛЯ FilmsActors ---
class FilmsActorsTest {

    private FilmsActors filmsActors;
    private Actor actor;
    private Film film;

    @BeforeEach
    void setUp() {
        filmsActors = new FilmsActors();

        actor = new Actor();
        actor.setId(1L);

        film = new Film();
        film.setFilmId(100L);
    }

    // --- ТЕСТЫ ДЛЯ setActors

    // если нет id
    @Test
    void testSetActor_WhenIdIsNull_CreatesNewIdAndSetsActorId() {
        assertNull(filmsActors.getId());

        filmsActors.setActor(actor);

        assertNotNull(filmsActors.getId());
        assertEquals(1L, filmsActors.getId().getActor());
        assertEquals(actor, filmsActors.getActor());
    }

    // уже есть id
    @Test
    void testSetActor_WhenIdExists_UpdatesActorId() {
        filmsActors.setId(new FilmsActorsId());
        assertNotNull(filmsActors.getId());

        filmsActors.setActor(actor);

        assertEquals(1L, filmsActors.getId().getActor());
        assertEquals(actor, filmsActors.getActor());
    }

    // ошибка, передача null
    @Test
    void testSetActor_NullActor_ThrowsException() {
        assertThrows(NullPointerException.class, () -> {
            filmsActors.setActor(null);
        });
    }

    // --- ТЕСТИРОВАНИЕ ДЛЯ setFilm ---

    // нет id
    @Test
    void testSetFilm_WhenIdIsNull_CreatesNewIdAndSetsFilmId() {
        assertNull(filmsActors.getId());

        filmsActors.setFilm(film);

        assertNotNull(filmsActors.getId());
        assertEquals(100L, filmsActors.getId().getFilm());
        assertEquals(film, filmsActors.getFilm());
    }

    // есть id
    @Test
    void testSetFilm_WhenIdExists_UpdatesFilmId() {
        filmsActors.setId(new FilmsActorsId());
        assertNotNull(filmsActors.getId());

        filmsActors.setFilm(film);
        assertEquals(100L, filmsActors.getId().getFilm());
        assertEquals(film, filmsActors.getFilm());
    }

    // передача null
    @Test
    void testSetFilm_NullFilm_ThrowsException() {
        assertThrows(NullPointerException.class, () -> {
            filmsActors.setFilm(null);
        });
    }
}


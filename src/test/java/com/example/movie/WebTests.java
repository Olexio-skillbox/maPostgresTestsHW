package com.example.movie;

import com.example.movie.movie.dto.request.CreateMovieRequest;
import com.example.movie.movie.dto.request.EditMovieRequest;
import com.example.movie.movie.entity.MovieEntity;
import com.example.movie.movie.repository.MovieRepository;
import com.example.movie.movie.routes.MovieRoutes;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class WebTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MovieRepository movieRepository;


    @BeforeEach
    public void config() {
        for (MovieEntity movie : MovieGenerator.list()) {
            movieRepository.save(movie);
        }
    }

    @Test
    void contextLoadTest() throws Exception {
        MovieEntity movie = MovieEntity.builder()
                .title("1")
                .description("1")
                .build();

        movie = movieRepository.save(movie);

        mockMvc.perform(get(MovieRoutes.BY_ID, movie.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void createTest() throws Exception {
        // Напишите тест, который проверяет корректность работы запроса
        // MovieApiController.create
        // assert false;
        CreateMovieRequest request = CreateMovieRequest.builder()
                .title("createTest")
                .description("createTest")
                .build();
        mockMvc.perform(post(MovieRoutes.CREATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(content().string(containsString("createTest")));
    }

    @Test
    void findByIdTest() throws Exception {
        // Напишите тест, который проверяет корректность работы запроса MovieApiController.byId
        // assert false;
        MovieEntity movie = MovieEntity.builder()
                .title("findByIdTest")
                .description("findByIdTest")
                .build();
        movieRepository.save(movie);
        mockMvc.perform(get(MovieRoutes.BY_ID, movie.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("findByIdTest")));
    }

    @Test
    void findById_NotFound_Test() throws Exception {
        // Напишите тест, который проверяет корректность ответа запроса MovieApiController.byId, когда элемент не найдет.
        // запрос должен возвращать 404 статус
        // assert false;
        mockMvc.perform(get(MovieRoutes.BY_ID, 123456789)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTest() throws Exception {
        // Напишите тест, который проверяет корректность работы запроса MovieApiController.edit
        // assert false;
        MovieEntity movie = MovieEntity.builder()
                .title("11")
                .description("22")
                .build();
        movie = movieRepository.save(movie);

        EditMovieRequest editMovieRequest = EditMovieRequest.builder()
                .title("updateTest")
                .description("updateTest")
                .build();
        mockMvc.perform(put(MovieRoutes.EDIT, movie.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editMovieRequest)))
                .andDo(print())
                .andExpect(content().string(containsString("updateTest")));
    }

    @Test
    void deleteTest() throws Exception {
        // Напишите тест, который проверяет корректность работы запроса MovieApiController.delete
        // assert false;
        MovieEntity movie = MovieEntity.builder()
                .title("11")
                .description("22")
                .build();
        movieRepository.save(movie);
        assert movieRepository.findById(movie.getId()).isPresent();

        mockMvc.perform(delete(MovieRoutes.BY_ID, movie.getId().toString()))
                .andDo(print())
                .andExpect(status().isOk());
        assert movieRepository.findById(movie.getId()).isEmpty();
    }

    @Test
    void searchTest() throws Exception {
        // Напишите тест, который проверяет корректность работы запроса MovieApiController.search
        // assert false;
        List<MovieEntity> result = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            MovieEntity movie = MovieEntity.builder()
                    .title("title_" + i)
                    .description("descr_" + i)
                    .build();

            result.add(movieRepository.save(movie));
        }
        mockMvc.perform(get(MovieRoutes.SEARCH)
                        .param("size", "1000")
                        .param("query", "title_")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)));
    }

    @Test
    void searchExistTest() throws Exception {
        List<MovieEntity> check = new ArrayList<>();
        check.add(new MovieEntity(
                6L,
                "Невидимый Мир",
                "Группа ученых раскрывает существование параллельного мира, где законы физики искажены, и начинают исследование этой невидимой реальности."));

        // Напишите тест, который проверяет корректность работы запроса MovieApiController.search,
        // при заранее созданных данных.
        // PS: код выше удалять не нужно
        // assert false;
        mockMvc.perform(get(MovieRoutes.SEARCH)
                        .param("size", "1000")
                        .param("query", "Невидимый")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(check)));
    }
}

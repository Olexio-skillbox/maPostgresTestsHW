package com.example.movie.movie.controller;

import com.example.movie.movie.dto.request.EditMovieRequest;
import com.example.movie.movie.dto.request.CreateMovieRequest;
import com.example.movie.movie.dto.response.MovieResponse;
import com.example.movie.movie.entity.MovieEntity;
import com.example.movie.movie.exception.BadRequestException;
import com.example.movie.movie.exception.MovieAlreadyExistException;
import com.example.movie.movie.exception.MovieNotFoundException;
import com.example.movie.movie.repository.MovieRepository;
import com.example.movie.movie.routes.MovieRoutes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MovieApiController {
    private final MovieRepository movieRepository;

    @GetMapping("/")
    public MovieEntity root() {
        MovieEntity movie = MovieEntity.builder()
                .build();

        movie = movieRepository.save(movie);
        return movie;
    }

    @PostMapping(MovieRoutes.CREATE)
    public MovieResponse create(@RequestBody CreateMovieRequest request) throws BadRequestException, MovieAlreadyExistException {
        request.validate();

        MovieEntity movie = MovieEntity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .build();

        movie = movieRepository.save(movie);
        return MovieResponse.of(movie);
    }

    @GetMapping(MovieRoutes.BY_ID)
    public MovieResponse byId(@PathVariable Long id) throws MovieNotFoundException {
        return MovieResponse.of(movieRepository.findById(id).orElseThrow(MovieNotFoundException::new));
    }

    @GetMapping(MovieRoutes.SEARCH)
    public List<MovieResponse> search(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "") String query
    ) {
        Pageable pageable = PageRequest.of(page, size);

        ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny()
                .withMatcher("title", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("description", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        Example<MovieEntity> example = Example.of(
                MovieEntity.builder().title(query).description(query).build(),
                exampleMatcher);

        return movieRepository
                .findAll(example, pageable)
                .stream()
                .map(MovieResponse::of)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Редактируем пользователя", description = "Редактирование существующего пользователя")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Пользователь отредактирован",
                            content = { @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MovieResponse.class)) }),
                    @ApiResponse(responseCode = "400", description = "Некорректный запрос",
                            content = @Content),
                    @ApiResponse(responseCode = "404", description = "Нет пользователя с таким id",
                            content = @Content)
            }
    )
    @PutMapping(MovieRoutes.EDIT)
    public MovieResponse edit(@PathVariable(value = "id") Long id, @RequestBody EditMovieRequest request)
            throws MovieNotFoundException, BadRequestException {

        request.validate();

        MovieEntity movie = movieRepository.findById(id).orElseThrow(MovieNotFoundException::new);
        movie.setTitle(request.getTitle());
        movie.setDescription(request.getDescription());
        movie = movieRepository.save(movie);

        return MovieResponse.of(movie);
    }

    @DeleteMapping(MovieRoutes.BY_ID)
    public String delete(@PathVariable Long id) {
        movieRepository.deleteById(id);
        return HttpStatus.OK.name();
    }

}

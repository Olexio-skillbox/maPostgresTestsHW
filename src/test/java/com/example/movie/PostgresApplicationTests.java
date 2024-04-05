package com.example.movie;

import com.example.movie.movie.entity.MovieEntity;
import com.example.movie.movie.repository.MovieRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
class PostgresApplicationTests {

	@Autowired
	private MovieRepository movieRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void repositoryTest() {
		// Напишите простой тест работы movieRepository.
		// Вам необходимо убедиться, что сохраниение и поиск по id проходят корректно
		MovieEntity movie = MovieEntity.builder()
				.title("11")
				.description("22")
				.build();
		movie = movieRepository.save(movie);

		Optional<MovieEntity> optionalMovieEntity = movieRepository.findById(movie.getId());
		assert optionalMovieEntity.isPresent();

		MovieEntity check = optionalMovieEntity.get();
		assert check.getId().equals(movie.getId());

		// assert false;
	}

}

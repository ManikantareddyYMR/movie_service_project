package com.deloitte;

import com.deloitte.model.Movie;
import com.deloitte.repository.MovieRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MovieControllerIntTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MovieRepository movieRepository;

//    @BeforeEach
//    void cleanUp() {
//        movieRepository.deleteAllInBatch();
//    }

    @Test
    void givenMovie_whenCreateMovie_thenReturnSavedMovie() throws Exception {

        // Given
        Movie movie = new Movie();
        movie.setName("Don");
        movie.setDirector("Farhan Akhtar");
        movie.setActors(List.of(
                "Shah Rukh Khan",
                "Priyanka Chopra",
                "Arjun Rampal",
                "Boman Irani",
                "Kareena Kapoor"
        ));

        // When create movie
        var response = mockMvc.perform(post("/api/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movie)));

        // Then verify saved movie
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(notNullValue())))
                .andExpect(jsonPath("$.name", is(movie.getName())))
                .andExpect(jsonPath("$.director", is(movie.getDirector())))
                .andExpect(jsonPath("$.actors", hasSize(5)))
                .andExpect(jsonPath("$.actors[0]", is("Shah Rukh Khan")))
                .andExpect(jsonPath("$.actors[1]", is("Priyanka Chopra")));
    }

    @Test
    void givenMovieId_whenFetchMovie_thenReturnMovie() throws Exception {
        // Given
        Movie movie = new Movie();
        movie.setName("Don");
        movie.setDirector("Farhan Akhtar");
        movie.setActors(List.of(
                "Shah Rukh Khan",
                "Priyanka Chopra",
                "Arjun Rampal",
                "Boman Irani",
                "Kareena Kapoor"
        ));

        Movie savedMovie = movieRepository.save(movie);

        // When - GET movie by ID
        var response = mockMvc.perform(get("/api/movies/" + savedMovie.getId()));

        // Then verify saved movie
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedMovie.getId().intValue())))
                .andExpect(jsonPath("$.name", is(movie.getName())))
                .andExpect(jsonPath("$.director", is(movie.getDirector())))
                .andExpect(jsonPath("$.actors", hasSize(5)))
                .andExpect(jsonPath("$.actors[0]", is("Shah Rukh Khan")))
                .andExpect(jsonPath("$.actors[1]", is("Priyanka Chopra")));
    }

    @Test
    void givenMovieId_whenUpdateMovie_thenReturnUpdatedMovie() throws Exception {
        // Given - Create a movie first
        Movie movie = new Movie();
        movie.setName("Don");
        movie.setDirector("Farhan Akhtar");
        movie.setActors(List.of(
                "Shah Rukh Khan",
                "Priyanka Chopra",
                "Arjun Rampal",
                "Boman Irani",
                "Kareena Kapoor"
        ));
        Movie savedMovie = movieRepository.save(movie);
        Long id = savedMovie.getId();

        movie.setActors(List.of(
                "Shah Rukh Khan",
                "Priyanka Chopra",
                "Boman Irani",
                "Lara Dutta",
                "Om Puri"
        ));

        // When - Update the movie
        var response = mockMvc.perform(put("/api/movies/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movie)));

        response.andDo(print())
                .andExpect(status().isOk());

        // Fetch the updated movie
        var fetchResponse = mockMvc.perform(get("/api/movies/" + id));

        // Then verify updated movie (FIXED: Changed from 'response' to 'fetchResponse')
        fetchResponse.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(movie.getName())))
                .andExpect(jsonPath("$.director", is(movie.getDirector())))
                .andExpect(jsonPath("$.actors", hasSize(5)))
                .andExpect(jsonPath("$.actors[0]", is("Shah Rukh Khan")))
                .andExpect(jsonPath("$.actors[1]", is("Priyanka Chopra")));
    }

    @Test
    void givenMovieId_whenDeleteMovie_thenMovieDeleted() throws Exception {
        // Given - Create a movie first
        Movie movie = new Movie();
        movie.setName("Don");
        movie.setDirector("Farhan Akhtar");
        movie.setActors(List.of(
                "Shah Rukh Khan",
                "Priyanka Chopra",
                "Arjun Rampal",
                "Boman Irani",
                "Kareena Kapoor"
        ));
        Movie savedMovie = movieRepository.save(movie);
        Long id = savedMovie.getId();

        // When - Delete the movie
        var response = mockMvc.perform(delete("/api/movies/" + id));

        // Then - Verify deletion was successful (FIXED: Changed from isOk() to isNoContent())
        response.andDo(print())
                .andExpect(status().isNoContent());  // Changed from isOk() to isNoContent()

        // Verify the movie no longer exists
        var fetchResponse = mockMvc.perform(get("/api/movies/" + id));

        fetchResponse.andDo(print())
                .andExpect(status().isNotFound());
    }
}
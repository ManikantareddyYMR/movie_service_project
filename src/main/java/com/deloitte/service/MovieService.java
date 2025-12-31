package com.deloitte.service;

import com.deloitte.exception.InvalidDataException;
import com.deloitte.exception.NotFoundException;
import com.deloitte.model.Movie;
import com.deloitte.repository.MovieRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    public Movie create(Movie movie) {
        if (movie == null) {
            throw new InvalidDataException("Movie cannot be null");
        }
        if (movie.getName() == null || movie.getName().trim().isEmpty()) {
            throw new InvalidDataException("Movie name cannot be empty");
        }
        if (movie.getDirector() == null || movie.getDirector().trim().isEmpty()) {
            throw new InvalidDataException("Movie director cannot be empty");
        }
        return movieRepository.save(movie);
    }

    public Movie read(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Movie not found with id: " + id));
    }

    public void update(Long id, Movie update) {
        if (update == null || id == null) {
            throw new InvalidDataException("Movie and ID cannot be null");
        }
        if (!movieRepository.existsById(id)) {
            throw new NotFoundException("Movie not found with id: " + id);
        }
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Movie not found with id: " + id));
        movie.setName(update.getName());
        movie.setDirector(update.getDirector());
        movie.setActors(update.getActors());
        movieRepository.save(movie);
    }

    public void delete(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new NotFoundException("Movie not found with id: " + id);
        }
        movieRepository.deleteById(id);
    }
}
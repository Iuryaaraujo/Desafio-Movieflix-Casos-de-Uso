package com.devsuperior.movieflix.services;

import com.devsuperior.movieflix.dto.ReviewDTO;
import com.devsuperior.movieflix.entities.Movie;
import com.devsuperior.movieflix.entities.Review;
import com.devsuperior.movieflix.entities.User;
import com.devsuperior.movieflix.repositories.MovieRepository;

import com.devsuperior.movieflix.repositories.ReviewRepository;
import com.devsuperior.movieflix.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private MovieRepository movieRepository;

    @Transactional(readOnly = true)
    public List<ReviewDTO> findByMovie(Long movieId) {

        if (!movieRepository.existsById(movieId)) {
            throw new ResourceNotFoundException("Entity not found");
        }

        Movie movie = movieRepository.getReferenceById(movieId);
        List<Review> reviewList = reviewRepository.findByMovie(movie);
        return reviewList.stream().map(ReviewDTO::new).collect(Collectors.toList());
    }

    @Transactional
    public ReviewDTO insert(ReviewDTO dto) {

        User user = authService.authenticated();

        Review entity = new Review();
        entity.setMovie(movieRepository.getReferenceById(dto.getMovieId()));
        entity.setUser(user);
        entity.setText(dto.getText());

        reviewRepository.save(entity);

        return new ReviewDTO(entity);
    }
}

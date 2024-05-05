package com.example.mydiary.controller;

import com.example.mydiary.model.Movie;
import com.example.mydiary.repository.MovieRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring6.context.webflux.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Controller
@Slf4j
public class MovieController {

    @Autowired
    private MovieRepository movieRepository;

    @GetMapping(value = "/")
    public String getAllMovies(final Model model){

        IReactiveDataDriverContextVariable reactiveDataDriverContextVariable
                =new ReactiveDataDriverContextVariable(
                movieRepository.findAll(),1);
        log.info("MOVIES READ FROM MONGODB {}",reactiveDataDriverContextVariable);
        model.addAttribute("movies",reactiveDataDriverContextVariable);

        return "allMovies";
    }


    @GetMapping(path = {"/edit","/edit/{id}"})
    public Mono<String> createUpdateMovieById(Model model, @PathVariable("id") Optional<Long> id){
        return id.map(movieRepository::findById)
                .orElseGet(() -> Mono.just(new Movie()))
                .doOnNext(movie -> log.info(" $$$$$ CREATE/UPDATED by ID Movie :: {}", movie))
                .doOnNext(movie -> model.addAttribute("movie", movie))
                .thenReturn("addMovie");
    }



    @PostMapping(value = "/create")
    public String createMovie(Movie movie){
        log.info(" ::::: CREATE/ UPDATE  ::::: {}", movie);
        if(movie.getId() == null){
            movie.setId(convertLocalDateTimeToLong(LocalDateTime.now()));
        }
        movieRepository.save(movie).subscribe();
        return "redirect:/";
    }


    @GetMapping(path="/delete/{id}")
    public String deleteMovie(Model model, @PathVariable("id") Long id){
        log.info(" ::::: DELETE MOVIE ID ::::: {}",id);
        movieRepository.deleteById(id).subscribe();
        return "redirect:/";
    }

    private Long convertLocalDateTimeToLong(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyyHHmmss");
        String formattedDateTime = localDateTime.format(formatter);
        return Long.parseLong(formattedDateTime);
    }
}

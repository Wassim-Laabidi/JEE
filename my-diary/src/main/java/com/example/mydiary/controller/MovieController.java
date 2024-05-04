package com.example.mydiary.model.controller;

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
    public String createUpdateMovieById(Model model, @PathVariable("id") Optional<Long> id){
        if(id.isPresent()){
            Mono<Movie> movie = movieRepository.findById(id.get());
            log.info(" $$$$$ CREATE/UPDATED by ID Movie :: {}",movie);
            model.addAttribute("movie",movie);
        }else {
            model.addAttribute("movie",new Movie());
        }
        return "addMovie";
    }

    @PostMapping(value = "/create")
    public String createMovie(Movie movie){
        log.info(" ::::: CREATE/ UPDATE  ::::: {}",movie);
        movieRepository.save(movie).subscribe();
        return "redirect:/";
    }

    @GetMapping(path="/delete/{id}")
    public String deleteMovie(Model model, @PathVariable("id") Long id){
        log.info(" ::::: DELETE MOVIE ID ::::: {}",id);
        movieRepository.deleteById(id).subscribe();
        return "redirect:/";
    }


}
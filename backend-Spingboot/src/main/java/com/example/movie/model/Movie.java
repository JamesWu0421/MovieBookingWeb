package com.example.movie.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movies")
public class Movie {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(name = "eng_title", length = 200)
    private String engTitle;
    
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;
    
    @Column(name = "poster_url", length = 500)
    private String posterUrl;
    
    @Column(name = "trailer_url", length = 500)
    private String trailerUrl;
    
    @Column(name = "rating_level", length = 100)
    private String ratingLevel;
    
    @Column(name = "runtime_minutes", nullable = false)
    private Integer runtimeMinutes;
    
    @Column(name = "release_date")
    private LocalDate releaseDate;
    
    @Column(name = "is_published", nullable = false)
    private Boolean isPublished = false;
    
    @Column(length = 100)
    private String director;
    
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String cast;
    
    @Column(length = 200)
    private String genres;
    
    @Column(length = 500)
    private String keywords;
}

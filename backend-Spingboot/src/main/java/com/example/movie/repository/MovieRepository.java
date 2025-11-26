package com.example.movie.repository;

import com.example.movie.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {
    
    /**
     * 查詢已發布的電影
     */
    List<Movie> findByIsPublishedTrue();
    
    /**
     * 根據標題搜尋電影
     */
    List<Movie> findByTitleContaining(String title);
    
    /**
     * 根據類型搜尋電影
     */
    List<Movie> findByGenresContaining(String genre);
}

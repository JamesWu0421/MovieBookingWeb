package tw.com.ispan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tw.com.ispan.domain.MovieBean;

public interface MovieRepository extends JpaRepository<MovieBean, Integer> {

    @Query("""
          SELECT m FROM MovieBean m
          WHERE (:keyword IS NULL OR
                LOWER(m.title)       LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(m.engTitle)    LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(m.keywords)    LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(m.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND (:genre IS NULL OR
                LOWER(m.genres) LIKE LOWER(CONCAT('%', :genre, '%')))
            AND (:published IS NULL OR m.isPublished = :published)
          """)
    List<MovieBean> searchMovies(
            @Param("keyword") String keyword,
            @Param("genre") String genre,
            @Param("published") Boolean published);
}

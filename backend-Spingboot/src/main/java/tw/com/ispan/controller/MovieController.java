package tw.com.ispan.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tw.com.ispan.domain.MovieBean;
import tw.com.ispan.service.MovieService;

// @CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    // GET /api/movies 取得全部
    @GetMapping
    public ResponseEntity<List<MovieBean>> getAllMovies() {
        List<MovieBean> list = movieService.getAllMovies();
        return ResponseEntity.ok(list);
    }

    // GET /api/movies/{id} 依 ID 取得
    @GetMapping("/{id}")
    public ResponseEntity<MovieBean> getMovieById(@PathVariable Integer id) {
        Optional<MovieBean> optional = movieService.getMovieById(id);
        return optional.map(ResponseEntity::ok)
                       .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /api/movies 新增
    @PostMapping
    public ResponseEntity<MovieBean> addMovie(@RequestBody MovieBean movie) {
        MovieBean created = movieService.addMovie(movie);
        return ResponseEntity.ok(created);
    }

    // PUT /api/movies/{id} 更新
    @PutMapping("/{id}")
    public ResponseEntity<MovieBean> updateMovie(
            @PathVariable Integer id,
            @RequestBody MovieBean movie) {

        Optional<MovieBean> optional = movieService.updateMovieById(id, movie);
        return optional.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // DELETE /api/movies/{id} 刪除
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Integer id) {
        boolean deleted = movieService.deleteMovieById(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    // GET /api/movies/search?keyword=...&genre=...&published=true
    @GetMapping("/search")
    public ResponseEntity<List<MovieBean>> searchMovies(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Boolean published) {

        List<MovieBean> list = movieService.searchMovies(keyword, genre, published);
        return ResponseEntity.ok(list);
    }
}

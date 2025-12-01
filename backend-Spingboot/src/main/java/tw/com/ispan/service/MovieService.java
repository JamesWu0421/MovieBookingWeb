package tw.com.ispan.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tw.com.ispan.domain.MovieBean;
import tw.com.ispan.repository.MovieRepository;

@Service
@Transactional
public class MovieService {

    @Autowired
    private MovieRepository movieRepo;

    // 取得全部電影
    public List<MovieBean> getAllMovies() {
        List<MovieBean> movielist = new ArrayList<>();
        movieRepo.findAll().forEach(movielist::add);
        return movielist;
    }

    // 依 ID 查詢電影
    public Optional<MovieBean> getMovieById(Integer id) {
        return movieRepo.findById(id);
    }

    // 新增電影
    public MovieBean addMovie(MovieBean movie) {
        return movieRepo.save(movie);
    }

    // 更新電影
    public Optional<MovieBean> updateMovieById(Integer id, MovieBean newMovieData) {
        Optional<MovieBean> oldMovieData = movieRepo.findById(id);
        if (oldMovieData.isPresent()) {
            MovieBean updatedMovie = oldMovieData.get();
            // 這邊依照你的欄位一個一個塞
            updatedMovie.setTitle(newMovieData.getTitle());
            updatedMovie.setEngTitle(newMovieData.getEngTitle());
            updatedMovie.setDescription(newMovieData.getDescription());
            updatedMovie.setPosterUrl(newMovieData.getPosterUrl());
            updatedMovie.setTrailerUrl(newMovieData.getTrailerUrl());
            updatedMovie.setRatingLevel(newMovieData.getRatingLevel());
            updatedMovie.setRuntimeMinutes(newMovieData.getRuntimeMinutes());
            updatedMovie.setReleaseDate(newMovieData.getReleaseDate());
            updatedMovie.setIsPublished(newMovieData.getIsPublished());
            updatedMovie.setDirector(newMovieData.getDirector());
            updatedMovie.setCast(newMovieData.getCast());
            updatedMovie.setGenres(newMovieData.getGenres());
            updatedMovie.setKeywords(newMovieData.getKeywords());

            return Optional.of(movieRepo.save(updatedMovie));
        }
        return Optional.empty();
    }

    // 刪除電影
    public boolean deleteMovieById(Integer id) {
        if (movieRepo.existsById(id)) {
            movieRepo.deleteById(id);
            return true;
        }
        return false;
    }

    // 搜尋電影（關鍵字 ＋ 類型 ＋ 是否上架）
    public List<MovieBean> searchMovies(String keyword, String genre, Boolean published) {
        if (keyword != null && keyword.isBlank()) {
            keyword = null;
        }
        if (genre != null && genre.isBlank()) {
            genre = null;
        }
        return movieRepo.searchMovies(keyword, genre, published);
    }
}

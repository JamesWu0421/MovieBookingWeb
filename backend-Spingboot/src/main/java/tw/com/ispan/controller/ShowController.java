package tw.com.ispan.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tw.com.ispan.domain.ShowBean;
import tw.com.ispan.service.ShowService;

@RestController
@RequestMapping("/api/shows")
public class ShowController {

    private final ShowService showService;

    public ShowController(ShowService showService) {
        this.showService = showService;
    }

    // GET 全部
    @GetMapping
    public ResponseEntity<List<ShowBean>> getAll() {
        return ResponseEntity.ok(showService.getAllShows());
    }

    // GET 單筆
    @GetMapping("/{id}")
    public ResponseEntity<ShowBean> getById(@PathVariable Integer id) {
        Optional<ShowBean> data = showService.getShowById(id);
        return data.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST 新增
    @PostMapping
    public ResponseEntity<ShowBean> create(@RequestBody ShowBean show) {
        return ResponseEntity.ok(showService.addShow(show));
    }

    // PUT 修改
    @PutMapping("/{id}")
    public ResponseEntity<ShowBean> update(
            @PathVariable Integer id,
            @RequestBody ShowBean show) {

        Optional<ShowBean> updated = showService.updateShow(id, show);
        return updated.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // DELETE 刪除
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        boolean deleted = showService.deleteShow(id);
        if (!deleted) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }
}

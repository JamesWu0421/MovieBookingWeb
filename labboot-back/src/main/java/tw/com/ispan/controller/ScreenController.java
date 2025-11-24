package tw.com.ispan.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tw.com.ispan.domain.ScreenBean;
import tw.com.ispan.service.ScreenService;

@RestController
@RequestMapping("/api/screens")
public class ScreenController {

    private final ScreenService screenService;

    public ScreenController(ScreenService screenService) {
        this.screenService = screenService;
    }

    // GET 全部影廳
    @GetMapping
    public ResponseEntity<List<ScreenBean>> getAllScreens() {
        return ResponseEntity.ok(screenService.getAllScreens());
    }

    // GET 單筆影廳
    @GetMapping("/{id}")
    public ResponseEntity<ScreenBean> getScreenById(@PathVariable Integer id) {
        Optional<ScreenBean> screen = screenService.getScreenById(id);
        return screen.map(ResponseEntity::ok)
                     .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST 新增影廳
    @PostMapping
    public ResponseEntity<ScreenBean> addScreen(@RequestBody ScreenBean screen) {
        ScreenBean created = screenService.addScreen(screen);
        return ResponseEntity.ok(created);
    }

    // PUT 更新影廳
    @PutMapping("/{id}")
    public ResponseEntity<ScreenBean> updateScreen(
            @PathVariable Integer id,
            @RequestBody ScreenBean screen) {

        Optional<ScreenBean> updated = screenService.updateScreen(id, screen);
        return updated.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // DELETE 刪除影廳
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScreen(@PathVariable Integer id) {
        boolean deleted = screenService.deleteScreen(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}

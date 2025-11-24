package tw.com.ispan.controller;


import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.com.ispan.dto.ShowTicketPriceCreateRequest;
import tw.com.ispan.domain.ShowTicketPricesBean;
import tw.com.ispan.service.ShowTicketPricesService;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/show-ticket-prices")
public class ShowTicketPricesController {

    private final ShowTicketPricesService service;

    public ShowTicketPricesController(ShowTicketPricesService service) {
        this.service = service;
    }

    // ========= Create =========

    /** 用 showId + ticketPackageId + isAvailable 建立票價 */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody ShowTicketPriceCreateRequest req) {
        try {
            ShowTicketPricesBean created = service.createFromIds(
                    req.getShowId(),
                    req.getTicketPackageId(),
                    req.getIsAvailable()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "建立票價時發生錯誤：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // ========= Read（挑幾個常用的） =========

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.findById(id));
        } catch (EntityNotFoundException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<ShowTicketPricesBean>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/by-show/{showId}")
    public ResponseEntity<List<ShowTicketPricesBean>> findByShowId(@PathVariable int showId) {
        return ResponseEntity.ok(service.findByShowId(showId));
    }

    @GetMapping("/by-show/{showId}/available")
    public ResponseEntity<List<ShowTicketPricesBean>> findAvailableByShowId(@PathVariable int showId) {
        return ResponseEntity.ok(service.findAvailableByShowId(showId));
    }

    @GetMapping("/by-price-range")
    public ResponseEntity<List<ShowTicketPricesBean>> findByPriceRange(
            @RequestParam int minPrice,
            @RequestParam int maxPrice) {
        return ResponseEntity.ok(service.findByPriceRange(minPrice, maxPrice));
    }

    @GetMapping("/by-time-range")
    public ResponseEntity<List<ShowTicketPricesBean>> findByTimeRange(
            @RequestParam("start") String start,
            @RequestParam("end") String end) {

        LocalTime startTime = LocalTime.parse(start);
        LocalTime endTime = LocalTime.parse(end);
        return ResponseEntity.ok(service.findByTimeRange(startTime, endTime));
    }

    // ========= Delete（示範一個） =========

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}
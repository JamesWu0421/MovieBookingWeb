package tw.com.ispan.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.com.ispan.dto.ShowTicketPricesRequestDTO;
import tw.com.ispan.dto.ShowTicketPricesResponseDTO;
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

    /**
     * 用 showId + ticketPackageId + isAvailable 建立票價
     * POST /api/show-ticket-prices
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody ShowTicketPricesRequestDTO requestDTO) {
        try {
            // 基本驗證
            if (requestDTO.getShowId() == null) {
                return new ResponseEntity<>("場次ID不能為空", HttpStatus.BAD_REQUEST);
            }
            if (requestDTO.getTicketPackageId() == null) {
                return new ResponseEntity<>("票種套餐ID不能為空", HttpStatus.BAD_REQUEST);
            }

            ShowTicketPricesResponseDTO created = service.createFromRequest(requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "建立票價時發生錯誤:" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // ========= Read =========

    /**
     * 根據 ID 取得單筆票價
     * GET /api/show-ticket-prices/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            ShowTicketPricesResponseDTO dto = service.findDTOById(id);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * 取得所有票價
     * GET /api/show-ticket-prices/all
     */
    @GetMapping("/all")
    public ResponseEntity<List<ShowTicketPricesResponseDTO>> findAll() {
        List<ShowTicketPricesResponseDTO> dtoList = service.findAllDTO();
        if (dtoList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(dtoList);
    }

    /**
     * 根據場次 ID 取得所有票價
     * GET /api/show-ticket-prices/by-show/{showId}
     */
    @GetMapping("/by-show/{showId}")
    public ResponseEntity<List<ShowTicketPricesResponseDTO>> findByShowId(@PathVariable int showId) {
        List<ShowTicketPricesResponseDTO> dtoList = service.findDTOByShowId(showId);
        if (dtoList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(dtoList);
    }

    /**
     * 根據場次 ID 取得「可售」票價
     * GET /api/show-ticket-prices/by-show/{showId}/available
     */
    @GetMapping("/by-show/{showId}/available")
    public ResponseEntity<List<ShowTicketPricesResponseDTO>> findAvailableByShowId(@PathVariable int showId) {
        List<ShowTicketPricesResponseDTO> dtoList = service.findAvailableDTOByShowId(showId);
        if (dtoList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(dtoList);
    }

    /**
     * 根據價格範圍查詢票價
     * GET /api/show-ticket-prices/by-price-range?minPrice=100&maxPrice=500
     */
    @GetMapping("/by-price-range")
    public ResponseEntity<List<ShowTicketPricesResponseDTO>> findByPriceRange(
            @RequestParam int minPrice,
            @RequestParam int maxPrice) {
        List<ShowTicketPricesResponseDTO> dtoList = service.findDTOByPriceRange(minPrice, maxPrice);
        if (dtoList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(dtoList);
    }

    /**
     * 根據時間範圍查詢票價
     * GET /api/show-ticket-prices/by-time-range?start=09:00:00&end=12:00:00
     */
    @GetMapping("/by-time-range")
    public ResponseEntity<?> findByTimeRange(
            @RequestParam("start") String start,
            @RequestParam("end") String end) {
        try {
            LocalTime startTime = LocalTime.parse(start);
            LocalTime endTime = LocalTime.parse(end);
            List<ShowTicketPricesResponseDTO> dtoList = service.findDTOByTimeRange(startTime, endTime);
            if (dtoList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return ResponseEntity.ok(dtoList);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "時間格式錯誤,請使用 HH:mm:ss 格式");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    // ========= Delete =========

    /**
     * 刪除票價
     * DELETE /api/show-ticket-prices/{id}
     */
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
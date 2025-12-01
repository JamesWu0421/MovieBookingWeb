package tw.com.ispan.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tw.com.ispan.domain.SeatBean;
import tw.com.ispan.service.SeatService;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    // 批次建立座位
    @PostMapping("/batch")
    public ResponseEntity<List<SeatBean>> batchCreateSeats(
            @RequestBody BatchCreateSeatRequest request
    ) {
        List<SeatBean> seats = seatService.batchCreateSeats(
                request.getScreenId(),
                request.getRowLabels(),
                request.getStartSeatNumber(),
                request.getEndSeatNumber()
        );

        return ResponseEntity.ok(seats);
    }

    // 依影廳查座位
    @GetMapping
    public ResponseEntity<List<SeatBean>> getSeatsByScreen(
            @RequestParam Integer screenId
    ) {
        List<SeatBean> seats = seatService.getSeatsByScreen(screenId);
        return ResponseEntity.ok(seats);
    }

    // 點座位 → 切換封鎖狀態
    // URL: PATCH /api/seats/{id}/block   body: { "blocked": true/false }
    @PatchMapping("/{id}/block")
    public ResponseEntity<SeatBean> updateBlocked(
            @PathVariable Integer id,
            @RequestBody UpdateBlockedRequest request
    ) {
        SeatBean updated = seatService.updateBlocked(id, request.isBlocked());
        return ResponseEntity.ok(updated);
    }
}

// 用來接收批次新增 body
class BatchCreateSeatRequest {
    private Integer screenId;
    private List<String> rowLabels;
    private Integer startSeatNumber;
    private Integer endSeatNumber;

    public Integer getScreenId() { return screenId; }
    public void setScreenId(Integer screenId) { this.screenId = screenId; }

    public List<String> getRowLabels() { return rowLabels; }
    public void setRowLabels(List<String> rowLabels) { this.rowLabels = rowLabels; }

    public Integer getStartSeatNumber() { return startSeatNumber; }
    public void setStartSeatNumber(Integer startSeatNumber) { this.startSeatNumber = startSeatNumber; }

    public Integer getEndSeatNumber() { return endSeatNumber; }
    public void setEndSeatNumber(Integer endSeatNumber) { this.endSeatNumber = endSeatNumber; }
}

// 用來接收封鎖狀態更新 body
class UpdateBlockedRequest {
    private boolean blocked;

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
}


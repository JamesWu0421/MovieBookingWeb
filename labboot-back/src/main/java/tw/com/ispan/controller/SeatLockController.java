// package tw.com.ispan.controller;

// import java.util.List;

// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import tw.com.ispan.domain.SeatLockBean;
// import tw.com.ispan.service.SeatLockService;
// import tw.com.ispan.service.SeatLockService.LockResult;

// @RestController
// @RequestMapping("/api/seat-locks")
// public class SeatLockController {

//     private final SeatLockService seatLockService;

//     public SeatLockController(SeatLockService seatLockService) {
//         this.seatLockService = seatLockService;
//     }

//     // 鎖定座位
//     @PostMapping("/lock")
//     public ResponseEntity<LockResult> lockSeats(@RequestBody LockSeatsRequest request) {
//         LockResult result = seatLockService.lockSeats(
//                 request.getShowId(),
//                 request.getSeatIds(),
//                 request.getUserId(),
//                 request.getLockSeconds()
//         );
//         return ResponseEntity.ok(result);
//     }

//     // 釋放座位
//     @PostMapping("/release")
//     public ResponseEntity<Void> releaseSeats(@RequestBody ReleaseSeatsRequest request) {
//         seatLockService.releaseSeats(
//                 request.getShowId(),
//                 request.getSeatIds(),
//                 request.getUserId()
//         );
//         return ResponseEntity.noContent().build();
//     }

//     // 查某個場次目前 active 的 lock
//     @GetMapping("/active")
//     public ResponseEntity<List<SeatLockBean>> getActiveLocks(@RequestParam Integer showId) {
//         List<SeatLockBean> locks = seatLockService.getActiveLocksByShow(showId);
//         return ResponseEntity.ok(locks);
//     }

//     // ====== Request 物件 ======

//     public static class LockSeatsRequest {
//         private Integer showId;
//         private List<Integer> seatIds;
//         private Integer userId;
//         private Integer lockSeconds; // 可選，null 則用預設

//         public Integer getShowId() { return showId; }
//         public void setShowId(Integer showId) { this.showId = showId; }

//         public List<Integer> getSeatIds() { return seatIds; }
//         public void setSeatIds(List<Integer> seatIds) { this.seatIds = seatIds; }

//         public Integer getUserId() { return userId; }
//         public void setUserId(Integer userId) { this.userId = userId; }

//         public Integer getLockSeconds() { return lockSeconds; }
//         public void setLockSeconds(Integer lockSeconds) { this.lockSeconds = lockSeconds; }
//     }

//     public static class ReleaseSeatsRequest {
//         private Integer showId;
//         private List<Integer> seatIds;
//         private Integer userId;

//         public Integer getShowId() { return showId; }
//         public void setShowId(Integer showId) { this.showId = showId; }

//         public List<Integer> getSeatIds() { return seatIds; }
//         public void setSeatIds(List<Integer> seatIds) { this.seatIds = seatIds; }

//         public Integer getUserId() { return userId; }
//         public void setUserId(Integer userId) { this.userId = userId; }
//     }
// }

// package tw.com.ispan.service;

// import java.time.LocalDateTime;
// import java.util.*;
// import java.util.stream.Collectors;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import jakarta.transaction.Transactional;
// import tw.com.ispan.domain.SeatLockBean;
// import tw.com.ispan.repository.SeatLockRepository;

// @Service
// @Transactional
// public class SeatLockService {

//     @Autowired
//     private SeatLockRepository seatLockRepo;

//     // 嘗試鎖定多個座位
//     public LockResult lockSeats(Integer showId,
//                                 List<Integer> seatIds,
//                                 Integer userId,
//                                 Integer lockSeconds) {

//         if (lockSeconds == null || lockSeconds <= 0) {
//             lockSeconds = 300; // 預設鎖 5 分鐘
//         }

//         LocalDateTime now = LocalDateTime.now();
//         LocalDateTime lockedUntil = now.plusSeconds(lockSeconds);

//         // 先查這些座位裡，哪些已經被 active lock 住了
//         List<SeatLockBean> existingLocks =
//                 seatLockRepo.findByShowIdAndSeatIdInAndStatusAndLockedUntilAfter(
//                         showId, seatIds, "active", now);

//         Set<Integer> lockedSeatIdSet = existingLocks.stream()
//                 .map(SeatLockBean::getSeatId)
//                 .collect(Collectors.toSet());

//         List<Integer> success = new ArrayList<>();
//         List<Integer> failed = new ArrayList<>();

//         for (Integer seatId : seatIds) {
//             if (lockedSeatIdSet.contains(seatId)) {
//                 failed.add(seatId);
//                 continue;
//             }

//             // 建立新的 lock
//             SeatLockBean lock = new SeatLockBean();
//             lock.setShowId(showId);
//             lock.setSeatId(seatId);
//             lock.setUserId(userId);
//             lock.setCreatedAt(now);
//             lock.setLockedUntil(lockedUntil);
//             lock.setStatus("active");

//             seatLockRepo.save(lock);
//             success.add(seatId);
//         }

//         LockResult result = new LockResult();
//         result.setLockedSeatIds(success);
//         result.setFailedSeatIds(failed);
//         result.setLockedUntil(lockedUntil);
//         return result;
//     }

//     // 釋放座位（例如用戶取消 / 超時也可以用 batch job 改 status）
//     public void releaseSeats(Integer showId, List<Integer> seatIds, Integer userId) {
//         LocalDateTime now = LocalDateTime.now();

//         List<SeatLockBean> locks = seatLockRepo
//                 .findByShowIdAndSeatIdInAndStatusAndLockedUntilAfter(showId, seatIds, "active", now);

//         for (SeatLockBean lock : locks) {
//             if (Objects.equals(lock.getUserId(), userId)) {
//                 lock.setStatus("released");
//                 seatLockRepo.save(lock);
//             }
//         }
//     }

//     // 查某個場次目前 active 的 seat lock（給前端畫「已被佔的座位」）
//     public List<SeatLockBean> getActiveLocksByShow(Integer showId) {
//         LocalDateTime now = LocalDateTime.now();
//         return seatLockRepo.findByShowIdAndStatusAndLockedUntilAfter(showId, "active", now);
//     }

//     // 簡單的結果物件
//     public static class LockResult {
//         private List<Integer> lockedSeatIds;
//         private List<Integer> failedSeatIds;
//         private LocalDateTime lockedUntil;

//         public List<Integer> getLockedSeatIds() { return lockedSeatIds; }
//         public void setLockedSeatIds(List<Integer> lockedSeatIds) { this.lockedSeatIds = lockedSeatIds; }

//         public List<Integer> getFailedSeatIds() { return failedSeatIds; }
//         public void setFailedSeatIds(List<Integer> failedSeatIds) { this.failedSeatIds = failedSeatIds; }

//         public LocalDateTime getLockedUntil() { return lockedUntil; }
//         public void setLockedUntil(LocalDateTime lockedUntil) { this.lockedUntil = lockedUntil; }
//     }
// }

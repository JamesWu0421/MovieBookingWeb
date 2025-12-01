package tw.com.ispan.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tw.com.ispan.domain.SeatLockBean;
import tw.com.ispan.repository.SeatLockRepository;

@Service
@Transactional
public class SeatLockService {

    @Autowired
    private SeatLockRepository seatLockRepo;

    /**
     * 嘗試鎖定多個座位
     */
    public LockResult lockSeats(Integer showId,
                                List<Integer> seatIds,
                                Integer userId,
                                Integer lockSeconds) {

        if (lockSeconds == null || lockSeconds <= 0) {
            lockSeconds = 300; // 預設鎖 5 分鐘
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lockedUntil = now.plusSeconds(lockSeconds);

        // 先查這些座位裡，哪些已經被 active lock 住了（而且尚未過期）
        List<SeatLockBean> existingLocks =
                seatLockRepo.findByShowIdAndSeatIdInAndStatusAndLockedUntilAfter(
                        showId, seatIds, "active", now);

        Set<Integer> lockedSeatIdSet = existingLocks.stream()
                .map(SeatLockBean::getSeatId)
                .collect(Collectors.toSet());

        List<Integer> success = new ArrayList<>();
        List<Integer> failed = new ArrayList<>();

        for (Integer seatId : seatIds) {
            if (lockedSeatIdSet.contains(seatId)) {
                // 已經被別人鎖住了
                failed.add(seatId);
                continue;
            }

            // 建立新的 lock
            SeatLockBean lock = new SeatLockBean();
            lock.setShowId(showId);
            lock.setSeatId(seatId);
            lock.setUserId(userId);
            lock.setCreatedAt(now);
            lock.setLockedUntil(lockedUntil);
            lock.setStatus("active");

            seatLockRepo.save(lock);
            success.add(seatId);
        }

        LockResult result = new LockResult();
        result.setLockedSeatIds(success);
        result.setFailedSeatIds(failed);
        result.setLockedUntil(lockedUntil);
        return result;
    }

    /**
     * 手動釋放座位（使用者取消之類）
     */
    public void releaseSeats(Integer showId, List<Integer> seatIds, Integer userId) {
        LocalDateTime now = LocalDateTime.now();

        List<SeatLockBean> locks = seatLockRepo
                .findByShowIdAndSeatIdInAndStatusAndLockedUntilAfter(showId, seatIds, "active", now);

        for (SeatLockBean lock : locks) {
            // 只釋放自己的 lock
            if (Objects.equals(lock.getUserId(), userId)) {
                lock.setStatus("released");
                seatLockRepo.save(lock);
            }
        }
    }

    /**
     * 查某個場次目前 active 的 seat lock
     * 給前端畫「已被佔的座位」（不能選）
     */
    public List<SeatLockBean> getActiveLocksByShow(Integer showId) {
        LocalDateTime now = LocalDateTime.now();
        return seatLockRepo.findByShowIdAndStatusAndLockedUntilAfter(showId, "active", now);
    }

    /**
     * 排程：定期把已過期的 active lock 改成 expired
     * fixedDelay = 60000 代表「前一次執行完後，等 60 秒再跑下一次」
     *
     * 記得在啟動類別上加 @EnableScheduling
     */
    @Scheduled(fixedDelay = 60_000)
    public void expireOldLocks() {
        LocalDateTime now = LocalDateTime.now();
        List<SeatLockBean> expired = seatLockRepo
                .findByStatusAndLockedUntilBefore("active", now);

        if (expired.isEmpty()) {
            return;
        }

        for (SeatLockBean lock : expired) {
            lock.setStatus("expired");
        }

        seatLockRepo.saveAll(expired);
    }

    // 簡單的結果物件
    public static class LockResult {
        private List<Integer> lockedSeatIds;
        private List<Integer> failedSeatIds;
        private LocalDateTime lockedUntil;

        public List<Integer> getLockedSeatIds() { return lockedSeatIds; }
        public void setLockedSeatIds(List<Integer> lockedSeatIds) { this.lockedSeatIds = lockedSeatIds; }

        public List<Integer> getFailedSeatIds() { return failedSeatIds; }
        public void setFailedSeatIds(List<Integer> failedSeatIds) { this.failedSeatIds = failedSeatIds; }

        public LocalDateTime getLockedUntil() { return lockedUntil; }
        public void setLockedUntil(LocalDateTime lockedUntil) { this.lockedUntil = lockedUntil; }
    }
}

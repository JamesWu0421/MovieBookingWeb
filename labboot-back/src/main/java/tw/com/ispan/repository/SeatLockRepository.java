// package tw.com.ispan.repository;

// import java.time.LocalDateTime;
// import java.util.List;

// import org.springframework.data.jpa.repository.JpaRepository;

// import tw.com.ispan.domain.SeatLockBean;

// public interface SeatLockRepository extends JpaRepository<SeatLockBean, Integer> {

//     // 查某個場次的所有 active lock（給前端畫座位圖用）
//     List<SeatLockBean> findByShowIdAndStatusAndLockedUntilAfter(
//             Integer showId, String status, LocalDateTime now);

//     // 查某個場次 + 指定多個 seatId，有沒有 active lock
//     List<SeatLockBean> findByShowIdAndSeatIdInAndStatusAndLockedUntilAfter(
//             Integer showId, List<Integer> seatIds, String status, LocalDateTime now);
// }

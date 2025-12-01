package tw.com.ispan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tw.com.ispan.domain.ShowTicketPricesBean;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShowTicketPricesRepository extends JpaRepository<ShowTicketPricesBean, Long> {

       // ================== 基本查詢 ==================

       /**
        * 根據場次 ID 查詢該場次所有票價（不含是否可售條件）
        */
       @Query("SELECT s FROM ShowTicketPricesBean s WHERE s.showId = :showId")
       List<ShowTicketPricesBean> findByShowId(@Param("showId") Integer showId);

       /**
        * 根據場次 ID 查詢「目前可售」的票價
        */
       @Query("SELECT s FROM ShowTicketPricesBean s " +
                     "WHERE s.showId = :showId AND s.available = true")
       List<ShowTicketPricesBean> findAvailableByShowId(@Param("showId") Integer showId);

       /**
        * 根據場次 ID + 票種套餐 ID 查單筆票價
        * （一個 show + 一個 ticketPackage 一般會對應一組價格）
        */
       @Query("SELECT s FROM ShowTicketPricesBean s " +
                     "WHERE s.showId = :showId AND s.ticketPackage.id = :ticketPackageId")
       ShowTicketPricesBean findByShowIdAndTicketPackageId(@Param("showId") Integer showId,
                     @Param("ticketPackageId") Long ticketPackageId);

       /**
        * 根據票種套餐 ID 查詢所有票價
        */
       @Query("SELECT s FROM ShowTicketPricesBean s WHERE s.ticketPackage.id = :ticketPackageId")
       List<ShowTicketPricesBean> findByTicketPackageId(@Param("ticketPackageId") Long ticketPackageId);

       // ================== 價格相關查詢 ==================

       /**
        * 查詢價格範圍內的票價
        */
       @Query("SELECT s FROM ShowTicketPricesBean s " +
                     "WHERE s.finalPrice >= :minPrice AND s.finalPrice <= :maxPrice")
       List<ShowTicketPricesBean> findByFinalPriceBetween(@Param("minPrice") int minPrice,
                     @Param("maxPrice") int maxPrice);

       // ================== 時間相關查詢（看你要不要用） ==================

       /**
        * 查詢某時間區間內開演的票價（例如早場 / 晚場）
        */
       @Query("SELECT s FROM ShowTicketPricesBean s " +
                     "WHERE s.startTime >= :startTime AND s.startTime < :endTime")
       List<ShowTicketPricesBean> findByStartTimeBetween(@Param("startTime") LocalTime startTime,
                     @Param("endTime") LocalTime endTime);

       /**
        * 查詢在某個時間點「正在播放中的場次票價」
        * （start_time <= time < end_time）
        */
       @Query("SELECT s FROM ShowTicketPricesBean s " +
                     "WHERE s.startTime <= :time AND s.endTime > :time")
       List<ShowTicketPricesBean> findByPlayingAt(@Param("time") LocalTime time);

       Optional<ShowTicketPricesBean> findTopByShowIdAndAvailable(Integer showId, Boolean available);
}

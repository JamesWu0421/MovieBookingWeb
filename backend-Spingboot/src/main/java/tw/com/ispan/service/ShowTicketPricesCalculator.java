package tw.com.ispan.service;


import org.springframework.stereotype.Component;
import tw.com.ispan.domain.ShowTicketPricesBean;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class ShowTicketPricesCalculator {

    /**
     * 根據 Bean 目前的欄位計算：
     * - 是否早鳥 & 早鳥調整額
     * - 片長加價
     * - 最終票價
     * - 設定 calculatedAt
     *
     * 注意：這裡不碰資料庫、不查其他表，外部應該先把
     * showId / startTime / movieDuration / screenBasePrice / ticketAdjustment 等欄位塞好。
     */
    public void calculatePrice(ShowTicketPricesBean bean) {
        if (bean == null) {
            return;
        }

        // ===== 早鳥判斷範例 =====
        // 這裡示意：中午 12:00 前開演視為早鳥，可自行依需求調整
        LocalTime startTime = bean.getStartTime();
        boolean isEarlyBird = false;
        int earlyBirdAdjustment = 0;

        if (startTime != null) {
            LocalTime earlyBirdCutoff = LocalTime.NOON; // 12:00
            isEarlyBird = startTime.isBefore(earlyBirdCutoff);
            if (isEarlyBird) {
                // 假設早鳥折扣 -20 元（只是示意，你可以改成從 TicketPackage 來或別的規則）
                earlyBirdAdjustment = -20;
            }
        }

        bean.setEarlyBird(isEarlyBird);
        bean.setEarlyBirdAdjustment(earlyBirdAdjustment);

        // ===== 片長加價範例 =====
        // > 180 分鐘加 50 元（示意）
        Integer duration = bean.getMovieDuration();
        int durationSurcharge = 0;
        if (duration != null && duration > 180) {
            durationSurcharge = 50;
        }
        bean.setDurationSurcharge(durationSurcharge);

        // ===== 計算最終價 =====
        int basePrice = safeInt(bean.getScreenBasePrice());
        int ticketAdjustment = safeInt(bean.getTicketAdjustment());
        int ebAdjustment = safeInt(bean.getEarlyBirdAdjustment());
        int durSurcharge = safeInt(bean.getDurationSurcharge());

        int finalPrice = basePrice + ticketAdjustment + ebAdjustment + durSurcharge;
        bean.setFinalPrice(finalPrice);

        // ===== 設定計算時間 =====
        bean.setCalculatedAt(LocalDateTime.now());
    }

    /**
     * Null safe 的 int 轉換（把 null 當 0）
     */
    private int safeInt(Integer value) {
        return value != null ? value : 0;
    }
}

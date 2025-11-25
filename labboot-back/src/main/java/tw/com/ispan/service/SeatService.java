package tw.com.ispan.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tw.com.ispan.domain.SeatBean;
import tw.com.ispan.repository.SeatRepository;

@Service
@Transactional
public class SeatService {

    @Autowired
    private SeatRepository seatRepo;

    // 批次新增座位（原本就有）
    public List<SeatBean> batchCreateSeats(
            Integer screenId,
            List<String> rowLabels,
            Integer startSeatNumber,
            Integer endSeatNumber
    ) {
        List<SeatBean> result = new ArrayList<>();

        for (String row : rowLabels) {
            for (int n = startSeatNumber; n <= endSeatNumber; n++) {
                SeatBean seat = new SeatBean();
                seat.setScreenId(screenId);
                seat.setRowLabel(row);
                seat.setSeatNumber(n);
                seat.setIsBlocked(false);

                result.add(seatRepo.save(seat));
            }
        }

        return result;
    }

    // 依影廳查座位（原本就有）
    public List<SeatBean> getSeatsByScreen(Integer screenId) {
        return seatRepo.findByScreenId(screenId);
    }

    // 新增：更新封鎖狀態
    public SeatBean updateBlocked(Integer id, boolean blocked) {
        SeatBean seat = seatRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Seat not found, id=" + id));

        seat.setIsBlocked(blocked);
        // 因為 class 上有 @Transactional，其實不呼叫 save 也會 flush
        // 但為了直覺跟保險，這邊我還是呼叫一次 save
        return seatRepo.save(seat);
    }
}

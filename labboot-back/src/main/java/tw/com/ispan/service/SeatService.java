// package tw.com.ispan.service;

// import java.util.ArrayList;
// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import jakarta.transaction.Transactional;
// import tw.com.ispan.domain.SeatBean;
// import tw.com.ispan.repository.SeatRepository;

// @Service
// @Transactional
// public class SeatService {

//     @Autowired
//     private SeatRepository seatRepo;

//     // 批次新增座位
//     public List<SeatBean> batchCreateSeats(
//             Integer screenId,
//             List<String> rowLabels,
//             Integer startSeatNumber,
//             Integer endSeatNumber
//     ) {
//         List<SeatBean> result = new ArrayList<>();

//         for (String row : rowLabels) {
//             for (int n = startSeatNumber; n <= endSeatNumber; n++) {
//                 SeatBean seat = new SeatBean();
//                 seat.setScreenId(screenId);
//                 seat.setRowLabel(row);
//                 seat.setSeatNumber(n);
//                 seat.setIsBlocked(false);

//                 result.add(seatRepo.save(seat));
//             }
//         }

//         return result;
//     }

//     public List<SeatBean> getSeatsByScreen(Integer screenId) {
//         return seatRepo.findByScreenId(screenId);
//     }
// }

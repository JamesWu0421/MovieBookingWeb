package tw.com.ispan.service;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.sql.Date;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tw.com.ispan.domain.BatchSessionsTempBean;
import tw.com.ispan.domain.ShowBean;
import tw.com.ispan.repository.ShowRepository;

@Service
@Transactional
public class ShowService {

    @Autowired
    private ShowRepository showRepo;

    // 取得全部場次
    public List<ShowBean> getAllShows() {
        List<ShowBean> list = new ArrayList<>();
        showRepo.findAll().forEach(list::add);
        return list;
    }

    // 依 ID 查詢場次
    public Optional<ShowBean> getShowById(Integer id) {
        return showRepo.findById(id);
    }

    // 新增場次
    public ShowBean addShow(ShowBean show) {
        return showRepo.save(show);
    }

    // 更新場次
    public Optional<ShowBean> updateShow(Integer id, ShowBean newData) {
        Optional<ShowBean> oldData = showRepo.findById(id);
        if (oldData.isPresent()) {
            ShowBean show = oldData.get();
            show.setMovieId(newData.getMovieId());
            show.setScreenId(newData.getScreenId());
            show.setShowDate(newData.getShowDate());
            show.setShowTime(newData.getShowTime());
            show.setEndTime(newData.getEndTime());
            return Optional.of(showRepo.save(show));
        }
        return Optional.empty();
    }
    /**
 * 批次匯入使用：由 BatchSessionsTempBean 直接建立場次
 * 會重用原本的 addShow(ShowBean) 的邏輯
 */
public ShowBean createShowFromBatchSessionTemp(BatchSessionsTempBean temp) {

    ShowBean bean = new ShowBean();
    bean.setMovieId(temp.getMovieId());
    bean.setScreenId(temp.getScreenId());

    // ⭐ LocalDate → java.sql.Date
    bean.setShowDate(Date.valueOf(temp.getShowDate()));

    // ⭐ LocalTime → java.sql.Time
    bean.setShowTime(Time.valueOf(temp.getShowTime()));
    bean.setEndTime(Time.valueOf(temp.getEndTime()));

    // ⭐ 重用你原本的單筆新增邏輯
    ShowBean created = this.addShow(bean);

    return created;
}


    // 刪除場次
    public boolean deleteShow(Integer id) {
        if (showRepo.existsById(id)) {
            showRepo.deleteById(id);
            return true;
        }
        return false;
    }
}

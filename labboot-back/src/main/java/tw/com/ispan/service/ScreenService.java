package tw.com.ispan.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tw.com.ispan.domain.ScreenBean;
import tw.com.ispan.repository.ScreenRepository;

@Service
@Transactional
public class ScreenService {

    @Autowired
    private ScreenRepository screenRepo;

    // 取得全部影廳
    public List<ScreenBean> getAllScreens() {
        List<ScreenBean> list = new ArrayList<>();
        screenRepo.findAll().forEach(list::add);
        return list;
    }

    // 依 ID 查詢影廳
    public Optional<ScreenBean> getScreenById(Integer id) {
        return screenRepo.findById(id);
    }

    // 新增影廳
    public ScreenBean addScreen(ScreenBean screen) {
        return screenRepo.save(screen);
    }

    // 更新影廳
    public Optional<ScreenBean> updateScreen(Integer id, ScreenBean newScreenData) {
        Optional<ScreenBean> oldData = screenRepo.findById(id);
        if (oldData.isPresent()) {
            ScreenBean screen = oldData.get();
            screen.setName(newScreenData.getName());
            screen.setTotalSeats(newScreenData.getTotalSeats());
            screen.setScreenType(newScreenData.getScreenType());
            screen.setPrice(newScreenData.getPrice());
            return Optional.of(screenRepo.save(screen));
        }
        return Optional.empty();
    }

    // 刪除影廳
    public boolean deleteScreen(Integer id) {
        if (screenRepo.existsById(id)) {
            screenRepo.deleteById(id);
            return true;
        }
        return false;
    }
}

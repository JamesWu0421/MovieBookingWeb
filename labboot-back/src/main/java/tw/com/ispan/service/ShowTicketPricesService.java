package tw.com.ispan.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.com.ispan.domain.ShowTicketPricesBean;
import tw.com.ispan.domain.TicketPackageBean;
import tw.com.ispan.domain.ShowBean;          // ⬅️ 請確認你實際的 Entity 名稱
import tw.com.ispan.domain.MovieBean;        // ⬅️ 同上
import tw.com.ispan.domain.ScreenBean;       // ⬅️ 同上
import tw.com.ispan.repository.ShowTicketPricesRepository;
import tw.com.ispan.repository.ShowRepository;           // ⬅️ 請確認 package 名稱
import tw.com.ispan.repository.MovieRepository;         // ⬅️
import tw.com.ispan.repository.ScreenRepository;        // ⬅️
import tw.com.ispan.repository.TicketPackageRepository; // ⬅️

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShowTicketPricesService {

    private final ShowTicketPricesRepository repository;
    private final ShowRepository showRepository;
    private final MovieRepository movieRepository;
    private final ScreenRepository screenRepository;
    private final TicketPackageRepository ticketPackageRepository;
    private final ShowTicketPricesCalculator calculator;

    @PersistenceContext
    private EntityManager em; // 用來把只有 id 的 ticketPackage 轉成受管實體

    public ShowTicketPricesService(ShowTicketPricesRepository repository,
                                   ShowRepository showRepository,
                                   MovieRepository movieRepository,
                                   ScreenRepository screenRepository,
                                   TicketPackageRepository ticketPackageRepository,
                                   ShowTicketPricesCalculator calculator) {
        this.repository = repository;
        this.showRepository = showRepository;
        this.movieRepository = movieRepository;
        this.screenRepository = screenRepository;
        this.ticketPackageRepository = ticketPackageRepository;
        this.calculator = calculator;
    }

    // ========= Create（給 Controller 用的：只收 showId + ticketPackageId + isAvailable） =========

    /**
     * 用 showId + ticketPackageId + isAvailable 建立票價
     * 會自動從：
     *  - shows  取 show_time / end_time
     *  - movies 取 runtime_minutes
     *  - screens 取 price
     *  - ticket_packages 取 price_adjustment / early_bird_adjustment
     */
    @Transactional
    public ShowTicketPricesBean createFromIds(Integer showId,
                                              Long ticketPackageId,
                                              Boolean isAvailable) {

        if (showId == null) {
            throw new IllegalArgumentException("showId 不能為空");
        }
        if (ticketPackageId == null) {
            throw new IllegalArgumentException("ticketPackageId 不能為空");
        }

        // 1. 取得 show
        ShowBean show = showRepository.findById(showId)
                .orElseThrow(() -> new EntityNotFoundException("找不到 showId = " + showId));

        // ⭐ 這邊請依你實際的 ShowBean 欄位/關聯名稱對一下
        LocalTime startTime = show.getShowTime() != null
        ? show.getShowTime().toLocalTime()
        : null;

LocalTime endTime = show.getEndTime() != null
        ? show.getEndTime().toLocalTime()
        : null;


        // 2. 取得 movie（拿片長）
   MovieBean movie = movieRepository.findById(show.getMovieId())
    .orElseThrow(() -> new EntityNotFoundException(
        "找不到對應的電影，Movie ID: " + show.getMovieId()));

Integer runtimeMinutes = movie.getRuntimeMinutes(); // e.g. movies.runtime_minutes

// 3. 取得 screen (螢幕別價格)
ScreenBean screen = screenRepository.findById(show.getScreenId())
    .orElseThrow(() -> new EntityNotFoundException(
        "找不到對應的影廳，Screen ID: " + show.getScreenId()));

        Integer basePrice = screen.getPrice(); // e.g. screens.price

        // 4. 取得 ticketPackage（拿各種調整）
        TicketPackageBean ticketPackage = ticketPackageRepository.findById(ticketPackageId)
                .orElseThrow(() -> new EntityNotFoundException("找不到 ticketPackageId = " + ticketPackageId));

        // 5. 建立 ShowTicketPricesBean 並填入所有 NOT NULL 的欄位
        ShowTicketPricesBean bean = new ShowTicketPricesBean();
        bean.setShowId(showId);
        bean.setTicketPackage(em.getReference(TicketPackageBean.class, ticketPackageId));

        bean.setStartTime(startTime);
        bean.setEndTime(endTime);
        bean.setMovieDuration(runtimeMinutes);
        bean.setScreenBasePrice(basePrice);

        bean.setTicketAdjustment(ticketPackage.getPriceAdjustment());
        bean.setEarlyBirdAdjustment(ticketPackage.getEarlyBirdAdjustment());

        bean.setAvailable(isAvailable != null ? isAvailable : Boolean.TRUE);

        // 6. 交給 calculator 算 earlyBird / durationSurcharge / finalPrice / calculatedAt
        calculator.calculatePrice(bean);

        // 7. 存進 DB
        return repository.save(bean);
    }

    // ========= Create（保留你原本的，給別的地方用） =========

    /**
     * 新增單筆票價：
     * 1. attach ticketPackage（避免只有 id 的 detached entity）
     * 2. 用 calculator 計算所有金額欄位
     * 3. 儲存
     */
    @Transactional
    public ShowTicketPricesBean create(ShowTicketPricesBean ticketPrice) {
        attachTicketPackage(ticketPrice);
        calculator.calculatePrice(ticketPrice);
        return repository.save(ticketPrice);
    }

    /**
     * 批次新增票價
     */
    @Transactional
    public List<ShowTicketPricesBean> createBatch(List<ShowTicketPricesBean> ticketPrices) {
        if (ticketPrices == null || ticketPrices.isEmpty()) {
            return ticketPrices;
        }

        for (ShowTicketPricesBean bean : ticketPrices) {
            attachTicketPackage(bean);
            calculator.calculatePrice(bean);
        }
        return repository.saveAll(ticketPrices);
    }

    // ========= Read =========

    @Transactional(readOnly = true)
    public ShowTicketPricesBean findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到 ID 為 " + id + " 的票價資料"));
    }

    @Transactional(readOnly = true)
    public List<ShowTicketPricesBean> findAll() {
        return repository.findAll();
    }

    /**
     * 依場次 ID 查所有票價（含不可售）
     */
    @Transactional(readOnly = true)
    public List<ShowTicketPricesBean> findByShowId(int showId) {
        return repository.findByShowId(showId);
    }

    /**
     * 依場次 ID 查「目前可售」票價
     */
    @Transactional(readOnly = true)
    public List<ShowTicketPricesBean> findAvailableByShowId(int showId) {
        return repository.findAvailableByShowId(showId);
    }

    /**
     * 查場次 + 套票的指定票價
     */
    @Transactional(readOnly = true)
    public ShowTicketPricesBean findByShowIdAndTicketPackageId(int showId, Long ticketPackageId) {
        ShowTicketPricesBean bean =
                repository.findByShowIdAndTicketPackageId(showId, ticketPackageId);
        if (bean == null) {
            throw new EntityNotFoundException("找不到 showId = " + showId +
                    ", ticketPackageId = " + ticketPackageId + " 的票價資料");
        }
        return bean;
    }

    /**
     * 查全系統目前「可售中」的票價
     * （這裡用 Java 過濾，若之後想優化可以在 Repository 加 method）
     */
    @Transactional(readOnly = true)
    public List<ShowTicketPricesBean> findAvailableTicketPrices() {
        return repository.findAll().stream()
                .filter(b -> Boolean.TRUE.equals(b.getAvailable()))
                .collect(Collectors.toList());
    }

    /**
     * 查全部「早鳥」票價
     */
    @Transactional(readOnly = true)
    public List<ShowTicketPricesBean> findEarlyBirdTicketPrices() {
        return repository.findAll().stream()
                .filter(b -> Boolean.TRUE.equals(b.getEarlyBird()))
                .collect(Collectors.toList());
    }

    /**
     * 依場次開始時間區間查票價
     */
    @Transactional(readOnly = true)
    public List<ShowTicketPricesBean> findByTimeRange(LocalTime startTime, LocalTime endTime) {
        return repository.findByStartTimeBetween(startTime, endTime);
    }

    /**
     * 依票種套餐 ID 查所有票價
     */
    @Transactional(readOnly = true)
    public List<ShowTicketPricesBean> findByTicketPackageId(Long ticketPackageId) {
        return repository.findByTicketPackageId(ticketPackageId);
    }

    /**
     * 依最終票價區間查票價
     */
    @Transactional(readOnly = true)
    public List<ShowTicketPricesBean> findByPriceRange(int minPrice, int maxPrice) {
        return repository.findByFinalPriceBetween(minPrice, maxPrice);
    }

    // ========= Update =========

    /**
     * 完整更新一筆票價資料
     */
    @Transactional
    public ShowTicketPricesBean update(Long id, ShowTicketPricesBean ticketPrice) {
        ShowTicketPricesBean existing = findById(id);

        // 基本欄位
        existing.setShowId(ticketPrice.getShowId());
        existing.setStartTime(ticketPrice.getStartTime());
        existing.setEndTime(ticketPrice.getEndTime());
        existing.setMovieDuration(ticketPrice.getMovieDuration());
        existing.setScreenBasePrice(ticketPrice.getScreenBasePrice());

        // 套票關聯（保持 lazy）
        if (ticketPrice.getTicketPackage() != null &&
                ticketPrice.getTicketPackage().getId() != null) {
            Long pkgId = ticketPrice.getTicketPackage().getId();
            existing.setTicketPackage(em.getReference(TicketPackageBean.class, pkgId));
        }

        // 手動調整欄位（若有從外面決定）
        existing.setTicketAdjustment(ticketPrice.getTicketAdjustment());
        existing.setEarlyBird(ticketPrice.getEarlyBird());
        existing.setEarlyBirdAdjustment(ticketPrice.getEarlyBirdAdjustment());
        existing.setDurationSurcharge(ticketPrice.getDurationSurcharge());
        existing.setAvailable(ticketPrice.getAvailable());

        // 重新計價
        calculator.calculatePrice(existing);

        return repository.save(existing);
    }

    /**
     * 只更新「是否可售」欄位
     */
    @Transactional
    public ShowTicketPricesBean updateAvailability(Long id, boolean isAvailable) {
        ShowTicketPricesBean existing = findById(id);
        existing.setAvailable(isAvailable);
        return repository.save(existing);
    }

    /**
     * 重新計算單筆票價
     */
    @Transactional
    public ShowTicketPricesBean recalculatePrice(Long id) {
        ShowTicketPricesBean bean = findById(id);
        calculator.calculatePrice(bean);
        return repository.save(bean);
    }

    /**
     * 依場次 ID 重新計算該場次所有票價
     */
    @Transactional
    public List<ShowTicketPricesBean> recalculatePricesByShowId(int showId) {
        List<ShowTicketPricesBean> list = repository.findByShowId(showId);
        for (ShowTicketPricesBean bean : list) {
            calculator.calculatePrice(bean);
        }
        return repository.saveAll(list);
    }

    // ========= Delete =========

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("刪除失敗，找不到 ID 為 " + id + " 的票價資料");
        }
        repository.deleteById(id);
    }

    @Transactional
    public void deleteByShowId(int showId) {
        List<ShowTicketPricesBean> list = repository.findByShowId(showId);
        if (list.isEmpty()) {
            return;
        }
        repository.deleteAll(list);
    }

    // ========= Others =========

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    @Transactional(readOnly = true)
    public long countByShowId(int showId) {
        return repository.findByShowId(showId).size();
    }

    // ========= Private helper =========

    /**
     * 把只有 id 的 ticketPackage 轉成受管實體，供計價 / 儲存使用
     */
    private void attachTicketPackage(ShowTicketPricesBean bean) {
        if (bean == null) return;

        TicketPackageBean pkg = bean.getTicketPackage();
        if (pkg != null && pkg.getId() != null) {
            Long pkgId = pkg.getId();
            bean.setTicketPackage(em.getReference(TicketPackageBean.class, pkgId));
        }
    }
}
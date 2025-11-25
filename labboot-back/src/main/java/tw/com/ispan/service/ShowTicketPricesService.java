package tw.com.ispan.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.com.ispan.domain.ShowTicketPricesBean;
import tw.com.ispan.domain.TicketPackageBean;
import tw.com.ispan.domain.ShowBean;
import tw.com.ispan.domain.MovieBean;
import tw.com.ispan.domain.ScreenBean;
import tw.com.ispan.dto.ShowTicketPricesRequestDTO;
import tw.com.ispan.dto.ShowTicketPricesResponseDTO;
import tw.com.ispan.mapper.ShowTicketPricesMapper;
import tw.com.ispan.repository.ShowTicketPricesRepository;
import tw.com.ispan.repository.ShowRepository;
import tw.com.ispan.repository.MovieRepository;
import tw.com.ispan.repository.ScreenRepository;
import tw.com.ispan.repository.TicketPackageRepository;

import java.time.LocalTime;
import java.util.List;

@Service
public class ShowTicketPricesService {

    private final ShowTicketPricesRepository repository;
    private final ShowRepository showRepository;
    private final MovieRepository movieRepository;
    private final ScreenRepository screenRepository;
    private final TicketPackageRepository ticketPackageRepository;
    private final ShowTicketPricesCalculator calculator;

    @PersistenceContext
    private EntityManager em;

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

    // ========= Create (使用 DTO) =========

    /**
     * 用 RequestDTO 創建票價
     */
    @Transactional
    public ShowTicketPricesResponseDTO createFromRequest(ShowTicketPricesRequestDTO requestDTO) {
        if (requestDTO.getShowId() == null) {
            throw new IllegalArgumentException("showId 不能為空");
        }
        if (requestDTO.getTicketPackageId() == null) {
            throw new IllegalArgumentException("ticketPackageId 不能為空");
        }

        // 1. 取得 show
        ShowBean show = showRepository.findById(requestDTO.getShowId())
                .orElseThrow(() -> new EntityNotFoundException("找不到 showId = " + requestDTO.getShowId()));

        LocalTime startTime = show.getShowTime() != null
                ? show.getShowTime().toLocalTime()
                : null;

        LocalTime endTime = show.getEndTime() != null
                ? show.getEndTime().toLocalTime()
                : null;

        // 2. 取得 movie (拿片長)
        MovieBean movie = movieRepository.findById(show.getMovieId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "找不到對應的電影,Movie ID: " + show.getMovieId()));

        Integer runtimeMinutes = movie.getRuntimeMinutes();

        // 3. 取得 screen (螢幕別價格)
        ScreenBean screen = screenRepository.findById(show.getScreenId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "找不到對應的影廳,Screen ID: " + show.getScreenId()));

        Integer basePrice = screen.getPrice();

        // 4. 取得 ticketPackage (拿各種調整)
        TicketPackageBean ticketPackage = ticketPackageRepository.findById(requestDTO.getTicketPackageId())
                .orElseThrow(() -> new EntityNotFoundException("找不到 ticketPackageId = " + requestDTO.getTicketPackageId()));

        // 5. 建立 ShowTicketPricesBean 並填入所有 NOT NULL 的欄位
        ShowTicketPricesBean bean = new ShowTicketPricesBean();
        bean.setShowId(requestDTO.getShowId());
        bean.setTicketPackage(em.getReference(TicketPackageBean.class, requestDTO.getTicketPackageId()));

        bean.setStartTime(startTime);
        bean.setEndTime(endTime);
        bean.setMovieDuration(runtimeMinutes);
        bean.setScreenBasePrice(basePrice);

        bean.setTicketAdjustment(ticketPackage.getPriceAdjustment());
        bean.setEarlyBirdAdjustment(ticketPackage.getEarlyBirdAdjustment());

        bean.setAvailable(requestDTO.getIsAvailable() != null ? requestDTO.getIsAvailable() : Boolean.TRUE);

        // 6. 交給 calculator 算 earlyBird / durationSurcharge / finalPrice / calculatedAt
        calculator.calculatePrice(bean);

        // 7. 存進 DB
        ShowTicketPricesBean saved = repository.save(bean);
        
        // 8. 轉換為 ResponseDTO 返回
        return ShowTicketPricesMapper.toResponseDTO(saved);
    }

    // ========= Read (返回 DTO) =========

    /**
     * 根據 ID 查詢 (返回 DTO)
     */
    @Transactional(readOnly = true)
    public ShowTicketPricesResponseDTO findDTOById(Long id) {
        ShowTicketPricesBean entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到 ID 為 " + id + " 的票價資料"));
        return ShowTicketPricesMapper.toResponseDTO(entity);
    }

    /**
     * 查詢所有 (返回 DTO List)
     */
    @Transactional(readOnly = true)
    public List<ShowTicketPricesResponseDTO> findAllDTO() {
        List<ShowTicketPricesBean> entities = repository.findAll();
        return ShowTicketPricesMapper.toResponseDTOList(entities);
    }

    /**
     * 依場次 ID 查所有票價 (返回 DTO List)
     */
    @Transactional(readOnly = true)
    public List<ShowTicketPricesResponseDTO> findDTOByShowId(int showId) {
        List<ShowTicketPricesBean> entities = repository.findByShowId(showId);
        return ShowTicketPricesMapper.toResponseDTOList(entities);
    }

    /**
     * 依場次 ID 查「目前可售」票價 (返回 DTO List)
     */
    @Transactional(readOnly = true)
    public List<ShowTicketPricesResponseDTO> findAvailableDTOByShowId(int showId) {
        List<ShowTicketPricesBean> entities = repository.findAvailableByShowId(showId);
        return ShowTicketPricesMapper.toResponseDTOList(entities);
    }

    /**
     * 依價格範圍查詢 (返回 DTO List)
     */
    @Transactional(readOnly = true)
    public List<ShowTicketPricesResponseDTO> findDTOByPriceRange(int minPrice, int maxPrice) {
        List<ShowTicketPricesBean> entities = repository.findByFinalPriceBetween(minPrice, maxPrice);
        return ShowTicketPricesMapper.toResponseDTOList(entities);
    }

    /**
     * 依時間範圍查詢 (返回 DTO List)
     */
    @Transactional(readOnly = true)
    public List<ShowTicketPricesResponseDTO> findDTOByTimeRange(LocalTime startTime, LocalTime endTime) {
        List<ShowTicketPricesBean> entities = repository.findByStartTimeBetween(startTime, endTime);
        return ShowTicketPricesMapper.toResponseDTOList(entities);
    }

    // ========= Delete =========

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("刪除失敗,找不到 ID 為 " + id + " 的票價資料");
        }
        repository.deleteById(id);
    }

    // ========= 保留舊方法 (向後兼容) =========

    /**
     * @deprecated 請使用 createFromRequest(ShowTicketPricesRequestDTO)
     */
    @Deprecated
    public ShowTicketPricesBean createFromIds(Integer showId,
                                              Long ticketPackageId,
                                              Boolean isAvailable) {

        if (showId == null) {
            throw new IllegalArgumentException("showId 不能為空");
        }
        if (ticketPackageId == null) {
            throw new IllegalArgumentException("ticketPackageId 不能為空");
        }

        ShowBean show = showRepository.findById(showId)
                .orElseThrow(() -> new EntityNotFoundException("找不到 showId = " + showId));

        LocalTime startTime = show.getShowTime() != null
                ? show.getShowTime().toLocalTime()
                : null;

        LocalTime endTime = show.getEndTime() != null
                ? show.getEndTime().toLocalTime()
                : null;

        MovieBean movie = movieRepository.findById(show.getMovieId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "找不到對應的電影,Movie ID: " + show.getMovieId()));

        Integer runtimeMinutes = movie.getRuntimeMinutes();

        ScreenBean screen = screenRepository.findById(show.getScreenId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "找不到對應的影廳,Screen ID: " + show.getScreenId()));

        Integer basePrice = screen.getPrice();

        TicketPackageBean ticketPackage = ticketPackageRepository.findById(ticketPackageId)
                .orElseThrow(() -> new EntityNotFoundException("找不到 ticketPackageId = " + ticketPackageId));

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

        calculator.calculatePrice(bean);

        return repository.save(bean);
    }

    /**
     * @deprecated 請使用 findDTOById(Long)
     */
    @Deprecated
    public ShowTicketPricesBean findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到 ID 為 " + id + " 的票價資料"));
    }

    /**
     * @deprecated 請使用 findAllDTO()
     */
    @Deprecated
    public List<ShowTicketPricesBean> findAll() {
        return repository.findAll();
    }

    /**
     * @deprecated 請使用 findDTOByShowId(int)
     */
    @Deprecated
    public List<ShowTicketPricesBean> findByShowId(int showId) {
        return repository.findByShowId(showId);
    }

    /**
     * @deprecated 請使用 findAvailableDTOByShowId(int)
     */
    @Deprecated
    public List<ShowTicketPricesBean> findAvailableByShowId(int showId) {
        return repository.findAvailableByShowId(showId);
    }

    /**
     * @deprecated 請使用 findDTOByPriceRange(int, int)
     */
    @Deprecated
    public List<ShowTicketPricesBean> findByPriceRange(int minPrice, int maxPrice) {
        return repository.findByFinalPriceBetween(minPrice, maxPrice);
    }

    /**
     * @deprecated 請使用 findDTOByTimeRange(LocalTime, LocalTime)
     */
    @Deprecated
    public List<ShowTicketPricesBean> findByTimeRange(LocalTime startTime, LocalTime endTime) {
        return repository.findByStartTimeBetween(startTime, endTime);
    }
}

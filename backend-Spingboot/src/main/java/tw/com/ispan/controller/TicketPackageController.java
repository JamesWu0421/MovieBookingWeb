package tw.com.ispan.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tw.com.ispan.dto.TicketPackageRequestDTO;
import tw.com.ispan.dto.TicketPackageResponseDTO;
import tw.com.ispan.service.TicketPackageService;

@RestController
@RequestMapping("/api/ticket-packages")
public class TicketPackageController {

    @Autowired
    private TicketPackageService ticketPackageService;

    /**
     * 取得所有票券套餐
     * GET /api/ticket-packages
     */
    @GetMapping
    public ResponseEntity<List<TicketPackageResponseDTO>> getAllTicketPackages() {
        try {
            List<TicketPackageResponseDTO> packageList = ticketPackageService.getAllTicketPackages();
            if (packageList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(packageList, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 依 ID 取得票券套餐
     * GET /api/ticket-packages/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<TicketPackageResponseDTO> getTicketPackageById(@PathVariable Long id) {
        Optional<TicketPackageResponseDTO> packageData = ticketPackageService.getTicketPackageById(id);
        return packageData.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                          .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 新增票券套餐
     * POST /api/ticket-packages
     * Request Body: TicketPackageRequestDTO
     */
    @PostMapping
    public ResponseEntity<?> createTicketPackage(@RequestBody TicketPackageRequestDTO requestDTO) {
        try {
            // 基本驗證
            if (requestDTO.getPackageName() == null || requestDTO.getPackageName().trim().isEmpty()) {
                return new ResponseEntity<>("套餐名稱不能為空", HttpStatus.BAD_REQUEST);
            }
            if (requestDTO.getPackageType() == null || requestDTO.getPackageType().trim().isEmpty()) {
                return new ResponseEntity<>("套餐類型不能為空", HttpStatus.BAD_REQUEST);
            }
            
            TicketPackageResponseDTO created = ticketPackageService.createTicketPackage(requestDTO);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("創建失敗: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 更新票券套餐
     * PUT /api/ticket-packages/{id}
     * Request Body: TicketPackageRequestDTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTicketPackage(
            @PathVariable Long id,
            @RequestBody TicketPackageRequestDTO requestDTO) {
        try {
            // 基本驗證
            if (requestDTO.getPackageName() == null || requestDTO.getPackageName().trim().isEmpty()) {
                return new ResponseEntity<>("套餐名稱不能為空", HttpStatus.BAD_REQUEST);
            }
            if (requestDTO.getPackageType() == null || requestDTO.getPackageType().trim().isEmpty()) {
                return new ResponseEntity<>("套餐類型不能為空", HttpStatus.BAD_REQUEST);
            }
            
            Optional<TicketPackageResponseDTO> updated = ticketPackageService.updateTicketPackage(id, requestDTO);
            return updated.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                          .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("更新失敗: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 刪除票券套餐
     * DELETE /api/ticket-packages/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteTicketPackage(@PathVariable Long id) {
        boolean deleted = ticketPackageService.deleteTicketPackage(id);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                       : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

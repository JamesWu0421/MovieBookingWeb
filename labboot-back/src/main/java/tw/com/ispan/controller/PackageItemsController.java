package tw.com.ispan.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.com.ispan.dto.PackageItemsRequestDTO;
import tw.com.ispan.dto.PackageItemsResponseDTO;
import tw.com.ispan.service.PackageItemsService;

@RestController
@RequestMapping("/api/package-items")
public class PackageItemsController {

    @Autowired
    private PackageItemsService packageItemsService;

    /**
     * 取得所有套餐內容物資訊
     * GET /api/package-items
     */
    @GetMapping
    public ResponseEntity<List<PackageItemsResponseDTO>> getAllPackageItems() {
        try {
            List<PackageItemsResponseDTO> packageList = packageItemsService.getAllPackageItems();
            if (packageList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(packageList, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 依據 ID 取得套票內容物
     * GET /api/package-items/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<PackageItemsResponseDTO> getPackageItemsById(@PathVariable Long id) {
        Optional<PackageItemsResponseDTO> packageData = packageItemsService.getPackageItemsById(id);
        return packageData.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                          .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 新增套票內容物
     * POST /api/package-items
     * Request Body: PackageItemsRequestDTO
     */
    @PostMapping
    public ResponseEntity<?> createPackageItems(@RequestBody PackageItemsRequestDTO requestDTO) {
        try {
            // 基本驗證
            if (requestDTO.getPackageId() == null) {
                return new ResponseEntity<>("套餐ID不能為空", HttpStatus.BAD_REQUEST);
            }
            if (requestDTO.getItemType() == null || requestDTO.getItemType().trim().isEmpty()) {
                return new ResponseEntity<>("項目類型不能為空", HttpStatus.BAD_REQUEST);
            }
            if (requestDTO.getItemName() == null || requestDTO.getItemName().trim().isEmpty()) {
                return new ResponseEntity<>("項目名稱不能為空", HttpStatus.BAD_REQUEST);
            }
            if (requestDTO.getQuantity() < 0) {
                return new ResponseEntity<>("數量不能小於0", HttpStatus.BAD_REQUEST);
            }
            if (requestDTO.getDisplayOrder() < 0) {
                return new ResponseEntity<>("顯示順序不能小於0", HttpStatus.BAD_REQUEST);
            }
            
            PackageItemsResponseDTO created = packageItemsService.createPackageItems(requestDTO);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // 處理找不到 TicketPackage 的情況
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("創建失敗: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 更新套餐商品
     * PUT /api/package-items/{id}
     * Request Body: PackageItemsRequestDTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePackageItems(
            @PathVariable Long id,
            @RequestBody PackageItemsRequestDTO requestDTO) {
        try {
            // 基本驗證
            if (requestDTO.getPackageId() == null) {
                return new ResponseEntity<>("套餐ID不能為空", HttpStatus.BAD_REQUEST);
            }
            if (requestDTO.getItemType() == null || requestDTO.getItemType().trim().isEmpty()) {
                return new ResponseEntity<>("項目類型不能為空", HttpStatus.BAD_REQUEST);
            }
            if (requestDTO.getItemName() == null || requestDTO.getItemName().trim().isEmpty()) {
                return new ResponseEntity<>("項目名稱不能為空", HttpStatus.BAD_REQUEST);
            }
            if (requestDTO.getQuantity() < 0) {
                return new ResponseEntity<>("數量不能小於0", HttpStatus.BAD_REQUEST);
            }
            if (requestDTO.getDisplayOrder() < 0) {
                return new ResponseEntity<>("顯示順序不能小於0", HttpStatus.BAD_REQUEST);
            }
            
            Optional<PackageItemsResponseDTO> updated = packageItemsService.updatePackageItems(id, requestDTO);
            return updated.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                          .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (RuntimeException e) {
            // 處理找不到 TicketPackage 的情況
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("更新失敗: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 刪除套餐商品
     * DELETE /api/package-items/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletePackageItems(@PathVariable Long id) {
        boolean deleted = packageItemsService.deletePackageItems(id);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                       : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}







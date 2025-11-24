package tw.com.ispan.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tw.com.ispan.domain.TicketPackageBean;
import tw.com.ispan.service.TicketPackageService;

@RestController
@RequestMapping("/api/ticket-packages") // 統一路徑開頭
public class TicketPackageController {

    @Autowired
    private TicketPackageService ticketPackageService;

    // 取得所有票券套餐
    @GetMapping
    public ResponseEntity<List<TicketPackageBean>> getAllTicketPackages() {
        try {
            List<TicketPackageBean> packageList = ticketPackageService.getAllTicketPackages();
            if (packageList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(packageList, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 依ID取得票券套餐
    @GetMapping("/{id}")
    public ResponseEntity<TicketPackageBean> getTicketPackageById(@PathVariable Long id) {
        Optional<TicketPackageBean> packageData = ticketPackageService.getTicketPackageById(id);
        return packageData.map(pkg -> new ResponseEntity<>(pkg, HttpStatus.OK))
                          .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // 新增票券套餐
    @PostMapping
    public ResponseEntity<TicketPackageBean> createTicketPackage(@RequestBody TicketPackageBean ticketPackage) {
        try {
            TicketPackageBean created = ticketPackageService.createTicketPackage(ticketPackage);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 更新票券套餐
    @PutMapping("/{id}")
    public ResponseEntity<TicketPackageBean> updateTicketPackage(
            @PathVariable Long id,
            @RequestBody TicketPackageBean ticketPackageDetails) {
        Optional<TicketPackageBean> updated = ticketPackageService.updateTicketPackage(id, ticketPackageDetails);
        return updated.map(pkg -> new ResponseEntity<>(pkg, HttpStatus.OK))
                      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // 刪除票券套餐
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteTicketPackage(@PathVariable Long id) {
        boolean deleted = ticketPackageService.deleteTicketPackage(id);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                       : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}




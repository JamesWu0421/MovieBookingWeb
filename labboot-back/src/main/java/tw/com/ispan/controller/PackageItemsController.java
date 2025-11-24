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

import tw.com.ispan.domain.PackageItemsBean;
import tw.com.ispan.service.PackageItemsService;




@RestController
@RequestMapping("/api/package-items")
public class PackageItemsController {

  @Autowired
  private PackageItemsService packageItemsService;

 //取得所有套餐內容物資訊

 @GetMapping
 public ResponseEntity<List<PackageItemsBean>> getAllPackageItems(){
  try {
            List<PackageItemsBean> packageList = packageItemsService.getAllPackageItems();
            if (packageList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(packageList, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
 }
  //依據id取得套票內容物
   @GetMapping("/{id}")
    public ResponseEntity<PackageItemsBean> getPackageItemsById(@PathVariable Long id) {
        Optional<PackageItemsBean> packageData = packageItemsService
        .getAllPackageItemsById(id);
        return packageData.map(pkg -> new ResponseEntity<>(pkg, HttpStatus.OK))
                          .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    //新增套票內容物
      @PostMapping
    public ResponseEntity<PackageItemsBean> createTicketPackage(@RequestBody PackageItemsBean packageItems) {
        try {
            PackageItemsBean created = packageItemsService.createPackageItems(packageItems);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 更新套餐商品
    @PutMapping("/{id}")
    public ResponseEntity<PackageItemsBean> updateTicketPackage(
            @PathVariable Long id,
            @RequestBody PackageItemsBean packageItemsDetails) {
        Optional<PackageItemsBean> updated = packageItemsService.updatePackageItems(id, packageItemsDetails);
        return updated.map(pkg -> new ResponseEntity<>(pkg, HttpStatus.OK))
                      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // 刪除套餐商品
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteTicketPackage(@PathVariable Long id) {
        boolean deleted = packageItemsService.deletePackageItems(id);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                       : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }






  
}

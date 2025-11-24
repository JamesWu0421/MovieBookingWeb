package tw.com.ispan.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tw.com.ispan.domain.PackageItemsBean;
import tw.com.ispan.repository.PackageItemsRepository;

@Service
@Transactional
public class PackageItemsService {
  
  @Autowired
  private PackageItemsRepository repository;

  // create

  public PackageItemsBean createPackageItems(
    PackageItemsBean packageItems ){
      packageItems.setCreatedAt(LocalDateTime.now());
      packageItems.setUpdatedAt(LocalDateTime.now());
      return repository.save(packageItems);
    }

  //read all
  
  public List<PackageItemsBean> getAllPackageItems(){
    return repository.findAll();
  }

  // read all by id

  public Optional<PackageItemsBean> getAllPackageItemsById(Long id){
    return repository.findById(id);
  }

  // update

  public Optional<PackageItemsBean> updatePackageItems(
    Long id, PackageItemsBean packageItemsDetail){
      Optional<PackageItemsBean> oldData = repository.findById(id);
      if (oldData.isPresent()) {
        PackageItemsBean packageItems = oldData.get();

      if (packageItemsDetail.getTicketPackage() != null) {
        packageItems.setTicketPackage(packageItemsDetail.getTicketPackage());
      }
      if (packageItemsDetail.getItemType() != null) {
        packageItems.setItemType(packageItemsDetail.getItemType());
      }
      if (packageItemsDetail.getItemName() != null) {
        packageItems.setItemName(packageItemsDetail.getItemName());
      }
      if (packageItemsDetail.getItemSpec() != null) {
        packageItems.setItemSpec(packageItemsDetail.getItemSpec());
      }
      // quantity 和 displayOrder 是 primitive type,直接設定
      packageItems.setQuantity(packageItemsDetail.getQuantity());
      packageItems.setDisplayOrder(packageItemsDetail.getDisplayOrder());
      
      // @PreUpdate 會自動更新 updatedAt
      PackageItemsBean updated = repository.save(packageItems);
      return Optional.of(updated);



      }
      return Optional.empty();

    }

    public boolean deletePackageItems(Long id) {
    if (repository.existsById(id)) {
      repository.deleteById(id);
      return true;
    }
    return false;
  }

}

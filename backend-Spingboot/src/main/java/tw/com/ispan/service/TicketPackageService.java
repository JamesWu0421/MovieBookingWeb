package tw.com.ispan.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tw.com.ispan.entity.TicketPackageBean;
import tw.com.ispan.repository.TicketPackageRepository;

@Service
@Transactional
public class TicketPackageService {

    @Autowired
    private TicketPackageRepository repository;

    // ==================== Create ====================
    public TicketPackageBean createTicketPackage(TicketPackageBean ticketPackage) {
        ticketPackage.setCreatedAt(LocalDateTime.now());
        ticketPackage.setUpdatedAt(LocalDateTime.now());
        return repository.save(ticketPackage);
    }

    // ==================== Read All ====================
    public List<TicketPackageBean> getAllTicketPackages() {
        return repository.findAll();
    }

    // ==================== Read by ID ====================
    public Optional<TicketPackageBean> getTicketPackageById(Long id) {
        return repository.findById(id);
    }

    // ==================== Update ====================
    public Optional<TicketPackageBean> updateTicketPackage(Long id, TicketPackageBean ticketPackageDetails) {
        Optional<TicketPackageBean> oldData = repository.findById(id);
        if (oldData.isPresent()) {
            TicketPackageBean ticketPackage = oldData.get();

            ticketPackage.setPackageType(ticketPackageDetails.getPackageType());
            ticketPackage.setPackageName(ticketPackageDetails.getPackageName());
            ticketPackage.setPackageCode(ticketPackageDetails.getPackageCode());
            ticketPackage.setPriceAdjustment(ticketPackageDetails.getPriceAdjustment());
            ticketPackage.setEarlyBirdAdjustment(ticketPackageDetails.getEarlyBirdAdjustment());
            ticketPackage.setImageUrl(ticketPackageDetails.getImageUrl());
            ticketPackage.setIsActive(ticketPackageDetails.getIsActive());
            ticketPackage.setDisplayOrder(ticketPackageDetails.getDisplayOrder());
            ticketPackage.setValidFrom(ticketPackageDetails.getValidFrom());
            ticketPackage.setValidUntil(ticketPackageDetails.getValidUntil());
            ticketPackage.setUpdatedAt(LocalDateTime.now());

            return Optional.of(repository.save(ticketPackage));
        }
        return Optional.empty();
    }

    // ==================== Delete ====================
    public boolean deleteTicketPackage(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}

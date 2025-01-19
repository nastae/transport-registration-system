package lt.transport.registration.repository;

import lt.transport.registration.entity.VehicleOwnershipHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleOwnershipHistoryRepository extends JpaRepository<VehicleOwnershipHistory, Long> {
}

package lt.transport.registration.repository;

import lt.transport.registration.entity.VehicleRegistration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleRegistrationRepository extends JpaRepository<VehicleRegistration, Long> {

    Optional<VehicleRegistration> findVehicleRegistrationByPlateNoIgnoreCaseAndIsDeletedFalse(String plateNo);

    Page<VehicleRegistration> findVehicleRegistrationByIsDeletedFalse(Pageable pageable);

    Optional<VehicleRegistration> findVehicleRegistrationByIdAndIsDeletedFalse(Long id);
}

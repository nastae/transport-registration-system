package lt.transport.registration.repository;

import lt.transport.registration.entity.VehicleRegistration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRegistrationRepository extends JpaRepository<VehicleRegistration, Long> {

    @Query("SELECT v FROM VehicleRegistration v WHERE LOWER(v.plateNo) = LOWER(:plateNo) AND v.isDeleted = false")
    Optional<VehicleRegistration> findByPlateNoIgnoreCaseAndNotDeleted(@Param("plateNo") String plateNo);

    @Query("SELECT v FROM VehicleRegistration v WHERE v.isDeleted = false")
    Page<VehicleRegistration> findAllNotDeleted(Pageable pageable);

    Optional<VehicleRegistration> findByIdAndIsDeletedFalse(Long id);

    List<VehicleRegistration> findByIsDeletedFalse();
}

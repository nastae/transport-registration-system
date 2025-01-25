package lt.transport.registration.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "vehicle_registration", indexes = {
        @Index(name = "idx_plate_no", columnList = "plateNo", unique = true)
})
public class VehicleRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plate_no", nullable = false, unique = true)
    private String plateNo;

    @Column(name = "make", nullable = false)
    private String make;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "vehicle_year", nullable = false)
    private Integer year;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(name = "owner_surname", nullable = false)
    private String ownerSurname;

    @Column(name = "owner_legal_name")
    private String ownerLegalName;

    @Column(name = "owner_code", nullable = false)
    private String ownerCode;

    @OneToMany(mappedBy = "vehicleRegistration", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VehicleOwnershipHistory> ownershipHistory = new ArrayList<>();

    @Column(name = "is_deleted")
    private boolean isDeleted;
}

package lt.transport.registration.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "vehicle_ownership_history")
public class VehicleOwnershipHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vehicle_registration_id", nullable = false)
    private VehicleRegistration vehicleRegistration;

    private String ownerName;
    private String ownerSurname;
    private String ownerLegalName;
    private String ownerCode;

    private LocalDateTime transferDate;
}

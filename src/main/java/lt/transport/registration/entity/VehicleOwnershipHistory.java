package lt.transport.registration.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

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

    public VehicleOwnershipHistory() {
    }

    public VehicleOwnershipHistory(VehicleRegistration vehicleRegistration, String ownerName, String ownerSurname, String ownerLegalName, String ownerCode) {
        this.vehicleRegistration = vehicleRegistration;
        this.ownerName = ownerName;
        this.ownerSurname = ownerSurname;
        this.ownerLegalName = ownerLegalName;
        this.ownerCode = ownerCode;
        this.transferDate = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VehicleRegistration getVehicleRegistration() {
        return vehicleRegistration;
    }

    public void setVehicleRegistration(VehicleRegistration vehicleRegistration) {
        this.vehicleRegistration = vehicleRegistration;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerSurname() {
        return ownerSurname;
    }

    public void setOwnerSurname(String ownerSurname) {
        this.ownerSurname = ownerSurname;
    }

    public String getOwnerLegalName() {
        return ownerLegalName;
    }

    public void setOwnerLegalName(String ownerLegalName) {
        this.ownerLegalName = ownerLegalName;
    }

    public String getOwnerCode() {
        return ownerCode;
    }

    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
    }

    public LocalDateTime getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(LocalDateTime transferDate) {
        this.transferDate = transferDate;
    }
}

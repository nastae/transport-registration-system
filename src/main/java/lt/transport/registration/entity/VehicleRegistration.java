package lt.transport.registration.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

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

    public VehicleRegistration() {
    }

    public VehicleRegistration(String plateNo, String make, String model, Integer year, String ownerName, String ownerSurname, String ownerLegalName, String ownerCode) {
        this.plateNo = plateNo;
        this.make = make;
        this.model = model;
        this.year = year;
        this.ownerName = ownerName;
        this.ownerSurname = ownerSurname;
        this.ownerLegalName = ownerLegalName;
        this.ownerCode = ownerCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlateNo() {
        return plateNo;
    }

    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
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

    public List<VehicleOwnershipHistory> getOwnershipHistory() {
        return ownershipHistory;
    }

    public void setOwnershipHistory(List<VehicleOwnershipHistory> ownershipHistory) {
        this.ownershipHistory = ownershipHistory;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    @Override
    public String toString() {
        return "VehicleRegistration{" +
                "id=" + id +
                ", plateNo='" + plateNo + '\'' +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", ownerName='" + ownerName + '\'' +
                ", ownerSurname='" + ownerSurname + '\'' +
                ", ownerLegalName='" + ownerLegalName + '\'' +
                ", ownerCode='" + ownerCode + '\'' +
                ", ownershipHistory=" + ownershipHistory +
                ", isDeleted=" + isDeleted +
                '}';
    }
}

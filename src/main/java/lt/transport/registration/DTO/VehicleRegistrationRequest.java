package lt.transport.registration.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Transporto priemonės registravimo duomenys")
public class VehicleRegistrationRequest {

    @Schema(description = "Transporto priemonės valstybinis numeris", example = "ABC123")
    private String plateNo;
    @Schema(description = "Transporto priemonės gamintojas", example = "Toyota")
    private String make;
    @Schema(description = "Transporto priemonės modelis", example = "Corolla")
    private String model;
    @Schema(description = "Transporto priemonės pagaminimo metai", example = "2020")
    private Integer year;
    @Schema(description = "Transporto priemonės savininko vardas", example = "Jonas")
    private String ownerName;
    @Schema(description = "Transporto priemonės savininko pavardė", example = "Petrauskas")
    private String ownerSurname;
    @Schema(description = "Transporto priemonės savininko įmonės pavadinimas", example = "UAB Petras")
    private String ownerLegalName;
    @Schema(description = "Transporto priemonės savininko asmens arba įmonės kodas ", example = "39601010000")
    private String ownerCode;

    public VehicleRegistrationRequest() {
    }

    public VehicleRegistrationRequest(String plateNo, String make, String model, Integer year, String ownerName, String ownerSurname, String ownerLegalName, String ownerCode) {
        this.plateNo = plateNo;
        this.make = make;
        this.model = model;
        this.year = year;
        this.ownerName = ownerName;
        this.ownerSurname = ownerSurname;
        this.ownerLegalName = ownerLegalName;
        this.ownerCode = ownerCode;
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
}

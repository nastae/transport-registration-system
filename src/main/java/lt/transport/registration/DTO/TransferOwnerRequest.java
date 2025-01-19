package lt.transport.registration.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Transporto priemonės savininkystės perėjimo duomenys")
public class TransferOwnerRequest {

    @Schema(description = "Transporto priemonės naujo savininko vardas", example = "Petras")
    private String newOwnerName;
    @Schema(description = "Transporto priemonės naujo savininko pavardė", example = "Petrauskas")
    private String newOwnerSurname;
    @Schema(description = "Transporto priemonės naujo savininko įmonės pavadinimas", example = "UAB Petras")
    private String newOwnerLegalName;
    @Schema(description = "Transporto priemonės naujo savininko asmens arba įmonės kodas ", example = "39601010000")
    private String newOwnerCode;

    public String getNewOwnerName() {
        return newOwnerName;
    }

    public void setNewOwnerName(String newOwnerName) {
        this.newOwnerName = newOwnerName;
    }

    public String getNewOwnerSurname() {
        return newOwnerSurname;
    }

    public void setNewOwnerSurname(String newOwnerSurname) {
        this.newOwnerSurname = newOwnerSurname;
    }

    public String getNewOwnerLegalName() {
        return newOwnerLegalName;
    }

    public void setNewOwnerLegalName(String newOwnerLegalName) {
        this.newOwnerLegalName = newOwnerLegalName;
    }

    public String getNewOwnerCode() {
        return newOwnerCode;
    }

    public void setNewOwnerCode(String newOwnerCode) {
        this.newOwnerCode = newOwnerCode;
    }
}

package lt.transport.registration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import static lt.transport.registration.constants.ResponseMessages.OWNER_CODE_IS_REQUIRED;
import static lt.transport.registration.constants.ResponseMessages.OWNER_NAME_IS_REQUIRED;
import static lt.transport.registration.constants.ResponseMessages.OWNER_SURNAME_IS_REQUIRED;

@Schema(description = "Transporto priemonės savininkystės perėjimo duomenys")
public record TransferOwnerRequest(
        @NotBlank(message = OWNER_NAME_IS_REQUIRED)
        @Schema(description = "Transporto priemonės naujo savininko vardas", example = "Petras")
        String newOwnerName,
        @NotBlank(message = OWNER_SURNAME_IS_REQUIRED)
        @Schema(description = "Transporto priemonės naujo savininko pavardė", example = "Petrauskas")
        String newOwnerSurname,
        @Schema(description = "Transporto priemonės naujo savininko įmonės pavadinimas", example = "UAB Petras")
        String newOwnerLegalName,
        @NotBlank(message = OWNER_CODE_IS_REQUIRED)
        @Schema(description = "Transporto priemonės naujo savininko asmens arba įmonės kodas ", example = "39601010000")
        String newOwnerCode) {
}

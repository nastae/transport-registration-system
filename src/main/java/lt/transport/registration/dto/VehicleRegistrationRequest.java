package lt.transport.registration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import static lt.transport.registration.constants.ResponseMessages.MAKE_IS_REQUIRED;
import static lt.transport.registration.constants.ResponseMessages.MODEL_IS_REQUIRED;
import static lt.transport.registration.constants.ResponseMessages.OWNER_CODE_IS_REQUIRED;
import static lt.transport.registration.constants.ResponseMessages.OWNER_NAME_IS_REQUIRED;
import static lt.transport.registration.constants.ResponseMessages.OWNER_SURNAME_IS_REQUIRED;
import static lt.transport.registration.constants.ResponseMessages.PLATE_NO_IS_REQUIRED;
import static lt.transport.registration.constants.ResponseMessages.YEAR_IS_REQUIRED;

@Schema(description = "Transporto priemonės registravimo duomenys")
public record VehicleRegistrationRequest(
        @NotBlank(message = PLATE_NO_IS_REQUIRED)
        @Schema(description = "Transporto priemonės valstybinis numeris", example = "ABC123")
        String plateNo,
        @NotBlank(message = MAKE_IS_REQUIRED)
        @Schema(description = "Transporto priemonės gamintojas", example = "Toyota")
        String make,
        @NotBlank(message = MODEL_IS_REQUIRED)
        @Schema(description = "Transporto priemonės modelis", example = "Corolla")
        String model,
        @NotNull(message = YEAR_IS_REQUIRED)
        @Schema(description = "Transporto priemonės pagaminimo metai", example = "2020")
        Integer year,
        @NotBlank(message = OWNER_NAME_IS_REQUIRED)
        @Schema(description = "Transporto priemonės savininko vardas", example = "Jonas")
        String ownerName,
        @NotBlank(message = OWNER_SURNAME_IS_REQUIRED)
        @Schema(description = "Transporto priemonės savininko pavardė", example = "Petrauskas")
        String ownerSurname,
        @Schema(description = "Transporto priemonės savininko įmonės pavadinimas", example = "UAB Petras")
        String ownerLegalName,
        @NotBlank(message = OWNER_CODE_IS_REQUIRED)
        @Schema(description = "Transporto priemonės savininko asmens arba įmonės kodas ", example = "39601010000")
        String ownerCode) {
}

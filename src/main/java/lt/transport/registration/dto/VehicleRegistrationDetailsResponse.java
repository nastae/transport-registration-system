package lt.transport.registration.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Transporto priemonės registravimo atsakymo duomenys")
public record VehicleRegistrationDetailsResponse(
        @Schema(description = "Unikalus transporto priemonės ID", example = "1")
        Long vehicleId,
        @Schema(description = "Transporto priemonės valstybinis numeris", example = "ABC123")
        String plateNo,
        @Schema(description = "Transporto priemonės gamintojas", example = "Toyota")
        String make,
        @Schema(description = "Transporto priemonės modelis", example = "Corolla")
        String model,
        @Schema(description = "Transporto priemonės pagaminimo metai", example = "2020")
        int year,
        @Schema(description = "Transporto priemonės savininko vardas", example = "Jonas")
        String ownerName,
        @Schema(description = "Transporto priemonės savininko pavardė", example = "Petrauskas")
        String ownerSurname,
        @Schema(description = "Transporto priemonės savininko įmonės pavadinimas", example = "UAB Petras")
        String ownerLegalName,
        @Schema(description = "Transporto priemonės savininko asmens arba įmonės kodas ", example = "39601010000")
        String ownerCode) {
}

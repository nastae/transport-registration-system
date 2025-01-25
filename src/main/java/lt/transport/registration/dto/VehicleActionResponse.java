package lt.transport.registration.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import static lt.transport.registration.constants.ResponseMessages.VEHICLE_REGISTERED_SUCCESSFULLY;

@Schema(description = "Atsakymas į veiksmą susijusi su transporto priemone")
public record VehicleActionResponse(
        @Schema(description = "Atsakymo žinutė apie atliktą veiksmą", example = VEHICLE_REGISTERED_SUCCESSFULLY)
        String message,
        @Schema(description = "Transporto priemonės ID", example = "1")
        Long vehicleId) {
}

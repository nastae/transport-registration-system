package lt.transport.registration.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

import static lt.transport.registration.constants.ResponseMessages.VEHICLE_REGISTERED_SUCCESSFULLY;

@Schema(description = "Atsakymas į veiksmą susijusi su transporto priemone")
public class VehicleActionResponse {

    @Schema(description = "Atsakymo žinutė apie atliktą veiksmą", example = VEHICLE_REGISTERED_SUCCESSFULLY)
    private String message;
    @Schema(description = "Transporto priemonės ID", example = "1")
    private Long vehicleId;

    public VehicleActionResponse() {
    }

    public VehicleActionResponse(String message, Long vehicleId) {
        this.message = message;
        this.vehicleId = vehicleId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }
}

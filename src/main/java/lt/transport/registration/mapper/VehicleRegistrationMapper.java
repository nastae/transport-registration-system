package lt.transport.registration.mapper;

import lt.transport.registration.DTO.VehicleActionResponse;
import lt.transport.registration.DTO.VehicleRegistrationDetailsResponse;
import lt.transport.registration.DTO.VehicleRegistrationRequest;
import lt.transport.registration.entity.VehicleRegistration;

public class VehicleRegistrationMapper {

    public static VehicleRegistrationDetailsResponse toDto(VehicleRegistration vehicleRegistration) {
        return new VehicleRegistrationDetailsResponse(
                vehicleRegistration.getId(),
                vehicleRegistration.getPlateNo(),
                vehicleRegistration.getMake(),
                vehicleRegistration.getModel(),
                vehicleRegistration.getYear(),
                vehicleRegistration.getOwnerName(),
                vehicleRegistration.getOwnerSurname(),
                vehicleRegistration.getOwnerLegalName(),
                vehicleRegistration.getOwnerCode()
        );
    }

    public static VehicleRegistration toEntity(VehicleRegistrationRequest request) {
        VehicleRegistration entity = new VehicleRegistration();
        entity.setPlateNo(request.getPlateNo());
        entity.setMake(request.getMake());
        entity.setModel(request.getModel());
        entity.setYear(request.getYear());
        entity.setOwnerName(request.getOwnerName());
        entity.setOwnerSurname(request.getOwnerSurname());
        entity.setOwnerLegalName(request.getOwnerLegalName());
        entity.setOwnerCode(request.getOwnerCode());
        return entity;
    }

    public static VehicleActionResponse toVehicleActionResponse(String message, VehicleRegistration vehicleRegistration) {
        return new VehicleActionResponse(message, vehicleRegistration.getId());
    }
}

package lt.transport.registration.mapper;

import lt.transport.registration.dto.VehicleActionResponse;
import lt.transport.registration.dto.VehicleRegistrationDetailsResponse;
import lt.transport.registration.dto.VehicleRegistrationRequest;
import lt.transport.registration.entity.VehicleRegistration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface VehicleRegistrationMapper {

    VehicleRegistrationMapper INSTANCE = Mappers.getMapper(VehicleRegistrationMapper.class);

    @Mapping(source = "vehicleRegistration.id", target = "vehicleId")
    VehicleRegistrationDetailsResponse toDto(VehicleRegistration vehicleRegistration);

    VehicleRegistration toEntity(VehicleRegistrationRequest request);

    @Mapping(source = "vehicleRegistration.id", target = "vehicleId")
    VehicleActionResponse toVehicleActionResponse(String message, VehicleRegistration vehicleRegistration);
}

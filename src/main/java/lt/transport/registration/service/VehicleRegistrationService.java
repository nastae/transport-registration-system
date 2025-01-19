package lt.transport.registration.service;

import jakarta.transaction.Transactional;
import lt.transport.registration.DTO.VehicleRegistrationDetailsResponse;
import lt.transport.registration.DTO.VehicleRegistrationPageResponse;
import lt.transport.registration.DTO.VehicleRegistrationRequest;
import lt.transport.registration.entity.VehicleOwnershipHistory;
import lt.transport.registration.entity.VehicleRegistration;
import lt.transport.registration.exception.PlateNoAlreadyExistsException;
import lt.transport.registration.exception.VehicleNotFoundException;
import lt.transport.registration.mapper.VehicleRegistrationMapper;
import lt.transport.registration.repository.VehicleOwnershipHistoryRepository;
import lt.transport.registration.repository.VehicleRegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static lt.transport.registration.constants.ResponseMessages.*;

@Service
public class VehicleRegistrationService {

    private final VehicleRegistrationRepository repository;

    private final VehicleOwnershipHistoryRepository historyRepository;

    @Autowired
    public VehicleRegistrationService(VehicleRegistrationRepository repository, VehicleOwnershipHistoryRepository historyRepository) {
        this.repository = repository;
        this.historyRepository = historyRepository;
    }

    public VehicleRegistration save(VehicleRegistrationRequest vehicleRegistrationRequest) {
        if (vehicleRegistrationRequest == null) {
            throw new IllegalArgumentException(REQUEST_BODY_CANNOT_BE_NULL);
        }
        if (vehicleRegistrationRequest.getPlateNo() == null || vehicleRegistrationRequest.getPlateNo().isEmpty()) {
            throw new IllegalArgumentException(PLATE_NO_IS_REQUIRED);
        }
        if (vehicleRegistrationRequest.getMake() == null || vehicleRegistrationRequest.getMake().isEmpty()) {
            throw new IllegalArgumentException(MAKE_IS_REQUIRED);
        }
        if (vehicleRegistrationRequest.getModel() == null || vehicleRegistrationRequest.getModel().isEmpty()) {
            throw new IllegalArgumentException(MODEL_IS_REQUIRED);
        }
        if (vehicleRegistrationRequest.getYear() == null) {
            throw new IllegalArgumentException(YEAR_IS_REQUIRED);
        }
        if (vehicleRegistrationRequest.getOwnerName() == null || (vehicleRegistrationRequest.getOwnerName() != null && vehicleRegistrationRequest.getOwnerName().isEmpty())) {
            throw new IllegalArgumentException(OWNER_NAME_IS_REQUIRED);
        }
        if (vehicleRegistrationRequest.getOwnerSurname() == null || (vehicleRegistrationRequest.getOwnerSurname() != null && vehicleRegistrationRequest.getOwnerSurname().isEmpty())) {
            throw new IllegalArgumentException(OWNER_SURNAME_IS_REQUIRED);
        }
        if (vehicleRegistrationRequest.getOwnerCode() == null || vehicleRegistrationRequest.getOwnerCode().isEmpty()) {
            throw new IllegalArgumentException(OWNER_CODE_IS_REQUIRED);
        }

        Optional<VehicleRegistration> existingVehicle = repository.findByPlateNoIgnoreCaseAndNotDeleted(vehicleRegistrationRequest.getPlateNo());

        if (existingVehicle.isPresent()) {
            throw new PlateNoAlreadyExistsException(PLATE_NO_ALREADY_EXISTS);
        }

        return repository.save(VehicleRegistrationMapper.toEntity(vehicleRegistrationRequest));
    }

    public VehicleRegistrationDetailsResponse findById(Long id) {
        Optional<VehicleRegistration> vehicleRegistration = repository.findByIdAndIsDeletedFalse(id);
        if (vehicleRegistration.isEmpty()) {
            throw new VehicleNotFoundException(String.format(VEHICLE_NOT_FOUND, id));
        }
        return VehicleRegistrationMapper.toDto(vehicleRegistration.get());
    }

    public VehicleRegistrationPageResponse findAll(int page, int size, String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<VehicleRegistrationDetailsResponse> vehicleRegistrations = repository.findAllNotDeleted(pageable).map(VehicleRegistrationMapper::toDto);
        return new VehicleRegistrationPageResponse(
                vehicleRegistrations.getContent(),
                page,
                size,
                vehicleRegistrations.getTotalPages(),
                vehicleRegistrations.getTotalElements()
        );
    }

    @Transactional
    public VehicleRegistration transferOwner(Long vehicleId, String newOwnerName, String newOwnerSurname, String newOwnerLegalName,
                                             String newOwnerCode) {
        if (newOwnerName == null || newOwnerName.isEmpty()) {
            throw new IllegalArgumentException(OWNER_NAME_IS_REQUIRED);
        }
        if (newOwnerSurname == null || newOwnerSurname.isEmpty()) {
            throw new IllegalArgumentException(OWNER_SURNAME_IS_REQUIRED);
        }
        if (newOwnerCode == null || newOwnerCode.isEmpty()) {
            throw new IllegalArgumentException(OWNER_CODE_IS_REQUIRED);
        }

        Optional<VehicleRegistration> vehicleOptional = repository.findByIdAndIsDeletedFalse(vehicleId);

        if (vehicleOptional.isEmpty()) {
            throw new VehicleNotFoundException(String.format(VEHICLE_NOT_FOUND, vehicleId));
        }

        VehicleRegistration vehicleRegistration = vehicleOptional.get();
        if ((vehicleRegistration.getOwnerName() == null || (vehicleRegistration.getOwnerName() != null && vehicleRegistration.getOwnerName().isEmpty()))
                || (vehicleRegistration.getOwnerSurname() == null || (vehicleRegistration.getOwnerSurname() != null && vehicleRegistration.getOwnerSurname().isEmpty()))
                || (vehicleRegistration.getOwnerCode() == null || vehicleRegistration.getOwnerCode() != null && (vehicleRegistration.getOwnerCode().isEmpty()))) {
            throw new IllegalStateException(CURRENT_OWNER_OF_THE_VEHICLE_NOT_FOUND);
        }
        VehicleOwnershipHistory history = new VehicleOwnershipHistory(
                vehicleRegistration,
                vehicleRegistration.getOwnerName(),
                vehicleRegistration.getOwnerSurname(),
                vehicleRegistration.getOwnerLegalName(),
                vehicleRegistration.getOwnerCode()
        );

        historyRepository.save(history);
        vehicleRegistration.getOwnershipHistory().add(history);

        vehicleRegistration.setOwnerName(newOwnerName);
        vehicleRegistration.setOwnerSurname(newOwnerSurname);
        vehicleRegistration.setOwnerLegalName(newOwnerLegalName);
        vehicleRegistration.setOwnerCode(newOwnerCode);
        return repository.save(vehicleRegistration);
    }

    public VehicleRegistration delete(Long vehicleId) {
        Optional<VehicleRegistration> vehicle = repository.findByIdAndIsDeletedFalse(vehicleId);

        if (vehicle.isEmpty()) {
            throw new VehicleNotFoundException(String.format(VEHICLE_NOT_FOUND, vehicleId));
        }

        VehicleRegistration vehicleRegistration = vehicle.get();
        vehicleRegistration.setDeleted(true);
        return repository.save(vehicleRegistration);
    }
}

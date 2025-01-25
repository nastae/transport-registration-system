package lt.transport.registration.service;

import jakarta.transaction.Transactional;
import lt.transport.registration.dto.VehicleRegistrationDetailsResponse;
import lt.transport.registration.dto.VehicleRegistrationPageResponse;
import lt.transport.registration.dto.VehicleRegistrationRequest;
import lt.transport.registration.entity.VehicleOwnershipHistory;
import lt.transport.registration.entity.VehicleRegistration;
import lt.transport.registration.exception.PlateNoAlreadyExistsException;
import lt.transport.registration.exception.VehicleNotFoundException;
import lt.transport.registration.mapper.VehicleRegistrationMapper;
import lt.transport.registration.repository.VehicleOwnershipHistoryRepository;
import lt.transport.registration.repository.VehicleRegistrationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static lt.transport.registration.constants.ResponseMessages.CURRENT_OWNER_OF_THE_VEHICLE_NOT_FOUND;
import static lt.transport.registration.constants.ResponseMessages.PLATE_NO_ALREADY_EXISTS;
import static lt.transport.registration.constants.ResponseMessages.VEHICLE_NOT_FOUND;

@Service
public class VehicleRegistrationService {

    private final VehicleRegistrationRepository vehicleRegistrationRepository;

    private final VehicleOwnershipHistoryRepository vehicleOwnershipHistoryRepository;

    private static final Logger logger = LoggerFactory.getLogger(VehicleRegistrationService.class);

    @Autowired
    public VehicleRegistrationService(VehicleRegistrationRepository vehicleRegistrationRepository, VehicleOwnershipHistoryRepository vehicleOwnershipHistoryRepository) {
        this.vehicleRegistrationRepository = vehicleRegistrationRepository;
        this.vehicleOwnershipHistoryRepository = vehicleOwnershipHistoryRepository;
    }

    public VehicleRegistration saveVehicleRegistration(VehicleRegistrationRequest vehicleRegistrationRequest) {
        logger.info("Attempting to save vehicle registration for plateNo: {}", vehicleRegistrationRequest.plateNo());
        if (vehicleRegistrationRepository.findVehicleRegistrationByPlateNoIgnoreCaseAndIsDeletedFalse(vehicleRegistrationRequest.plateNo()).isPresent()) {
            logger.error("Plate number {} already exists in the system", vehicleRegistrationRequest.plateNo());
            throw new PlateNoAlreadyExistsException(PLATE_NO_ALREADY_EXISTS);
        }

        VehicleRegistration vehicleRegistration = VehicleRegistrationMapper.INSTANCE.toEntity(vehicleRegistrationRequest);
        vehicleRegistration.setPlateNo(vehicleRegistration.getPlateNo().toLowerCase());
        logger.debug("Mapped VehicleRegistration entity: {}", vehicleRegistration);

        VehicleRegistration savedVehicleRegistration = vehicleRegistrationRepository.save(vehicleRegistration);
        logger.info("Vehicle registration saved successfully with ID: {}", savedVehicleRegistration.getId());
        return savedVehicleRegistration;
    }

    public VehicleRegistrationDetailsResponse findVehicleRegistrationById(Long id) {
        logger.info("Searching for vehicle registration with ID: {}", id);
         return vehicleRegistrationRepository.findVehicleRegistrationByIdAndIsDeletedFalse(id)
                .map(v -> {
                    logger.info("Vehicle registration found with ID: {}", id);
                    return VehicleRegistrationMapper.INSTANCE.toDto(v);
                })
                .orElseThrow(() -> {
                    logger.error("Vehicle registration with ID {} not found", id);
                    return new VehicleNotFoundException(String.format(VEHICLE_NOT_FOUND, id));
                });
    }

    public VehicleRegistrationPageResponse findAllVehicleRegistrations(int page, int size, String sortBy, String sortDirection) {
        logger.info("Searching for all vehicle registrations. Page: {}, Size: {}, Sort by: {}, Sort direction: {}", page, size, sortBy, sortDirection);
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<VehicleRegistrationDetailsResponse> vehicleRegistrations = vehicleRegistrationRepository.findVehicleRegistrationByIsDeletedFalse(pageable).map(VehicleRegistrationMapper.INSTANCE::toDto);
        logger.info("Found {} vehicle registrations on page {} out of {} pages. Total records: {}",
                vehicleRegistrations.getContent().size(), page, vehicleRegistrations.getTotalPages(), vehicleRegistrations.getTotalElements());

        return new VehicleRegistrationPageResponse(
                vehicleRegistrations.getContent(),
                page,
                size,
                vehicleRegistrations.getTotalPages(),
                vehicleRegistrations.getTotalElements()
        );
    }

    @Transactional
    public VehicleRegistration transferOwnerOfVehicleRegistration(Long vehicleId, String newOwnerName, String newOwnerSurname, String newOwnerLegalName,
                                                                  String newOwnerCode) {
        logger.info("Starting transfer of ownership of vehicle registration with ID: {}", vehicleId);
        VehicleRegistration vehicleRegistration = vehicleRegistrationRepository.findVehicleRegistrationByIdAndIsDeletedFalse(vehicleId)
                .orElseThrow(() -> {
                    logger.error("Vehicle registration with ID {} not found", vehicleId);
                    return new VehicleNotFoundException(String.format(VEHICLE_NOT_FOUND, vehicleId));
                });

        if ((vehicleRegistration.getOwnerName() == null ||  vehicleRegistration.getOwnerName().isEmpty())
                || (vehicleRegistration.getOwnerSurname() == null || vehicleRegistration.getOwnerSurname().isEmpty())
                || (vehicleRegistration.getOwnerCode() == null || vehicleRegistration.getOwnerCode().isEmpty())) {
            logger.error("Current owner details not found for vehicle registration ID {}", vehicleId);
            throw new IllegalStateException(CURRENT_OWNER_OF_THE_VEHICLE_NOT_FOUND);
        }
        VehicleOwnershipHistory history = VehicleOwnershipHistory.builder()
                .vehicleRegistration(vehicleRegistration)
                .ownerName(vehicleRegistration.getOwnerName())
                .ownerSurname(vehicleRegistration.getOwnerSurname())
                .ownerLegalName(vehicleRegistration.getOwnerLegalName())
                .ownerCode(vehicleRegistration.getOwnerCode())
                .transferDate(LocalDateTime.now())
                .build();
        logger.info("Creating ownership history for vehicle registration ID {}", vehicleId);

        vehicleOwnershipHistoryRepository.save(history);
        vehicleRegistration.getOwnershipHistory().add(history);
        logger.info("Ownership history saved and added to vehicle registration with ID {}", vehicleId);

        vehicleRegistration.setOwnerName(newOwnerName);
        vehicleRegistration.setOwnerSurname(newOwnerSurname);
        vehicleRegistration.setOwnerLegalName(newOwnerLegalName);
        vehicleRegistration.setOwnerCode(newOwnerCode);
        VehicleRegistration updatedVehicleRegistration = vehicleRegistrationRepository.save(vehicleRegistration);
        logger.info("Vehicle ownership transferred successfully for vehicle registration ID: {} to new owner: {} {}",
                updatedVehicleRegistration.getId(), updatedVehicleRegistration.getOwnerName(), updatedVehicleRegistration.getOwnerSurname());
        return updatedVehicleRegistration;
    }

    public VehicleRegistration deleteVehicleRegistration(Long vehicleId) {
        logger.info("Starting deletion process for vehicle registration with ID: {}", vehicleId);
        VehicleRegistration vehicleRegistration = vehicleRegistrationRepository.findVehicleRegistrationByIdAndIsDeletedFalse(vehicleId)
                .orElseThrow(() -> {
                    logger.error("Vehicle registration with ID {} not found for deletion", vehicleId);
                    return new VehicleNotFoundException(String.format(VEHICLE_NOT_FOUND, vehicleId));
                });

        logger.info("Vehicle registration with ID {} found. Marking as deleted.", vehicleId);
        vehicleRegistration.setDeleted(true);
        VehicleRegistration deletedVehicleRegistration = vehicleRegistrationRepository.save(vehicleRegistration);
        logger.info("Vehicle registration with ID {} successfully marked as deleted", deletedVehicleRegistration.getId());
        return deletedVehicleRegistration;
    }
}

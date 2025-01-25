package lt.transport.registration.service;

import lt.transport.registration.dto.TransferOwnerRequest;
import lt.transport.registration.dto.VehicleRegistrationDetailsResponse;
import lt.transport.registration.dto.VehicleRegistrationPageResponse;
import lt.transport.registration.dto.VehicleRegistrationRequest;
import lt.transport.registration.entity.VehicleOwnershipHistory;
import lt.transport.registration.entity.VehicleRegistration;
import lt.transport.registration.exception.PlateNoAlreadyExistsException;
import lt.transport.registration.exception.VehicleNotFoundException;
import lt.transport.registration.repository.VehicleOwnershipHistoryRepository;
import lt.transport.registration.repository.VehicleRegistrationRepository;
import lt.transport.registration.util.TestDataUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static lt.transport.registration.constants.ResponseMessages.CURRENT_OWNER_OF_THE_VEHICLE_NOT_FOUND;
import static lt.transport.registration.constants.ResponseMessages.PLATE_NO_ALREADY_EXISTS;
import static lt.transport.registration.constants.ResponseMessages.VEHICLE_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class VehicleRegistrationServiceTest {

    @Mock
    private VehicleRegistrationRepository vehicleRegistrationRepository;

    @Mock
    private VehicleOwnershipHistoryRepository vehicleOwnershipHistoryRepository;

    @InjectMocks
    private VehicleRegistrationService vehicleRegistrationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterVehicleForNaturalPerson() {
        VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();

        when(vehicleRegistrationRepository.save(any())).thenReturn(vehicleRegistration);
        when(vehicleRegistrationRepository.findVehicleRegistrationByPlateNoIgnoreCaseAndIsDeletedFalse(any())).thenReturn(Optional.empty());

        VehicleRegistration result = vehicleRegistrationService.saveVehicleRegistration(vehicleRegistrationRequest);

        assertNotNull(result);
        assertEquals("ABC123", result.getPlateNo());
        assertEquals("Toyota", result.getMake());
        assertEquals("Corolla", result.getModel());
        assertEquals(2020, result.getYear());
        assertEquals("Jonas", result.getOwnerName());
        assertEquals("Petrauskas", result.getOwnerSurname());
        assertNull(result.getOwnerLegalName());
        assertEquals("39601010000", result.getOwnerCode());
        assertFalse(result.isDeleted());
        verify(vehicleRegistrationRepository, times(1)).findVehicleRegistrationByPlateNoIgnoreCaseAndIsDeletedFalse(any());
        verify(vehicleRegistrationRepository, times(1)).save(any());
    }

    @Test
    void testRegisterVehicleForLegalEntity() {
        VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getLegalEntityVehicleRegistrationRequest();
        VehicleRegistration vehicleRegistration = TestDataUtil.getLegalEntityVehicleRegistration();

        when(vehicleRegistrationRepository.save(any())).thenReturn(vehicleRegistration);
        when(vehicleRegistrationRepository.findVehicleRegistrationByPlateNoIgnoreCaseAndIsDeletedFalse(any())).thenReturn(Optional.empty());

        VehicleRegistration result = vehicleRegistrationService.saveVehicleRegistration(vehicleRegistrationRequest);

        assertNotNull(result);
        assertEquals("BCD456", result.getPlateNo());
        assertEquals("Toyota", result.getMake());
        assertEquals("Corolla", result.getModel());
        assertEquals(2020, result.getYear());
        assertEquals("Jonas", result.getOwnerName());
        assertEquals("Petrauskas", result.getOwnerSurname());
        assertEquals("UAB ABC", result.getOwnerLegalName());
        assertEquals("123456789", result.getOwnerCode());
        assertFalse(result.isDeleted());
        verify(vehicleRegistrationRepository, times(1)).findVehicleRegistrationByPlateNoIgnoreCaseAndIsDeletedFalse(any());
        verify(vehicleRegistrationRepository, times(1)).save(any());
    }

    @Test
    void testRegisterVehicle_withDuplicatedPlateNo_shouldThrowException() {
        VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();

        when(vehicleRegistrationRepository.save(any())).thenReturn(vehicleRegistration);
        when(vehicleRegistrationRepository.findVehicleRegistrationByPlateNoIgnoreCaseAndIsDeletedFalse(any())).thenReturn(Optional.of(vehicleRegistration));

        PlateNoAlreadyExistsException exception = assertThrows(PlateNoAlreadyExistsException.class, () -> {
            vehicleRegistrationService.saveVehicleRegistration(vehicleRegistrationRequest);
        });
        assertEquals(PLATE_NO_ALREADY_EXISTS, exception.getMessage());
    }

    @Test
    void testRegisterVehicle_withOwnerLegalNameNull_shouldSuccess() {
        VehicleRegistrationRequest vehicleRegistrationRequest = new VehicleRegistrationRequest("ABC123",
                "Toyota", "Corolla", 2020, "Jonas", "Petrauskas",
                null, "39601010000");
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();

        when(vehicleRegistrationRepository.save(any())).thenReturn(vehicleRegistration);
        when(vehicleRegistrationRepository.findVehicleRegistrationByPlateNoIgnoreCaseAndIsDeletedFalse(any())).thenReturn(Optional.empty());

        VehicleRegistration result = vehicleRegistrationService.saveVehicleRegistration(vehicleRegistrationRequest);

        assertNotNull(result);
        assertEquals("ABC123", result.getPlateNo());
        assertEquals("Toyota", result.getMake());
        assertEquals("Corolla", result.getModel());
        assertEquals(2020, result.getYear());
        assertEquals("Jonas", result.getOwnerName());
        assertEquals("Petrauskas", result.getOwnerSurname());
        assertNull(result.getOwnerLegalName());
        assertEquals("39601010000", result.getOwnerCode());
        assertFalse(result.isDeleted());
        verify(vehicleRegistrationRepository, times(1)).findVehicleRegistrationByPlateNoIgnoreCaseAndIsDeletedFalse(any());
        verify(vehicleRegistrationRepository, times(1)).save(any());
    }

    @Test
    void testfindVehicleRegistrationByIdAndIsDeletedFalse() {
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
        when(vehicleRegistrationRepository.findVehicleRegistrationByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(vehicleRegistration));

        VehicleRegistrationDetailsResponse result = vehicleRegistrationService.findVehicleRegistrationById(1L);

        assertNotNull(result);
        assertEquals("ABC123", result.plateNo());
        assertEquals("Toyota", result.make());
        assertEquals("Corolla", result.model());
        assertEquals(2020, result.year());
        assertEquals("Jonas", result.ownerName());
        assertEquals("Petrauskas", result.ownerSurname());
        assertNull(result.ownerLegalName());
        assertEquals("39601010000", result.ownerCode());
        verify(vehicleRegistrationRepository, times(1)).findVehicleRegistrationByIdAndIsDeletedFalse(1L);
    }

    @Test
    void testFindVehicleRegistrationByIdNotFound() {
        when(vehicleRegistrationRepository.findVehicleRegistrationByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());

        VehicleNotFoundException exception = assertThrows(VehicleNotFoundException.class, () -> {
            vehicleRegistrationService.findVehicleRegistrationById(1L);
        });
        assertEquals(String.format(VEHICLE_NOT_FOUND, 1), exception.getMessage());
    }

    @Test
    void testTransferOwnerOfVehicleRegistration() {
        VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
        when(vehicleRegistrationRepository.findVehicleRegistrationByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(vehicleRegistration));

        when(vehicleRegistrationRepository.save(any())).thenReturn(vehicleRegistration);
        when(vehicleRegistrationRepository.findVehicleRegistrationByPlateNoIgnoreCaseAndIsDeletedFalse(any())).thenReturn(Optional.empty());

        VehicleRegistration savedVehicleRegistration = vehicleRegistrationService.saveVehicleRegistration(vehicleRegistrationRequest);

        TransferOwnerRequest transferOwnerRequest = TestDataUtil.getNewOwner();
        VehicleRegistration transferredVehicleRegistration = vehicleRegistrationService.transferOwnerOfVehicleRegistration(savedVehicleRegistration.getId(), transferOwnerRequest.newOwnerName(), transferOwnerRequest.newOwnerSurname(), transferOwnerRequest.newOwnerLegalName(), transferOwnerRequest.newOwnerCode());

        assertNotNull(transferredVehicleRegistration);

        List<VehicleOwnershipHistory> histories = transferredVehicleRegistration.getOwnershipHistory();
        assertEquals("ABC123", transferredVehicleRegistration.getPlateNo());
        assertEquals("Toyota", transferredVehicleRegistration.getMake());
        assertEquals("Corolla", transferredVehicleRegistration.getModel());
        assertEquals(2020, transferredVehicleRegistration.getYear());
        assertEquals("Petras", transferredVehicleRegistration.getOwnerName());
        assertEquals("Petraitis", transferredVehicleRegistration.getOwnerSurname());
        assertEquals("UAB Petras", transferredVehicleRegistration.getOwnerLegalName());
        assertEquals("39601010000", transferredVehicleRegistration.getOwnerCode());
        assertEquals(1, histories.size());
        VehicleOwnershipHistory history = histories.get(0);
        assertEquals("Jonas", history.getOwnerName());
        assertEquals("Petrauskas", history.getOwnerSurname());
        assertNull(history.getOwnerLegalName());
        assertEquals("39601010000", history.getOwnerCode());
        assertFalse(transferredVehicleRegistration.isDeleted());
        verify(vehicleOwnershipHistoryRepository, times(1)).save(any(VehicleOwnershipHistory.class));
        verify(vehicleRegistrationRepository, times(1)).findVehicleRegistrationByIdAndIsDeletedFalse(transferredVehicleRegistration.getId());
        verify(vehicleRegistrationRepository, times(1)).save(transferredVehicleRegistration);
    }

    @Test
    void testfindAllVehicleRegistrations() {
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
        Page<VehicleRegistration> vehicleRegistrationPage = new PageImpl<>(List.of(vehicleRegistration));
        when(vehicleRegistrationRepository.findVehicleRegistrationByIsDeletedFalse(any(Pageable.class))).thenReturn(vehicleRegistrationPage);

        VehicleRegistrationPageResponse result = vehicleRegistrationService.findAllVehicleRegistrations(0, 1, "plateNo", "ASC");

        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals(0, result.currentPage());
        assertEquals(1, result.pageSize());
        assertEquals(1, result.totalPages());
        assertEquals(1, result.totalElements());
        VehicleRegistrationDetailsResponse vehicleRegistrationDetailsResponse = result.content().get(0);
        assertEquals("ABC123", vehicleRegistrationDetailsResponse.plateNo());
        assertEquals("Toyota", vehicleRegistrationDetailsResponse.make());
        assertEquals("Corolla", vehicleRegistrationDetailsResponse.model());
        assertEquals(2020, vehicleRegistrationDetailsResponse.year());
        assertEquals("Jonas", vehicleRegistrationDetailsResponse.ownerName());
        assertEquals("Petrauskas", vehicleRegistrationDetailsResponse.ownerSurname());
        assertNull(vehicleRegistrationDetailsResponse.ownerLegalName());
        assertEquals("39601010000", vehicleRegistrationDetailsResponse.ownerCode());
        verify(vehicleRegistrationRepository, times(1)).findVehicleRegistrationByIsDeletedFalse(any(Pageable.class));
    }

    @Test
    void testTransferOwner_withNewOwnerOfVehicleRegistrationLegalNameNull_shouldSuccess() {
        VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
        when(vehicleRegistrationRepository.findVehicleRegistrationByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(vehicleRegistration));

        when(vehicleRegistrationRepository.save(any())).thenReturn(vehicleRegistration);
        when(vehicleRegistrationRepository.findVehicleRegistrationByPlateNoIgnoreCaseAndIsDeletedFalse(any())).thenReturn(Optional.empty());

        VehicleRegistration savedVehicleRegistration = vehicleRegistrationService.saveVehicleRegistration(vehicleRegistrationRequest);

        TransferOwnerRequest transferOwnerRequest = new TransferOwnerRequest(
                "Petras", "Petraitis", null, "39601010000");

        VehicleRegistration transferredVehicleRegistration = vehicleRegistrationService.transferOwnerOfVehicleRegistration(savedVehicleRegistration.getId(), transferOwnerRequest.newOwnerName(), transferOwnerRequest.newOwnerSurname(), transferOwnerRequest.newOwnerLegalName(), transferOwnerRequest.newOwnerCode());

        assertNotNull(transferredVehicleRegistration);
        assertEquals("ABC123", transferredVehicleRegistration.getPlateNo());
        assertEquals("Toyota", transferredVehicleRegistration.getMake());
        assertEquals("Corolla", transferredVehicleRegistration.getModel());
        assertEquals(2020, transferredVehicleRegistration.getYear());
        assertEquals("Petras", transferredVehicleRegistration.getOwnerName());
        assertEquals("Petraitis", transferredVehicleRegistration.getOwnerSurname());
        assertNull(transferredVehicleRegistration.getOwnerLegalName());
        assertEquals("39601010000", transferredVehicleRegistration.getOwnerCode());
        assertFalse(transferredVehicleRegistration.isDeleted());
        verify(vehicleOwnershipHistoryRepository, times(1)).save(any(VehicleOwnershipHistory.class));
        verify(vehicleRegistrationRepository, times(1)).findVehicleRegistrationByIdAndIsDeletedFalse(transferredVehicleRegistration.getId());
        verify(vehicleRegistrationRepository, times(1)).save(transferredVehicleRegistration);
    }

    @Test
    void testTransferOwner_OfVehicleRegistration_withNotSavedVehicleRegistration_shouldThrowException() {
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
        when(vehicleRegistrationRepository.findVehicleRegistrationByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());
        when(vehicleRegistrationRepository.save(any())).thenReturn(vehicleRegistration);
        when(vehicleRegistrationRepository.findVehicleRegistrationByPlateNoIgnoreCaseAndIsDeletedFalse(any())).thenReturn(Optional.empty());

        TransferOwnerRequest transferOwnerRequest = TestDataUtil.getNewOwner();

        VehicleNotFoundException exception = assertThrows(VehicleNotFoundException.class, () -> {
            vehicleRegistrationService.transferOwnerOfVehicleRegistration(vehicleRegistration.getId(), transferOwnerRequest.newOwnerName(), transferOwnerRequest.newOwnerSurname(), transferOwnerRequest.newOwnerLegalName(), transferOwnerRequest.newOwnerCode());
        });
        assertEquals(String.format(VEHICLE_NOT_FOUND, 1), exception.getMessage());

        verify(vehicleOwnershipHistoryRepository, times(0)).save(any(VehicleOwnershipHistory.class));
        verify(vehicleRegistrationRepository, times(1)).findVehicleRegistrationByIdAndIsDeletedFalse(vehicleRegistration.getId());
        verify(vehicleRegistrationRepository, times(0)).save(vehicleRegistration);
    }

    @Test
    void testTransferOwner_withVehicleRegistrationOwnerOfVehicleRegistrationNameNull_shouldThrowException() {
        VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
        when(vehicleRegistrationRepository.findVehicleRegistrationByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(vehicleRegistration));

        when(vehicleRegistrationRepository.save(any())).thenReturn(vehicleRegistration);
        when(vehicleRegistrationRepository.findVehicleRegistrationByPlateNoIgnoreCaseAndIsDeletedFalse(any())).thenReturn(Optional.empty());

        VehicleRegistration savedVehicleRegistration = vehicleRegistrationService.saveVehicleRegistration(vehicleRegistrationRequest);

        vehicleRegistration.setOwnerName(null);
        TransferOwnerRequest transferOwnerRequest = TestDataUtil.getNewOwner();

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            vehicleRegistrationService.transferOwnerOfVehicleRegistration(savedVehicleRegistration.getId(), transferOwnerRequest.newOwnerName(), transferOwnerRequest.newOwnerSurname(), transferOwnerRequest.newOwnerLegalName(), transferOwnerRequest.newOwnerCode());
        });
        assertEquals(CURRENT_OWNER_OF_THE_VEHICLE_NOT_FOUND, exception.getMessage());

        verify(vehicleOwnershipHistoryRepository, times(0)).save(any(VehicleOwnershipHistory.class));
        verify(vehicleRegistrationRepository, times(1)).findVehicleRegistrationByIdAndIsDeletedFalse(vehicleRegistration.getId());
        verify(vehicleRegistrationRepository, times(0)).save(vehicleRegistration);
    }

    @Test
    void testTransferOwner_withVehicleRegistrationOwnerOfVehicleRegistrationNameEmpty_shouldThrowException() {
        VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
        when(vehicleRegistrationRepository.findVehicleRegistrationByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(vehicleRegistration));

        when(vehicleRegistrationRepository.save(any())).thenReturn(vehicleRegistration);
        when(vehicleRegistrationRepository.findVehicleRegistrationByPlateNoIgnoreCaseAndIsDeletedFalse(any())).thenReturn(Optional.empty());

        VehicleRegistration savedVehicleRegistration = vehicleRegistrationService.saveVehicleRegistration(vehicleRegistrationRequest);

        vehicleRegistration.setOwnerName("");
        TransferOwnerRequest transferOwnerRequest = TestDataUtil.getNewOwner();

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            vehicleRegistrationService.transferOwnerOfVehicleRegistration(savedVehicleRegistration.getId(), transferOwnerRequest.newOwnerName(), transferOwnerRequest.newOwnerSurname(), transferOwnerRequest.newOwnerLegalName(), transferOwnerRequest.newOwnerCode());
        });
        assertEquals(CURRENT_OWNER_OF_THE_VEHICLE_NOT_FOUND, exception.getMessage());

        verify(vehicleOwnershipHistoryRepository, times(0)).save(any(VehicleOwnershipHistory.class));
        verify(vehicleRegistrationRepository, times(1)).findVehicleRegistrationByIdAndIsDeletedFalse(vehicleRegistration.getId());
        verify(vehicleRegistrationRepository, times(0)).save(vehicleRegistration);
    }

    @Test
    void testTransferOwner_withVehicleRegistrationOwnerOfVehicleRegistrationSurnameNull_shouldThrowException() {
        VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
        when(vehicleRegistrationRepository.findVehicleRegistrationByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(vehicleRegistration));

        when(vehicleRegistrationRepository.save(any())).thenReturn(vehicleRegistration);
        when(vehicleRegistrationRepository.findVehicleRegistrationByPlateNoIgnoreCaseAndIsDeletedFalse(any())).thenReturn(Optional.empty());

        VehicleRegistration savedVehicleRegistration = vehicleRegistrationService.saveVehicleRegistration(vehicleRegistrationRequest);

        vehicleRegistration.setOwnerSurname(null);
        TransferOwnerRequest transferOwnerRequest = TestDataUtil.getNewOwner();

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            vehicleRegistrationService.transferOwnerOfVehicleRegistration(savedVehicleRegistration.getId(), transferOwnerRequest.newOwnerName(), transferOwnerRequest.newOwnerSurname(), transferOwnerRequest.newOwnerLegalName(), transferOwnerRequest.newOwnerCode());
        });
        assertEquals(CURRENT_OWNER_OF_THE_VEHICLE_NOT_FOUND, exception.getMessage());

        verify(vehicleOwnershipHistoryRepository, times(0)).save(any(VehicleOwnershipHistory.class));
        verify(vehicleRegistrationRepository, times(1)).findVehicleRegistrationByIdAndIsDeletedFalse(vehicleRegistration.getId());
        verify(vehicleRegistrationRepository, times(0)).save(vehicleRegistration);
    }

    @Test
    void testTransferOwner_withVehicleRegistrationOwnerOfVehicleRegistrationSurnameEmpty_shouldThrowException() {
        VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
        when(vehicleRegistrationRepository.findVehicleRegistrationByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(vehicleRegistration));

        when(vehicleRegistrationRepository.save(any())).thenReturn(vehicleRegistration);
        when(vehicleRegistrationRepository.findVehicleRegistrationByPlateNoIgnoreCaseAndIsDeletedFalse(any())).thenReturn(Optional.empty());

        VehicleRegistration savedVehicleRegistration = vehicleRegistrationService.saveVehicleRegistration(vehicleRegistrationRequest);

        vehicleRegistration.setOwnerSurname("");
        TransferOwnerRequest transferOwnerRequest = TestDataUtil.getNewOwner();

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            vehicleRegistrationService.transferOwnerOfVehicleRegistration(savedVehicleRegistration.getId(), transferOwnerRequest.newOwnerName(), transferOwnerRequest.newOwnerSurname(), transferOwnerRequest.newOwnerLegalName(), transferOwnerRequest.newOwnerCode());
        });
        assertEquals(CURRENT_OWNER_OF_THE_VEHICLE_NOT_FOUND, exception.getMessage());

        verify(vehicleOwnershipHistoryRepository, times(0)).save(any(VehicleOwnershipHistory.class));
        verify(vehicleRegistrationRepository, times(1)).findVehicleRegistrationByIdAndIsDeletedFalse(vehicleRegistration.getId());
        verify(vehicleRegistrationRepository, times(0)).save(vehicleRegistration);
    }

    @Test
    void testTransferOwner_withVehicleRegistrationOwnerOfVehicleRegistrationLegalNameNull_shouldSuccess() {
        VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
        when(vehicleRegistrationRepository.findVehicleRegistrationByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(vehicleRegistration));

        when(vehicleRegistrationRepository.save(any())).thenReturn(vehicleRegistration);
        when(vehicleRegistrationRepository.findVehicleRegistrationByPlateNoIgnoreCaseAndIsDeletedFalse(any())).thenReturn(Optional.empty());

        VehicleRegistration savedVehicleRegistration = vehicleRegistrationService.saveVehicleRegistration(vehicleRegistrationRequest);

        TransferOwnerRequest transferOwnerRequest = TestDataUtil.getNewOwner();

        VehicleRegistration transferredVehicleRegistration = vehicleRegistrationService.transferOwnerOfVehicleRegistration(savedVehicleRegistration.getId(), transferOwnerRequest.newOwnerName(), transferOwnerRequest.newOwnerSurname(), transferOwnerRequest.newOwnerLegalName(), transferOwnerRequest.newOwnerCode());

        assertNotNull(transferredVehicleRegistration);
        assertEquals("ABC123", transferredVehicleRegistration.getPlateNo());
        assertEquals("Toyota", transferredVehicleRegistration.getMake());
        assertEquals("Corolla", transferredVehicleRegistration.getModel());
        assertEquals(2020, transferredVehicleRegistration.getYear());
        assertEquals("Petras", transferredVehicleRegistration.getOwnerName());
        assertEquals("Petraitis", transferredVehicleRegistration.getOwnerSurname());
        assertEquals("UAB Petras", transferredVehicleRegistration.getOwnerLegalName());
        assertEquals("39601010000", transferredVehicleRegistration.getOwnerCode());
        assertFalse(transferredVehicleRegistration.isDeleted());
        verify(vehicleOwnershipHistoryRepository, times(1)).save(any(VehicleOwnershipHistory.class));
        verify(vehicleRegistrationRepository, times(1)).findVehicleRegistrationByIdAndIsDeletedFalse(transferredVehicleRegistration.getId());
        verify(vehicleRegistrationRepository, times(1)).save(transferredVehicleRegistration);
    }

    @Test
    void testTransferOwner_withVehicleRegistrationOwnerOfVehicleRegistrationCodeNull_shouldThrowException() {
        VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
        when(vehicleRegistrationRepository.findVehicleRegistrationByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(vehicleRegistration));

        when(vehicleRegistrationRepository.save(any())).thenReturn(vehicleRegistration);
        when(vehicleRegistrationRepository.findVehicleRegistrationByPlateNoIgnoreCaseAndIsDeletedFalse(any())).thenReturn(Optional.empty());

        VehicleRegistration savedVehicleRegistration = vehicleRegistrationService.saveVehicleRegistration(vehicleRegistrationRequest);

        vehicleRegistration.setOwnerCode(null);
        TransferOwnerRequest transferOwnerRequest = TestDataUtil.getNewOwner();

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            vehicleRegistrationService.transferOwnerOfVehicleRegistration(savedVehicleRegistration.getId(), transferOwnerRequest.newOwnerName(), transferOwnerRequest.newOwnerSurname(), transferOwnerRequest.newOwnerLegalName(), transferOwnerRequest.newOwnerCode());
        });
        assertEquals(CURRENT_OWNER_OF_THE_VEHICLE_NOT_FOUND, exception.getMessage());

        verify(vehicleOwnershipHistoryRepository, times(0)).save(any(VehicleOwnershipHistory.class));
        verify(vehicleRegistrationRepository, times(1)).findVehicleRegistrationByIdAndIsDeletedFalse(vehicleRegistration.getId());
        verify(vehicleRegistrationRepository, times(0)).save(vehicleRegistration);
    }

    @Test
    void testTransferOwner_withVehicleRegistrationOwnerOfVehicleRegistrationCodeEmpty_shouldThrowException() {
        VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
        when(vehicleRegistrationRepository.findVehicleRegistrationByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(vehicleRegistration));

        when(vehicleRegistrationRepository.save(any())).thenReturn(vehicleRegistration);
        when(vehicleRegistrationRepository.findVehicleRegistrationByPlateNoIgnoreCaseAndIsDeletedFalse(any())).thenReturn(Optional.empty());

        VehicleRegistration savedVehicleRegistration = vehicleRegistrationService.saveVehicleRegistration(vehicleRegistrationRequest);

        vehicleRegistration.setOwnerCode("");
        TransferOwnerRequest transferOwnerRequest = TestDataUtil.getNewOwner();

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            vehicleRegistrationService.transferOwnerOfVehicleRegistration(savedVehicleRegistration.getId(), transferOwnerRequest.newOwnerName(), transferOwnerRequest.newOwnerSurname(), transferOwnerRequest.newOwnerLegalName(), transferOwnerRequest.newOwnerCode());
        });
        assertEquals(CURRENT_OWNER_OF_THE_VEHICLE_NOT_FOUND, exception.getMessage());

        verify(vehicleOwnershipHistoryRepository, times(0)).save(any(VehicleOwnershipHistory.class));
        verify(vehicleRegistrationRepository, times(1)).findVehicleRegistrationByIdAndIsDeletedFalse(vehicleRegistration.getId());
        verify(vehicleRegistrationRepository, times(0)).save(vehicleRegistration);
    }

    @Test
    void testDeleteVehicleRegistration() {
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
        when(vehicleRegistrationRepository.findVehicleRegistrationByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(vehicleRegistration));
        when(vehicleRegistrationRepository.save(vehicleRegistration)).thenReturn(vehicleRegistration);

        VehicleRegistration deletedVehicleRegistration = vehicleRegistrationService.deleteVehicleRegistration(1L);

        assertNotNull(deletedVehicleRegistration);
        assertEquals("ABC123", deletedVehicleRegistration.getPlateNo());
        assertEquals("Toyota", deletedVehicleRegistration.getMake());
        assertEquals("Corolla", deletedVehicleRegistration.getModel());
        assertEquals(2020, deletedVehicleRegistration.getYear());
        assertEquals("Jonas", deletedVehicleRegistration.getOwnerName());
        assertEquals("Petrauskas", deletedVehicleRegistration.getOwnerSurname());
        assertNull(deletedVehicleRegistration.getOwnerLegalName());
        assertEquals("39601010000", deletedVehicleRegistration.getOwnerCode());
        assertTrue(deletedVehicleRegistration.isDeleted());

        verify(vehicleRegistrationRepository, times(1)).findVehicleRegistrationByIdAndIsDeletedFalse(1L);
        verify(vehicleRegistrationRepository, times(1)).save(vehicleRegistration);
    }

    @Test
    void testDelete_VehicleRegistration_withNotSavedVehicleRegistration_shouldThrowException() {
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
        when(vehicleRegistrationRepository.findVehicleRegistrationByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());

        VehicleNotFoundException exception = assertThrows(VehicleNotFoundException.class, () -> {
            vehicleRegistrationService.deleteVehicleRegistration(1L);
        });
        assertEquals(String.format(VEHICLE_NOT_FOUND, 1), exception.getMessage());

        verify(vehicleOwnershipHistoryRepository, times(0)).save(any(VehicleOwnershipHistory.class));
        verify(vehicleRegistrationRepository, times(1)).findVehicleRegistrationByIdAndIsDeletedFalse(vehicleRegistration.getId());
        verify(vehicleRegistrationRepository, times(0)).save(vehicleRegistration);

        verify(vehicleRegistrationRepository, times(1)).findVehicleRegistrationByIdAndIsDeletedFalse(1L);
        verify(vehicleRegistrationRepository, times(0)).save(vehicleRegistration);
    }
}

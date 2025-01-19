package lt.transport.registration.service;

import lt.transport.registration.DTO.TransferOwnerRequest;
import lt.transport.registration.DTO.VehicleRegistrationDetailsResponse;
import lt.transport.registration.DTO.VehicleRegistrationPageResponse;
import lt.transport.registration.DTO.VehicleRegistrationRequest;
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

import static lt.transport.registration.constants.ResponseMessages.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class VehicleRegistrationServiceTest {

        @Mock
        private VehicleRegistrationRepository repository;

        @Mock
        private VehicleOwnershipHistoryRepository historyRepository;

        @InjectMocks
        private VehicleRegistrationService service;

        @BeforeEach
        void setUp() {
                MockitoAnnotations.openMocks(this);
        }

        @Test
        void testRegisterVehicleForNaturalPerson() {
                VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
                VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();

                when(repository.save(any())).thenReturn(vehicleRegistration);
                when(repository.findByPlateNoIgnoreCaseAndNotDeleted(any())).thenReturn(Optional.empty());

                VehicleRegistration result = service.save(vehicleRegistrationRequest);

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
                verify(repository, times(1)).findByPlateNoIgnoreCaseAndNotDeleted(any());
                verify(repository, times(1)).save(any());
        }

        @Test
        void testRegisterVehicleForLegalEntity() {
                VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getLegalEntityVehicleRegistrationRequest();
                VehicleRegistration vehicleRegistration = TestDataUtil.getLegalEntityVehicleRegistration();

                when(repository.save(any())).thenReturn(vehicleRegistration);
                when(repository.findByPlateNoIgnoreCaseAndNotDeleted(any())).thenReturn(Optional.empty());

                VehicleRegistration result = service.save(vehicleRegistrationRequest);

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
                verify(repository, times(1)).findByPlateNoIgnoreCaseAndNotDeleted(any());
                verify(repository, times(1)).save(any());
        }

        @Test
        void testRegisterVehicle_withDuplicatedPlateNo_shouldThrowException() {
                VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
                VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();

                when(repository.save(any())).thenReturn(vehicleRegistration);
                when(repository.findByPlateNoIgnoreCaseAndNotDeleted(any())).thenReturn(Optional.of(vehicleRegistration));

                PlateNoAlreadyExistsException exception = assertThrows(PlateNoAlreadyExistsException.class, () -> {
                        service.save(vehicleRegistrationRequest);
                });
                assertEquals(PLATE_NO_ALREADY_EXISTS, exception.getMessage());
        }

        @Test
        void testRegisterVehicle_withResponseBodyNull_shouldThrowException() {
                VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();

                when(repository.save(any())).thenReturn(vehicleRegistration);
                when(repository.findByPlateNoIgnoreCaseAndNotDeleted(any())).thenReturn(Optional.empty());

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                        service.save(null);
                });
                assertEquals(REQUEST_BODY_CANNOT_BE_NULL, exception.getMessage());
        }

        @Test
        void testRegisterVehicle_withPlateNoNull_shouldThrowException() {
                VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
                vehicleRegistrationRequest.setPlateNo(null);
                VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();

                when(repository.save(any())).thenReturn(vehicleRegistration);
                when(repository.findByPlateNoIgnoreCaseAndNotDeleted(any())).thenReturn(Optional.empty());

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                        service.save(vehicleRegistrationRequest);
                });
                assertEquals(PLATE_NO_IS_REQUIRED, exception.getMessage());
        }

        @Test
        void testRegisterVehicle_withPlateNoEmpty_shouldThrowException() {
                VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
                vehicleRegistrationRequest.setPlateNo("");
                VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();

                when(repository.save(any())).thenReturn(vehicleRegistration);
                when(repository.findByPlateNoIgnoreCaseAndNotDeleted(any())).thenReturn(Optional.empty());

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                        service.save(vehicleRegistrationRequest);
                });
                assertEquals(PLATE_NO_IS_REQUIRED, exception.getMessage());
        }

        @Test
        void testRegisterVehicle_withMakeNull_shouldThrowException() {
                VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
                vehicleRegistrationRequest.setMake(null);
                VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();

                when(repository.save(any())).thenReturn(vehicleRegistration);
                when(repository.findByPlateNoIgnoreCaseAndNotDeleted(any())).thenReturn(Optional.empty());

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                        service.save(vehicleRegistrationRequest);
                });
                assertEquals(MAKE_IS_REQUIRED, exception.getMessage());
        }

        @Test
        void testRegisterVehicle_withMakeEmpty_shouldThrowException() {
                VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
                vehicleRegistrationRequest.setMake("");
                VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();

                when(repository.save(any())).thenReturn(vehicleRegistration);
                when(repository.findByPlateNoIgnoreCaseAndNotDeleted(any())).thenReturn(Optional.empty());

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                        service.save(vehicleRegistrationRequest);
                });
                assertEquals(MAKE_IS_REQUIRED, exception.getMessage());
        }

        @Test
        void testRegisterVehicle_withModelNull_shouldThrowException() {
                VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
                vehicleRegistrationRequest.setModel(null);
                VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();

                when(repository.save(any())).thenReturn(vehicleRegistration);
                when(repository.findByPlateNoIgnoreCaseAndNotDeleted(any())).thenReturn(Optional.empty());

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                        service.save(vehicleRegistrationRequest);
                });
                assertEquals(MODEL_IS_REQUIRED, exception.getMessage());
        }

        @Test
        void testRegisterVehicle_withModelEmpty_shouldThrowException() {
                VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
                vehicleRegistrationRequest.setModel("");
                VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();

                when(repository.save(any())).thenReturn(vehicleRegistration);
                when(repository.findByPlateNoIgnoreCaseAndNotDeleted(any())).thenReturn(Optional.empty());

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                        service.save(vehicleRegistrationRequest);
                });
                assertEquals(MODEL_IS_REQUIRED, exception.getMessage());
        }

        @Test
        void testRegisterVehicle_withYearNull_shouldThrowException() {
                VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
                vehicleRegistrationRequest.setYear(null);
                VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();

                when(repository.save(any())).thenReturn(vehicleRegistration);
                when(repository.findByPlateNoIgnoreCaseAndNotDeleted(any())).thenReturn(Optional.empty());

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                        service.save(vehicleRegistrationRequest);
                });
                assertEquals(YEAR_IS_REQUIRED, exception.getMessage());
        }

        @Test
        void testRegisterVehicle_withOwnerNameNull_shouldThrowException() {
                VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
                vehicleRegistrationRequest.setOwnerName(null);
                VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();

                when(repository.save(any())).thenReturn(vehicleRegistration);
                when(repository.findByPlateNoIgnoreCaseAndNotDeleted(any())).thenReturn(Optional.empty());

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                        service.save(vehicleRegistrationRequest);
                });
                assertEquals(OWNER_NAME_IS_REQUIRED, exception.getMessage());
        }

        @Test
        void testRegisterVehicle_withOwnerNameEmpty_shouldThrowException() {
                VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
                vehicleRegistrationRequest.setOwnerName("");
                VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();

                when(repository.save(any())).thenReturn(vehicleRegistration);
                when(repository.findByPlateNoIgnoreCaseAndNotDeleted(any())).thenReturn(Optional.empty());

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                        service.save(vehicleRegistrationRequest);
                });
                assertEquals(OWNER_NAME_IS_REQUIRED, exception.getMessage());
        }

        @Test
        void testRegisterVehicle_withOwnerSurnameNull_shouldThrowException() {
                VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
                vehicleRegistrationRequest.setOwnerSurname(null);
                VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();

                when(repository.save(any())).thenReturn(vehicleRegistration);
                when(repository.findByPlateNoIgnoreCaseAndNotDeleted(any())).thenReturn(Optional.empty());

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                        service.save(vehicleRegistrationRequest);
                });
                assertEquals(OWNER_SURNAME_IS_REQUIRED, exception.getMessage());
        }

        @Test
        void testRegisterVehicle_withOwnerSurnameEmpty_shouldThrowException() {
                VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
                vehicleRegistrationRequest.setOwnerSurname("");
                VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();

                when(repository.save(any())).thenReturn(vehicleRegistration);
                when(repository.findByPlateNoIgnoreCaseAndNotDeleted(any())).thenReturn(Optional.empty());

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                        service.save(vehicleRegistrationRequest);
                });
                assertEquals(OWNER_SURNAME_IS_REQUIRED, exception.getMessage());
        }

        @Test
        void testRegisterVehicle_withOwnerLegalNameNull_shouldSuccess() {
                VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
                vehicleRegistrationRequest.setOwnerLegalName(null);
                VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();

                when(repository.save(any())).thenReturn(vehicleRegistration);
                when(repository.findByPlateNoIgnoreCaseAndNotDeleted(any())).thenReturn(Optional.empty());

                VehicleRegistration result = service.save(vehicleRegistrationRequest);

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
                verify(repository, times(1)).findByPlateNoIgnoreCaseAndNotDeleted(any());
                verify(repository, times(1)).save(any());
        }

        @Test
        void testRegisterVehicle_withOwnerCodeNull_shouldThrowException() {
                VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
                vehicleRegistrationRequest.setOwnerCode(null);
                VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();

                when(repository.save(any())).thenReturn(vehicleRegistration);
                when(repository.findByPlateNoIgnoreCaseAndNotDeleted(any())).thenReturn(Optional.of(vehicleRegistration));

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                        service.save(vehicleRegistrationRequest);
                });
                assertEquals(OWNER_CODE_IS_REQUIRED, exception.getMessage());
        }

        @Test
        void testRegisterVehicle_withOwnerCodeEmpty_shouldThrowException() {
                VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
                vehicleRegistrationRequest.setOwnerCode("");
                VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();

                when(repository.save(any())).thenReturn(vehicleRegistration);
                when(repository.findByPlateNoIgnoreCaseAndNotDeleted(any())).thenReturn(Optional.of(vehicleRegistration));

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                        service.save(vehicleRegistrationRequest);
                });
                assertEquals(OWNER_CODE_IS_REQUIRED, exception.getMessage());
        }

        @Test
        void testfindByIdAndIsDeletedFalse() {
                VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
                when(repository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(vehicleRegistration));

                VehicleRegistrationDetailsResponse result = service.findById(1L);

                assertNotNull(result);
                assertEquals("ABC123", result.getPlateNo());
                assertEquals("Toyota", result.getMake());
                assertEquals("Corolla", result.getModel());
                assertEquals(2020, result.getYear());
                assertEquals("Jonas", result.getOwnerName());
                assertEquals("Petrauskas", result.getOwnerSurname());
                assertNull(result.getOwnerLegalName());
                assertEquals("39601010000", result.getOwnerCode());
                verify(repository, times(1)).findByIdAndIsDeletedFalse(1L);
        }

        @Test
        void testFindByIdNotFound() {
                when(repository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());

                VehicleNotFoundException exception = assertThrows(VehicleNotFoundException.class, () -> {
                        service.findById(1L);
                });
                assertEquals(String.format(VEHICLE_NOT_FOUND, 1), exception.getMessage());
        }

        @Test
        void testTransferOwner() {
                VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
                VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
                when(repository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(vehicleRegistration));

                when(repository.save(any())).thenReturn(vehicleRegistration);
                when(repository.findByPlateNoIgnoreCaseAndNotDeleted(any())).thenReturn(Optional.empty());

                VehicleRegistration savedVehicleRegistration = service.save(vehicleRegistrationRequest);

                TransferOwnerRequest transferOwnerRequest = TestDataUtil.getNewOwner();
                VehicleRegistration transferredVehicleRegistration = service.transferOwner(savedVehicleRegistration.getId(), transferOwnerRequest.getNewOwnerName(), transferOwnerRequest.getNewOwnerSurname(),
                        transferOwnerRequest.getNewOwnerLegalName(), transferOwnerRequest.getNewOwnerCode());

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
                verify(historyRepository, times(1)).save(any(VehicleOwnershipHistory.class));
                verify(repository, times(1)).findByIdAndIsDeletedFalse(transferredVehicleRegistration.getId());
                verify(repository, times(1)).save(transferredVehicleRegistration);
        }

        @Test
        void testfindAll() {
                VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
                Page<VehicleRegistration> vehicleRegistrationPage = new PageImpl<>(List.of(vehicleRegistration));
                when(repository.findAllNotDeleted(any(Pageable.class))).thenReturn(vehicleRegistrationPage);

                VehicleRegistrationPageResponse result = service.findAll(0, 1, "plateNo", "ASC");

                assertNotNull(result);
                assertEquals(1, result.getContent().size());
                assertEquals(0, result.getCurrentPage());
                assertEquals(1, result.getPageSize());
                assertEquals(1, result.getTotalPages());
                assertEquals(1, result.getTotalElements());
                VehicleRegistrationDetailsResponse vehicleRegistrationDetailsResponse = result.getContent().get(0);
                assertEquals("ABC123", vehicleRegistrationDetailsResponse.getPlateNo());
                assertEquals("Toyota", vehicleRegistrationDetailsResponse.getMake());
                assertEquals("Corolla", vehicleRegistrationDetailsResponse.getModel());
                assertEquals(2020, vehicleRegistrationDetailsResponse.getYear());
                assertEquals("Jonas", vehicleRegistrationDetailsResponse.getOwnerName());
                assertEquals("Petrauskas", vehicleRegistrationDetailsResponse.getOwnerSurname());
                assertNull(vehicleRegistrationDetailsResponse.getOwnerLegalName());
                assertEquals("39601010000", vehicleRegistrationDetailsResponse.getOwnerCode());
                verify(repository, times(1)).findAllNotDeleted(any(Pageable.class));
        }

        @Test
        void testTransferOwner_withNewOwnerNameNull_shouldThrowException() {
                VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
                VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
                when(repository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(vehicleRegistration));

                when(repository.save(any())).thenReturn(vehicleRegistration);
                when(repository.findByPlateNoIgnoreCaseAndNotDeleted(any())).thenReturn(Optional.empty());

                VehicleRegistration savedVehicleRegistration = service.save(vehicleRegistrationRequest);

                TransferOwnerRequest transferOwnerRequest = TestDataUtil.getNewOwner();
                transferOwnerRequest.setNewOwnerName(null);

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                        service.transferOwner(savedVehicleRegistration.getId(), transferOwnerRequest.getNewOwnerName(), transferOwnerRequest.getNewOwnerSurname(),
                                transferOwnerRequest.getNewOwnerLegalName(), transferOwnerRequest.getNewOwnerCode());
                });
                assertEquals(OWNER_NAME_IS_REQUIRED, exception.getMessage());

                verify(historyRepository, times(0)).save(any(VehicleOwnershipHistory.class));
                verify(repository, times(0)).findByIdAndIsDeletedFalse(vehicleRegistration.getId());
                verify(repository, times(0)).save(vehicleRegistration);
        }

        @Test
        void testTransferOwner_withNewOwnerNameEmpty_shouldThrowException() {
                VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
                VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
                when(repository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(vehicleRegistration));

                when(repository.save(any())).thenReturn(vehicleRegistration);
                when(repository.findByPlateNoIgnoreCaseAndNotDeleted(any())).thenReturn(Optional.empty());

                VehicleRegistration savedVehicleRegistration = service.save(vehicleRegistrationRequest);

                TransferOwnerRequest transferOwnerRequest = TestDataUtil.getNewOwner();
                transferOwnerRequest.setNewOwnerName("");

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                        service.transferOwner(savedVehicleRegistration.getId(), transferOwnerRequest.getNewOwnerName(), transferOwnerRequest.getNewOwnerSurname(),
                                transferOwnerRequest.getNewOwnerLegalName(), transferOwnerRequest.getNewOwnerCode());
                });
                assertEquals(OWNER_NAME_IS_REQUIRED, exception.getMessage());

                verify(historyRepository, times(0)).save(any(VehicleOwnershipHistory.class));
                verify(repository, times(0)).findByIdAndIsDeletedFalse(vehicleRegistration.getId());
                verify(repository, times(0)).save(vehicleRegistration);
        }

        @Test
        void testTransferOwner_withNewOwnerSurnameNull_shouldThrowException() {
                VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
                VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
                when(repository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(vehicleRegistration));

                when(repository.save(any())).thenReturn(vehicleRegistration);
                when(repository.findByPlateNoIgnoreCaseAndNotDeleted(any())).thenReturn(Optional.empty());

                VehicleRegistration savedVehicleRegistration = service.save(vehicleRegistrationRequest);

                TransferOwnerRequest transferOwnerRequest = TestDataUtil.getNewOwner();
                transferOwnerRequest.setNewOwnerSurname(null);

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                        service.transferOwner(savedVehicleRegistration.getId(), transferOwnerRequest.getNewOwnerName(), transferOwnerRequest.getNewOwnerSurname(),
                                transferOwnerRequest.getNewOwnerLegalName(), transferOwnerRequest.getNewOwnerCode());
                });
                assertEquals(OWNER_SURNAME_IS_REQUIRED, exception.getMessage());

                verify(historyRepository, times(0)).save(any(VehicleOwnershipHistory.class));
                verify(repository, times(0)).findByIdAndIsDeletedFalse(vehicleRegistration.getId());
                verify(repository, times(0)).save(vehicleRegistration);
        }

        @Test
        void testTransferOwner_withNewOwnerSurnameEmpty_shouldThrowException() {
                VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
                VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
                when(repository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(vehicleRegistration));

                when(repository.save(any())).thenReturn(vehicleRegistration);
                when(repository.findByPlateNoIgnoreCaseAndNotDeleted(any())).thenReturn(Optional.empty());

                VehicleRegistration savedVehicleRegistration = service.save(vehicleRegistrationRequest);

                TransferOwnerRequest transferOwnerRequest = TestDataUtil.getNewOwner();
                transferOwnerRequest.setNewOwnerSurname("");

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                        service.transferOwner(savedVehicleRegistration.getId(), transferOwnerRequest.getNewOwnerName(), transferOwnerRequest.getNewOwnerSurname(),
                                transferOwnerRequest.getNewOwnerLegalName(), transferOwnerRequest.getNewOwnerCode());
                });
                assertEquals(OWNER_SURNAME_IS_REQUIRED, exception.getMessage());

                verify(historyRepository, times(0)).save(any(VehicleOwnershipHistory.class));
                verify(repository, times(0)).findByIdAndIsDeletedFalse(vehicleRegistration.getId());
                verify(repository, times(0)).save(vehicleRegistration);
        }

        @Test
        void testTransferOwner_withNewOwnerLegalNameNull_shouldSuccess() {
                VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
                VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
                when(repository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(vehicleRegistration));

                when(repository.save(any())).thenReturn(vehicleRegistration);
                when(repository.findByPlateNoIgnoreCaseAndNotDeleted(any())).thenReturn(Optional.empty());

                VehicleRegistration savedVehicleRegistration = service.save(vehicleRegistrationRequest);

                TransferOwnerRequest transferOwnerRequest = TestDataUtil.getNewOwner();
                transferOwnerRequest.setNewOwnerLegalName(null);

                VehicleRegistration transferredVehicleRegistration = service.transferOwner(savedVehicleRegistration.getId(), transferOwnerRequest.getNewOwnerName(), transferOwnerRequest.getNewOwnerSurname(),
                        transferOwnerRequest.getNewOwnerLegalName(), transferOwnerRequest.getNewOwnerCode());

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
                verify(historyRepository, times(1)).save(any(VehicleOwnershipHistory.class));
                verify(repository, times(1)).findByIdAndIsDeletedFalse(transferredVehicleRegistration.getId());
                verify(repository, times(1)).save(transferredVehicleRegistration);
        }

        @Test
        void testTransferOwner_withNewOwneCodeNull_shouldThrowException() {
                VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
                VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
                when(repository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(vehicleRegistration));

                when(repository.save(any())).thenReturn(vehicleRegistration);
                when(repository.findByPlateNoIgnoreCaseAndNotDeleted(any())).thenReturn(Optional.empty());

                VehicleRegistration savedVehicleRegistration = service.save(vehicleRegistrationRequest);

                TransferOwnerRequest transferOwnerRequest = TestDataUtil.getNewOwner();
                transferOwnerRequest.setNewOwnerCode(null);

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                        service.transferOwner(savedVehicleRegistration.getId(), transferOwnerRequest.getNewOwnerName(), transferOwnerRequest.getNewOwnerSurname(),
                                transferOwnerRequest.getNewOwnerLegalName(), transferOwnerRequest.getNewOwnerCode());
                });
                assertEquals(OWNER_CODE_IS_REQUIRED, exception.getMessage());

                verify(historyRepository, times(0)).save(any(VehicleOwnershipHistory.class));
                verify(repository, times(0)).findByIdAndIsDeletedFalse(vehicleRegistration.getId());
                verify(repository, times(0)).save(vehicleRegistration);
        }

        @Test
        void testTransferOwner_withNewOwnerCodeEmpty_shouldThrowException() {
                VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
                VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
                when(repository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(vehicleRegistration));

                when(repository.save(any())).thenReturn(vehicleRegistration);
                when(repository.findByPlateNoIgnoreCaseAndNotDeleted(any())).thenReturn(Optional.empty());

                VehicleRegistration savedVehicleRegistration = service.save(vehicleRegistrationRequest);

                TransferOwnerRequest transferOwnerRequest = TestDataUtil.getNewOwner();
                transferOwnerRequest.setNewOwnerCode("");

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                        service.transferOwner(savedVehicleRegistration.getId(), transferOwnerRequest.getNewOwnerName(), transferOwnerRequest.getNewOwnerSurname(),
                                transferOwnerRequest.getNewOwnerLegalName(), transferOwnerRequest.getNewOwnerCode());
                });
                assertEquals(OWNER_CODE_IS_REQUIRED, exception.getMessage());

                verify(historyRepository, times(0)).save(any(VehicleOwnershipHistory.class));
                verify(repository, times(0)).findByIdAndIsDeletedFalse(vehicleRegistration.getId());
                verify(repository, times(0)).save(vehicleRegistration);
        }

        @Test
        void testTransferOwner_withNotSavedVehicleRegistration_shouldThrowException() {
                VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
                when(repository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());
                when(repository.save(any())).thenReturn(vehicleRegistration);
                when(repository.findByPlateNoIgnoreCaseAndNotDeleted(any())).thenReturn(Optional.empty());

                TransferOwnerRequest transferOwnerRequest = TestDataUtil.getNewOwner();

                VehicleNotFoundException exception = assertThrows(VehicleNotFoundException.class, () -> {
                        service.transferOwner(vehicleRegistration.getId(), transferOwnerRequest.getNewOwnerName(), transferOwnerRequest.getNewOwnerSurname(),
                                transferOwnerRequest.getNewOwnerLegalName(), transferOwnerRequest.getNewOwnerCode());
                });
                assertEquals(String.format(VEHICLE_NOT_FOUND, 1), exception.getMessage());

                verify(historyRepository, times(0)).save(any(VehicleOwnershipHistory.class));
                verify(repository, times(1)).findByIdAndIsDeletedFalse(vehicleRegistration.getId());
                verify(repository, times(0)).save(vehicleRegistration);
        }

        @Test
        void testTransferOwner_withVehicleRegistrationOwnerNameNull_shouldThrowException() {
                VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
                VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
                when(repository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(vehicleRegistration));

                when(repository.save(any())).thenReturn(vehicleRegistration);
                when(repository.findByPlateNoIgnoreCaseAndNotDeleted(any())).thenReturn(Optional.empty());

                VehicleRegistration savedVehicleRegistration = service.save(vehicleRegistrationRequest);

                vehicleRegistration.setOwnerName(null);
                TransferOwnerRequest transferOwnerRequest = TestDataUtil.getNewOwner();

                IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                        service.transferOwner(savedVehicleRegistration.getId(), transferOwnerRequest.getNewOwnerName(), transferOwnerRequest.getNewOwnerSurname(),
                                transferOwnerRequest.getNewOwnerLegalName(), transferOwnerRequest.getNewOwnerCode());
                });
                assertEquals(CURRENT_OWNER_OF_THE_VEHICLE_NOT_FOUND, exception.getMessage());

                verify(historyRepository, times(0)).save(any(VehicleOwnershipHistory.class));
                verify(repository, times(1)).findByIdAndIsDeletedFalse(vehicleRegistration.getId());
                verify(repository, times(0)).save(vehicleRegistration);
        }

        @Test
        void testTransferOwner_withVehicleRegistrationOwnerNameEmpty_shouldThrowException() {
                VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
                VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
                when(repository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(vehicleRegistration));

                when(repository.save(any())).thenReturn(vehicleRegistration);
                when(repository.findByPlateNoIgnoreCaseAndNotDeleted(any())).thenReturn(Optional.empty());

                VehicleRegistration savedVehicleRegistration = service.save(vehicleRegistrationRequest);

                vehicleRegistration.setOwnerName("");
                TransferOwnerRequest transferOwnerRequest = TestDataUtil.getNewOwner();

                IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                        service.transferOwner(savedVehicleRegistration.getId(), transferOwnerRequest.getNewOwnerName(), transferOwnerRequest.getNewOwnerSurname(),
                                transferOwnerRequest.getNewOwnerLegalName(), transferOwnerRequest.getNewOwnerCode());
                });
                assertEquals(CURRENT_OWNER_OF_THE_VEHICLE_NOT_FOUND, exception.getMessage());

                verify(historyRepository, times(0)).save(any(VehicleOwnershipHistory.class));
                verify(repository, times(1)).findByIdAndIsDeletedFalse(vehicleRegistration.getId());
                verify(repository, times(0)).save(vehicleRegistration);
        }

        @Test
        void testTransferOwner_withVehicleRegistrationOwnerSurnameNull_shouldThrowException() {
                VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
                VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
                when(repository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(vehicleRegistration));

                when(repository.save(any())).thenReturn(vehicleRegistration);
                when(repository.findByPlateNoIgnoreCaseAndNotDeleted(any())).thenReturn(Optional.empty());

                VehicleRegistration savedVehicleRegistration = service.save(vehicleRegistrationRequest);

                vehicleRegistration.setOwnerSurname(null);
                TransferOwnerRequest transferOwnerRequest = TestDataUtil.getNewOwner();

                IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                        service.transferOwner(savedVehicleRegistration.getId(), transferOwnerRequest.getNewOwnerName(), transferOwnerRequest.getNewOwnerSurname(),
                                transferOwnerRequest.getNewOwnerLegalName(), transferOwnerRequest.getNewOwnerCode());
                });
                assertEquals(CURRENT_OWNER_OF_THE_VEHICLE_NOT_FOUND, exception.getMessage());

                verify(historyRepository, times(0)).save(any(VehicleOwnershipHistory.class));
                verify(repository, times(1)).findByIdAndIsDeletedFalse(vehicleRegistration.getId());
                verify(repository, times(0)).save(vehicleRegistration);
        }

        @Test
        void testTransferOwner_withVehicleRegistrationOwnerSurnameEmpty_shouldThrowException() {
                VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
                VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
                when(repository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(vehicleRegistration));

                when(repository.save(any())).thenReturn(vehicleRegistration);
                when(repository.findByPlateNoIgnoreCaseAndNotDeleted(any())).thenReturn(Optional.empty());

                VehicleRegistration savedVehicleRegistration = service.save(vehicleRegistrationRequest);

                vehicleRegistration.setOwnerSurname("");
                TransferOwnerRequest transferOwnerRequest = TestDataUtil.getNewOwner();

                IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                        service.transferOwner(savedVehicleRegistration.getId(), transferOwnerRequest.getNewOwnerName(), transferOwnerRequest.getNewOwnerSurname(),
                                transferOwnerRequest.getNewOwnerLegalName(), transferOwnerRequest.getNewOwnerCode());
                });
                assertEquals(CURRENT_OWNER_OF_THE_VEHICLE_NOT_FOUND, exception.getMessage());

                verify(historyRepository, times(0)).save(any(VehicleOwnershipHistory.class));
                verify(repository, times(1)).findByIdAndIsDeletedFalse(vehicleRegistration.getId());
                verify(repository, times(0)).save(vehicleRegistration);
        }

        @Test
        void testTransferOwner_withVehicleRegistrationOwnerLegalNameNull_shouldSuccess() {
                VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
                VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
                when(repository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(vehicleRegistration));

                when(repository.save(any())).thenReturn(vehicleRegistration);
                when(repository.findByPlateNoIgnoreCaseAndNotDeleted(any())).thenReturn(Optional.empty());

                VehicleRegistration savedVehicleRegistration = service.save(vehicleRegistrationRequest);

                TransferOwnerRequest transferOwnerRequest = TestDataUtil.getNewOwner();

                VehicleRegistration transferredVehicleRegistration = service.transferOwner(savedVehicleRegistration.getId(), transferOwnerRequest.getNewOwnerName(), transferOwnerRequest.getNewOwnerSurname(),
                        transferOwnerRequest.getNewOwnerLegalName(), transferOwnerRequest.getNewOwnerCode());

//                assertNotNull(transferredVehicleRegistration);
//                assertEquals("ABC123", transferredVehicleRegistration.getPlateNo());
//                assertEquals("Toyota", transferredVehicleRegistration.getMake());
//                assertEquals("Corolla", transferredVehicleRegistration.getModel());
//                assertEquals(2020, transferredVehicleRegistration.getYear());
//                assertEquals("Petras", transferredVehicleRegistration.getOwnerName());
//                assertEquals("Petraitis", transferredVehicleRegistration.getOwnerSurname());
//                assertEquals("UAB Petras", transferredVehicleRegistration.getOwnerLegalName());
//                assertEquals("39601010000", transferredVehicleRegistration.getOwnerCode());
//                assertFalse(transferredVehicleRegistration.isDeleted());
//                verify(historyRepository, times(1)).save(any(VehicleOwnershipHistory.class));
//                verify(repository, times(1)).findByIdAndIsDeletedFalse(transferredVehicleRegistration.getId());
//                verify(repository, times(1)).save(transferredVehicleRegistration);
        }

        @Test
        void testTransferOwner_withVehicleRegistrationOwnerCodeNull_shouldThrowException() {
                VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
                VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
                when(repository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(vehicleRegistration));

                when(repository.save(any())).thenReturn(vehicleRegistration);
                when(repository.findByPlateNoIgnoreCaseAndNotDeleted(any())).thenReturn(Optional.empty());

                VehicleRegistration savedVehicleRegistration = service.save(vehicleRegistrationRequest);

                vehicleRegistration.setOwnerCode(null);
                TransferOwnerRequest transferOwnerRequest = TestDataUtil.getNewOwner();

                IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                        service.transferOwner(savedVehicleRegistration.getId(), transferOwnerRequest.getNewOwnerName(), transferOwnerRequest.getNewOwnerSurname(),
                                transferOwnerRequest.getNewOwnerLegalName(), transferOwnerRequest.getNewOwnerCode());
                });
                assertEquals(CURRENT_OWNER_OF_THE_VEHICLE_NOT_FOUND, exception.getMessage());

                verify(historyRepository, times(0)).save(any(VehicleOwnershipHistory.class));
                verify(repository, times(1)).findByIdAndIsDeletedFalse(vehicleRegistration.getId());
                verify(repository, times(0)).save(vehicleRegistration);
        }

        @Test
        void testTransferOwner_withVehicleRegistrationOwnerCodeEmpty_shouldThrowException() {
                VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
                VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
                when(repository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(vehicleRegistration));

                when(repository.save(any())).thenReturn(vehicleRegistration);
                when(repository.findByPlateNoIgnoreCaseAndNotDeleted(any())).thenReturn(Optional.empty());

                VehicleRegistration savedVehicleRegistration = service.save(vehicleRegistrationRequest);

                vehicleRegistration.setOwnerCode("");
                TransferOwnerRequest transferOwnerRequest = TestDataUtil.getNewOwner();

                IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                        service.transferOwner(savedVehicleRegistration.getId(), transferOwnerRequest.getNewOwnerName(), transferOwnerRequest.getNewOwnerSurname(),
                                transferOwnerRequest.getNewOwnerLegalName(), transferOwnerRequest.getNewOwnerCode());
                });
                assertEquals(CURRENT_OWNER_OF_THE_VEHICLE_NOT_FOUND, exception.getMessage());

                verify(historyRepository, times(0)).save(any(VehicleOwnershipHistory.class));
                verify(repository, times(1)).findByIdAndIsDeletedFalse(vehicleRegistration.getId());
                verify(repository, times(0)).save(vehicleRegistration);
        }

        @Test
        void testDelete() {
                VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
                when(repository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(vehicleRegistration));
                when(repository.save(vehicleRegistration)).thenReturn(vehicleRegistration);

                VehicleRegistration deletedVehicleRegistration = service.delete(1L);

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

                verify(repository, times(1)).findByIdAndIsDeletedFalse(1L);
                verify(repository, times(1)).save(vehicleRegistration);
        }

        @Test
        void testDelete_withNotSavedVehicleRegistration_shouldThrowException() {
                VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
                when(repository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());

                VehicleNotFoundException exception = assertThrows(VehicleNotFoundException.class, () -> {
                        service.delete(1L);
                });
                assertEquals(String.format(VEHICLE_NOT_FOUND, 1), exception.getMessage());

                verify(historyRepository, times(0)).save(any(VehicleOwnershipHistory.class));
                verify(repository, times(1)).findByIdAndIsDeletedFalse(vehicleRegistration.getId());
                verify(repository, times(0)).save(vehicleRegistration);

                verify(repository, times(1)).findByIdAndIsDeletedFalse(1L);
                verify(repository, times(0)).save(vehicleRegistration);
        }
}

package lt.transport.registration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lt.transport.registration.DTO.TransferOwnerRequest;
import lt.transport.registration.DTO.VehicleRegistrationPageResponse;
import lt.transport.registration.DTO.VehicleRegistrationRequest;
import lt.transport.registration.entity.VehicleRegistration;
import lt.transport.registration.exception.PlateNoAlreadyExistsException;
import lt.transport.registration.exception.VehicleNotFoundException;
import lt.transport.registration.mapper.VehicleRegistrationMapper;
import lt.transport.registration.service.VehicleRegistrationService;
import lt.transport.registration.util.TestDataUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static lt.transport.registration.constants.ResponseMessages.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebMvcTest(VehicleRegistrationController.class)
public class VehicleRegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VehicleRegistrationService service;

    private ObjectMapper objectMapper;

    @BeforeAll
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testRegisterVehicleForNaturalPerson() throws Exception {
        VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
        lenient().when(service.save(any(VehicleRegistrationRequest.class)))
                .thenReturn(vehicleRegistration);

        mockMvc.perform(post("/vehicles/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(vehicleRegistrationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(VEHICLE_REGISTERED_SUCCESSFULLY))
                .andExpect(jsonPath("$.vehicleId").value("1"));

        verify(service).save(any());
    }

    @Test
    void testRegisterVehicleForLegalEntity() throws Exception {
        VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getLegalEntityVehicleRegistrationRequest();
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
        lenient().when(service.save(any(VehicleRegistrationRequest.class)))
                .thenReturn(vehicleRegistration);

        mockMvc.perform(post("/vehicles/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(vehicleRegistrationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(VEHICLE_REGISTERED_SUCCESSFULLY))
                .andExpect(jsonPath("$.vehicleId").value("1"));

        verify(service).save(any());
    }

    @Test
    void testRegisterVehicle_withDuplicatedPlateNo_shouldReturnBadRequest() throws Exception {
        VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
        lenient().when(service.save(any(VehicleRegistrationRequest.class)))
                .thenThrow(new PlateNoAlreadyExistsException(PLATE_NO_ALREADY_EXISTS));

        mockMvc.perform(post("/vehicles/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(vehicleRegistrationRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(PLATE_NO_ALREADY_EXISTS));

        verify(service).save(any());
    }

    @Test
    void testRegisterVehicle_withInvalidInput_shouldReturnBadRequest() throws Exception {
        VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
        lenient().when(service.save(any(VehicleRegistrationRequest.class)))
                .thenThrow(new IllegalArgumentException("message"));

        mockMvc.perform(post("/vehicles/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(vehicleRegistrationRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("message"));

        verify(service).save(any());
    }

    @Test
    void testGetById() throws Exception {
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
        when(service.findById(anyLong())).thenReturn(VehicleRegistrationMapper.toDto(vehicleRegistration));

        mockMvc.perform(get("/vehicles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicleId").value("1"))
                .andExpect(jsonPath("$.plateNo").value("ABC123"))
                .andExpect(jsonPath("$.make").value("Toyota"))
                .andExpect(jsonPath("$.model").value("Corolla"))
                .andExpect(jsonPath("$.year").value("2020"))
                .andExpect(jsonPath("$.ownerName").value("Jonas"))
                .andExpect(jsonPath("$.ownerSurname").value("Petrauskas"))
                .andExpect(jsonPath("$.ownerLegalName").isEmpty())
                .andExpect(jsonPath("$.ownerCode").value("39601010000"));

        verify(service).findById(anyLong());
    }

    @Test
    void testGetByIdNotFound() throws Exception {
        when(service.findById(anyLong())).thenThrow(new VehicleNotFoundException(String.format(VEHICLE_NOT_FOUND, 1)));

        mockMvc.perform(get("/vehicles/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value(String.format(VEHICLE_NOT_FOUND, 1)));

        verify(service).findById(anyLong());
    }

    @Test
    void testGetAll() throws Exception {
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
        when(service.findAll(0, 1, "plateNo", "ASC")).thenReturn(new VehicleRegistrationPageResponse(
                List.of(VehicleRegistrationMapper.toDto(vehicleRegistration)),
                0,
                1,
                1,
                1
        ));

        mockMvc.perform(get("/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                                .param("page", "0")
                                .param("size", "1")
                                .param("sortBy", "plateNo")
                                .param("sortDirection", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].vehicleId").value("1"))
                .andExpect(jsonPath("$.content[0].plateNo").value("ABC123"))
                .andExpect(jsonPath("$.content[0].make").value("Toyota"))
                .andExpect(jsonPath("$.content[0].model").value("Corolla"))
                .andExpect(jsonPath("$.content[0].year").value(2020))
                .andExpect(jsonPath("$.content[0].ownerName").value("Jonas"))
                .andExpect(jsonPath("$.content[0].ownerSurname").value("Petrauskas"))
                .andExpect(jsonPath("$.content[0].ownerLegalName").isEmpty())
                .andExpect(jsonPath("$.content[0].ownerCode").value("39601010000"))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.pageSize").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(service).findAll(anyInt(), anyInt(), any(), any());
    }

    @Test
    public void testTransferOwner() throws Exception {
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
        TransferOwnerRequest transferOwnerRequest = TestDataUtil.getNewOwner();
        when(service.transferOwner(anyLong(), any(), any(), any(), any())).thenReturn(vehicleRegistration);

        mockMvc.perform(post("/vehicles/{vehicleId}/transfer-owner", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(transferOwnerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(VEHICLE_OWNER_TRANSFERRED))
                .andExpect(jsonPath("$.vehicleId").value("1"));

        verify(service, times(1)).transferOwner(anyLong(), any(), any(), any(), any());
    }

    @Test
    public void testTransferOwner_withInvalidInput_ShouldReturnBadRequest() throws Exception {
        TransferOwnerRequest transferOwnerRequest = TestDataUtil.getNewOwner();
        when(service.transferOwner(anyLong(), any(), any(), any(), any()))
                .thenThrow(new IllegalArgumentException("message"));

        mockMvc.perform(post("/vehicles/{vehicleId}/transfer-owner", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(transferOwnerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("message"));

        verify(service, times(1)).transferOwner(anyLong(), any(), any(), any(), any());
    }

    @Test
    public void testTransferOwner_withInvalidState_ShouldReturnNotFound() throws Exception {
        TransferOwnerRequest transferOwnerRequest = TestDataUtil.getNewOwner();
        when(service.transferOwner(anyLong(), any(), any(), any(), any()))
                .thenThrow(new IllegalStateException("message"));

        mockMvc.perform(post("/vehicles/{vehicleId}/transfer-owner", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(transferOwnerRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("message"));

        verify(service, times(1)).transferOwner(anyLong(), any(), any(), any(), any());
    }

    @Test
    public void testTransferOwner_withNoVehicleRegistration_ShouldReturnNotFound() throws Exception {
        TransferOwnerRequest transferOwnerRequest = TestDataUtil.getNewOwner();
        when(service.transferOwner(anyLong(), any(), any(), any(), any()))
                .thenThrow(new VehicleNotFoundException(String.format(VEHICLE_NOT_FOUND, 1L)));

        mockMvc.perform(post("/vehicles/{vehicleId}/transfer-owner", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(transferOwnerRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value(String.format(VEHICLE_NOT_FOUND, 1)));

        verify(service, times(1)).transferOwner(anyLong(), any(), any(), any(), any());
    }

    @Test
    void testDelete() throws Exception {
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
        lenient().when(service.delete(anyLong()))
                .thenReturn(vehicleRegistration);

        mockMvc.perform(delete("/vehicles/1"))
                .andExpect(status().isOk());

        verify(service, times(1)).delete(any());
    }

    @Test
    void testDelete_withNotSavedVehicleRegistration_shouldReturnNotFound() throws Exception {
        when(service.delete(1L)).thenThrow(new VehicleNotFoundException(String.format(VEHICLE_NOT_FOUND, 1)));

        mockMvc.perform(delete("/vehicles/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value(String.format(VEHICLE_NOT_FOUND, 1)));

        verify(service, times(1)).delete(any());
    }
}

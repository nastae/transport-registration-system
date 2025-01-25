package lt.transport.registration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lt.transport.registration.dto.TransferOwnerRequest;
import lt.transport.registration.dto.VehicleRegistrationPageResponse;
import lt.transport.registration.dto.VehicleRegistrationRequest;
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

import static lt.transport.registration.constants.ResponseMessages.PLATE_NO_ALREADY_EXISTS;
import static lt.transport.registration.constants.ResponseMessages.VEHICLE_NOT_FOUND;
import static lt.transport.registration.constants.ResponseMessages.VEHICLE_OWNER_TRANSFERRED;
import static lt.transport.registration.constants.ResponseMessages.VEHICLE_REGISTERED_SUCCESSFULLY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebMvcTest(VehicleRegistrationController.class)
public class VehicleRegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VehicleRegistrationService vehicleRegistrationService;

    private ObjectMapper objectMapper;

    @BeforeAll
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testRegisterVehicleForNaturalPerson() throws Exception {
        VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
        lenient().when(vehicleRegistrationService.saveVehicleRegistration(any(VehicleRegistrationRequest.class)))
                .thenReturn(vehicleRegistration);

        mockMvc.perform(post("/vehicles/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(vehicleRegistrationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(VEHICLE_REGISTERED_SUCCESSFULLY))
                .andExpect(jsonPath("$.vehicleId").value("1"));

        verify(vehicleRegistrationService).saveVehicleRegistration(any());
    }

    @Test
    void testRegisterVehicleForLegalEntity() throws Exception {
        VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getLegalEntityVehicleRegistrationRequest();
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
        lenient().when(vehicleRegistrationService.saveVehicleRegistration(any(VehicleRegistrationRequest.class)))
                .thenReturn(vehicleRegistration);

        mockMvc.perform(post("/vehicles/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(vehicleRegistrationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(VEHICLE_REGISTERED_SUCCESSFULLY))
                .andExpect(jsonPath("$.vehicleId").value("1"));

        verify(vehicleRegistrationService).saveVehicleRegistration(any());
    }

    @Test
    void testRegisterVehicle_withDuplicatedPlateNo_shouldReturnBadRequest() throws Exception {
        VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
        lenient().when(vehicleRegistrationService.saveVehicleRegistration(any(VehicleRegistrationRequest.class)))
                .thenThrow(new PlateNoAlreadyExistsException(PLATE_NO_ALREADY_EXISTS));

        mockMvc.perform(post("/vehicles/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(vehicleRegistrationRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(PLATE_NO_ALREADY_EXISTS))
                .andExpect(jsonPath("$.statusCode").value(400));

        verify(vehicleRegistrationService).saveVehicleRegistration(any());
    }

    @Test
    void testGetVehicleRegistrationById() throws Exception {
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
        when(vehicleRegistrationService.findVehicleRegistrationById(anyLong())).thenReturn(VehicleRegistrationMapper.INSTANCE.toDto(vehicleRegistration));

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

        verify(vehicleRegistrationService).findVehicleRegistrationById(anyLong());
    }

    @Test
    void testGetVehicleRegistrationByIdNotFound() throws Exception {
        when(vehicleRegistrationService.findVehicleRegistrationById(anyLong())).thenThrow(new VehicleNotFoundException(String.format(VEHICLE_NOT_FOUND, 1)));

        mockMvc.perform(get("/vehicles/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(String.format(VEHICLE_NOT_FOUND, 1)))
                .andExpect(jsonPath("$.statusCode").value(404));

        verify(vehicleRegistrationService).findVehicleRegistrationById(anyLong());
    }

    @Test
    void testGetAllVehicleRegistrations() throws Exception {
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
        when(vehicleRegistrationService.findAllVehicleRegistrations(0, 1, "plateNo", "ASC")).thenReturn(new VehicleRegistrationPageResponse(
                List.of(VehicleRegistrationMapper.INSTANCE.toDto(vehicleRegistration)),
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

        verify(vehicleRegistrationService).findAllVehicleRegistrations(anyInt(), anyInt(), any(), any());
    }

    @Test
    public void testTransferOwnerOfVehicleRegistration() throws Exception {
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
        TransferOwnerRequest transferOwnerRequest = TestDataUtil.getNewOwner();
        when(vehicleRegistrationService.transferOwnerOfVehicleRegistration(anyLong(), any(), any(), any(), any())).thenReturn(vehicleRegistration);

        mockMvc.perform(post("/vehicles/{vehicleId}/transfer-owner", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(transferOwnerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(VEHICLE_OWNER_TRANSFERRED))
                .andExpect(jsonPath("$.vehicleId").value("1"));

        verify(vehicleRegistrationService, times(1)).transferOwnerOfVehicleRegistration(anyLong(), any(), any(), any(), any());
    }

    @Test
    public void testTransferOwner_OfVehicleRegistration_withInvalidState_ShouldReturnNotFound() throws Exception {
        TransferOwnerRequest transferOwnerRequest = TestDataUtil.getNewOwner();
        when(vehicleRegistrationService.transferOwnerOfVehicleRegistration(anyLong(), any(), any(), any(), any()))
                .thenThrow(new IllegalStateException("message"));

        mockMvc.perform(post("/vehicles/{vehicleId}/transfer-owner", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(transferOwnerRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("message"))
                .andExpect(jsonPath("$.statusCode").value(404));

        verify(vehicleRegistrationService, times(1)).transferOwnerOfVehicleRegistration(anyLong(), any(), any(), any(), any());
    }

    @Test
    public void testTransferOwner_OfVehicleRegistration_withNoVehicleRegistration_ShouldReturnNotFound() throws Exception {
        TransferOwnerRequest transferOwnerRequest = TestDataUtil.getNewOwner();
        when(vehicleRegistrationService.transferOwnerOfVehicleRegistration(anyLong(), any(), any(), any(), any()))
                .thenThrow(new VehicleNotFoundException(String.format(VEHICLE_NOT_FOUND, 1L)));

        mockMvc.perform(post("/vehicles/{vehicleId}/transfer-owner", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(transferOwnerRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(String.format(VEHICLE_NOT_FOUND, 1)))
                .andExpect(jsonPath("$.statusCode").value(404));

        verify(vehicleRegistrationService, times(1)).transferOwnerOfVehicleRegistration(anyLong(), any(), any(), any(), any());
    }

    @Test
    void testDeleteVehicleRegistration() throws Exception {
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
        lenient().when(vehicleRegistrationService.deleteVehicleRegistration(anyLong()))
                .thenReturn(vehicleRegistration);

        mockMvc.perform(delete("/vehicles/1"))
                .andExpect(status().isOk());

        verify(vehicleRegistrationService, times(1)).deleteVehicleRegistration(any());
    }

    @Test
    void testDelete_VehicleRegistration_withNotSavedVehicleRegistration_shouldReturnNotFound() throws Exception {
        when(vehicleRegistrationService.deleteVehicleRegistration(1L)).thenThrow(new VehicleNotFoundException(String.format(VEHICLE_NOT_FOUND, 1)));

        mockMvc.perform(delete("/vehicles/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(String.format(VEHICLE_NOT_FOUND, 1)))
                .andExpect(jsonPath("$.statusCode").value(404));

        verify(vehicleRegistrationService, times(1)).deleteVehicleRegistration(any());
    }
}

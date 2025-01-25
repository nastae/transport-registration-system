package lt.transport.registration.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lt.transport.registration.dto.TransferOwnerRequest;
import lt.transport.registration.dto.VehicleRegistrationRequest;
import lt.transport.registration.entity.VehicleOwnershipHistory;
import lt.transport.registration.entity.VehicleRegistration;
import lt.transport.registration.mapper.VehicleRegistrationMapper;
import lt.transport.registration.repository.VehicleRegistrationRepository;
import lt.transport.registration.util.TestDataUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static lt.transport.registration.constants.ResponseMessages.MAKE_IS_REQUIRED;
import static lt.transport.registration.constants.ResponseMessages.MODEL_IS_REQUIRED;
import static lt.transport.registration.constants.ResponseMessages.OWNER_CODE_IS_REQUIRED;
import static lt.transport.registration.constants.ResponseMessages.OWNER_NAME_IS_REQUIRED;
import static lt.transport.registration.constants.ResponseMessages.OWNER_SURNAME_IS_REQUIRED;
import static lt.transport.registration.constants.ResponseMessages.PLATE_NO_ALREADY_EXISTS;
import static lt.transport.registration.constants.ResponseMessages.PLATE_NO_IS_REQUIRED;
import static lt.transport.registration.constants.ResponseMessages.VEHICLE_DELETED;
import static lt.transport.registration.constants.ResponseMessages.VEHICLE_NOT_FOUND;
import static lt.transport.registration.constants.ResponseMessages.VEHICLE_OWNER_TRANSFERRED;
import static lt.transport.registration.constants.ResponseMessages.VEHICLE_REGISTERED_SUCCESSFULLY;
import static lt.transport.registration.constants.ResponseMessages.YEAR_IS_REQUIRED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
public class VehicleRegistrationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private VehicleRegistrationRepository repository;
    private ObjectMapper objectMapper;

    @BeforeAll
    void setUpBeforeAll() {
        objectMapper = new ObjectMapper();
    }

    @BeforeEach
    void setUpBeforeEach() {
        repository.deleteAll();
        resetAutoIncrement();
    }

    @Test
    void testRegisterVehicleForNaturalPerson() throws Exception {
        VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
        mockMvc.perform(post("/vehicles/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(vehicleRegistrationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(VEHICLE_REGISTERED_SUCCESSFULLY))
                .andExpect(jsonPath("$.vehicleId").value("1"));

        assertTrue(repository.existsById(1L));
    }

    @Test
    void testRegisterVehicleForLegalEntity() throws Exception {
        VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getLegalEntityVehicleRegistrationRequest();
        mockMvc.perform(post("/vehicles/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(vehicleRegistrationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(VEHICLE_REGISTERED_SUCCESSFULLY))
                .andExpect(jsonPath("$.vehicleId").value("1"));

        assertTrue(repository.existsById(1L));
    }

    @Test
    public void testRegisterVehicle_withUniquePlateNo_shouldSuccess() throws Exception {
        VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
        repository.save(VehicleRegistrationMapper.INSTANCE.toEntity(vehicleRegistrationRequest));

        vehicleRegistrationRequest = new VehicleRegistrationRequest("BCD123",
                "Toyota", "Corolla", 2020, "Jonas", "Petrauskas",
                null, "39601010000");

        mockMvc.perform(post("/vehicles/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(vehicleRegistrationRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(VEHICLE_REGISTERED_SUCCESSFULLY))
                .andExpect(jsonPath("$.vehicleId").value("2"));

        assertTrue(repository.existsById(2L));
    }

    @Test
    public void testRegisterVehicle_withDuplicatedPlateNo_shouldReturnBadRequest() throws Exception {
        VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();
        repository.save(VehicleRegistrationMapper.INSTANCE.toEntity(vehicleRegistrationRequest));

        mockMvc.perform(post("/vehicles/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(vehicleRegistrationRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(PLATE_NO_ALREADY_EXISTS))
                .andExpect(jsonPath("$.statusCode").value(400));

        assertTrue(repository.existsById(1L));
    }

    @Test
    public void testRegisterVehicle_withPlateNoNull_shouldReturnBadRequest() throws Exception {
        VehicleRegistrationRequest vehicleRegistrationRequest = new VehicleRegistrationRequest(null,
                "Toyota", "Corolla", 2020, "Jonas", "Petrauskas",
                null, "39601010000");

        mockMvc.perform(post("/vehicles/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(vehicleRegistrationRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(PLATE_NO_IS_REQUIRED))
                .andExpect(jsonPath("$.statusCode").value(400));

        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    public void testRegisterVehicle_withPlateNoEmpty_shouldReturnBadRequest() throws Exception {
        VehicleRegistrationRequest vehicleRegistrationRequest = new VehicleRegistrationRequest("",
                "Toyota", "Corolla", 2020, "Jonas", "Petrauskas",
                null, "39601010000");

        mockMvc.perform(post("/vehicles/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(vehicleRegistrationRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(PLATE_NO_IS_REQUIRED))
                .andExpect(jsonPath("$.statusCode").value(400));

        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    public void testRegisterVehicle_withMakeNull_shouldReturnBadRequest() throws Exception {
        VehicleRegistrationRequest vehicleRegistrationRequest = new VehicleRegistrationRequest("ABC123",
                null, "Corolla", 2020, "Jonas", "Petrauskas",
                null, "39601010000");

        mockMvc.perform(post("/vehicles/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(vehicleRegistrationRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(MAKE_IS_REQUIRED))
                .andExpect(jsonPath("$.statusCode").value(400));

        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    public void testRegisterVehicle_withMakeEmpty_shouldReturnBadRequest() throws Exception {
        VehicleRegistrationRequest vehicleRegistrationRequest = new VehicleRegistrationRequest("ABC123",
                "", "Corolla", 2020, "Jonas", "Petrauskas",
                null, "39601010000");

        mockMvc.perform(post("/vehicles/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(vehicleRegistrationRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(MAKE_IS_REQUIRED))
                .andExpect(jsonPath("$.statusCode").value(400));

        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    public void testRegisterVehicle_withModelNull_shouldReturnBadRequest() throws Exception {
        VehicleRegistrationRequest vehicleRegistrationRequest = new VehicleRegistrationRequest("ABC123",
                "Toyota", null, 2020, "Jonas", "Petrauskas",
                null, "39601010000");

        mockMvc.perform(post("/vehicles/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(vehicleRegistrationRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(MODEL_IS_REQUIRED))
                .andExpect(jsonPath("$.statusCode").value(400));

        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    public void testRegisterVehicle_withModelEmpty_shouldReturnBadRequest() throws Exception {
        VehicleRegistrationRequest vehicleRegistrationRequest = new VehicleRegistrationRequest("ABC123",
                "Toyota", "", 2020, "Jonas", "Petrauskas",
                null, "39601010000");

        mockMvc.perform(post("/vehicles/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(vehicleRegistrationRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(MODEL_IS_REQUIRED))
                .andExpect(jsonPath("$.statusCode").value(400));

        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    public void testRegisterVehicle_withYearNull_shouldReturnBadRequest() throws Exception {
        VehicleRegistrationRequest vehicleRegistrationRequest = new VehicleRegistrationRequest("ABC123",
                "Toyota", "Corolla", null, "Jonas", "Petrauskas",
                null, "39601010000");

        mockMvc.perform(post("/vehicles/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(vehicleRegistrationRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(YEAR_IS_REQUIRED))
                .andExpect(jsonPath("$.statusCode").value(400));

        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    public void testRegisterVehicle_withOwnerNameNull_shouldReturnBadRequest() throws Exception {
        VehicleRegistrationRequest vehicleRegistrationRequest = new VehicleRegistrationRequest("ABC123",
                "Toyota", "Corolla", 2020, null, "Petrauskas",
                null, "39601010000");

        mockMvc.perform(post("/vehicles/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(vehicleRegistrationRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(OWNER_NAME_IS_REQUIRED))
                .andExpect(jsonPath("$.statusCode").value(400));

        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    public void testRegisterVehicle_withOwnerNameEmpty_shouldReturnBadRequest() throws Exception {
        VehicleRegistrationRequest vehicleRegistrationRequest = new VehicleRegistrationRequest("ABC123",
                "Toyota", "Corolla", 2020, "", "Petrauskas",
                null, "39601010000");

        mockMvc.perform(post("/vehicles/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(vehicleRegistrationRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(OWNER_NAME_IS_REQUIRED))
                .andExpect(jsonPath("$.statusCode").value(400));

        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    public void testRegisterVehicle_withOwnerSurnameNull_shouldReturnBadRequest() throws Exception {
        VehicleRegistrationRequest vehicleRegistrationRequest = new VehicleRegistrationRequest("ABC123",
                "Toyota", "Corolla", 2020, "Jonas", null,
                null, "39601010000");

        mockMvc.perform(post("/vehicles/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(vehicleRegistrationRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(OWNER_SURNAME_IS_REQUIRED))
                .andExpect(jsonPath("$.statusCode").value(400));

        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    public void testRegisterVehicle_withOwnerSurnameEmpty_shouldReturnBadRequest() throws Exception {
        VehicleRegistrationRequest vehicleRegistrationRequest = new VehicleRegistrationRequest("ABC123",
                "Toyota", "Corolla", 2020, "Jonas", "",
                null, "39601010000");

        mockMvc.perform(post("/vehicles/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(vehicleRegistrationRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(OWNER_SURNAME_IS_REQUIRED))
                .andExpect(jsonPath("$.statusCode").value(400));

        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    public void testRegisterVehicle_withOwnerLegalNameNull_shouldReturnSuccess() throws Exception {
        VehicleRegistrationRequest vehicleRegistrationRequest = new VehicleRegistrationRequest("ABC123",
                "Toyota", "Corolla", 2020, "Jonas", "Petrauskas",
                null, "39601010000");

        mockMvc.perform(post("/vehicles/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(vehicleRegistrationRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(VEHICLE_REGISTERED_SUCCESSFULLY))
                .andExpect(jsonPath("$.vehicleId").value("1"));

        assertTrue(repository.existsById(1L));
    }

    @Test
    public void testRegisterVehicle_withOwnerCodeNull_shouldReturnBadRequest() throws Exception {
        VehicleRegistrationRequest vehicleRegistrationRequest = new VehicleRegistrationRequest("ABC123",
                "Toyota", "Corolla", 2020, "Jonas", "Petrauskas",
                null, null);

        mockMvc.perform(post("/vehicles/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(vehicleRegistrationRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(OWNER_CODE_IS_REQUIRED))
                .andExpect(jsonPath("$.statusCode").value(400));

        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    public void testRegisterVehicle_withOwnerCodeEmpty_shouldReturnBadRequest() throws Exception {
        VehicleRegistrationRequest vehicleRegistrationRequest = new VehicleRegistrationRequest("ABC123",
                "Toyota", "Corolla", 2020, "Jonas", "Petrauskas",
                null, "");

        mockMvc.perform(post("/vehicles/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(vehicleRegistrationRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(OWNER_CODE_IS_REQUIRED))
                .andExpect(jsonPath("$.statusCode").value(400));

        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void testGetVehicleRegistrationById() throws Exception {
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
        vehicleRegistration.setId(null);
        VehicleRegistration savedVehicle = repository.save(vehicleRegistration);

        String plateNo = "ABC123";
        String make = "Toyota";
        String model = "Corolla";
        Integer year = 2020;
        String ownerName = "Jonas";
        String ownerSurname = "Petrauskas";
        String ownerCode = "39601010000";

        mockMvc.perform(get("/vehicles/{vehicleId}", savedVehicle.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plateNo").value(plateNo))
                .andExpect(jsonPath("$.make").value(make))
                .andExpect(jsonPath("$.model").value(model))
                .andExpect(jsonPath("$.year").value(year))
                .andExpect(jsonPath("$.ownerName").value(ownerName))
                .andExpect(jsonPath("$.ownerSurname").value(ownerSurname))
                .andExpect(jsonPath("$.ownerLegalName").isEmpty())
                .andExpect(jsonPath("$.ownerCode").value(ownerCode));

        Optional<VehicleRegistration> vehicleRegistrationOptional = repository.findById(savedVehicle.getId());

        assertTrue(vehicleRegistrationOptional.isPresent());
        vehicleRegistration = vehicleRegistrationOptional.get();
        assertEquals(plateNo, vehicleRegistration.getPlateNo());
        assertEquals(make, vehicleRegistration.getMake());
        assertEquals(model, vehicleRegistration.getModel());
        assertEquals(year, vehicleRegistration.getYear());
        assertEquals(ownerName, vehicleRegistration.getOwnerName());
        assertEquals(ownerSurname, vehicleRegistration.getOwnerSurname());
        assertNull(vehicleRegistration.getOwnerLegalName());
        assertEquals(ownerCode, vehicleRegistration.getOwnerCode());
    }

    @Test
    void testGetVehicleRegistrationByIdNotFound() throws Exception {
        mockMvc.perform(get("/vehicles/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(String.format(VEHICLE_NOT_FOUND, 1)))
                .andExpect(jsonPath("$.statusCode").value(404));

        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void testGetAllVehicleRegistrations() throws Exception {
        VehicleRegistration vehicleRegistration1 = TestDataUtil.getNaturalPersonVehicleRegistration();
        vehicleRegistration1.setId(null);
        VehicleRegistration vehicleRegistration2 = TestDataUtil.getLegalEntityVehicleRegistration();
        vehicleRegistration2.setId(null);

        repository.save(vehicleRegistration1);
        repository.save(vehicleRegistration2);

        String plateNo = "BCD456";
        String make = "Toyota";
        String model = "Corolla";
        Integer year = 2020;
        String ownerName = "Jonas";
        String ownerSurname = "Petrauskas";
        String ownerLegalName = "UAB ABC";
        String ownerCode = "123456789";

        mockMvc.perform(get("/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "1")
                        .param("size", "1")
                        .param("sortBy", "plateNo")
                        .param("sortDirection", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].vehicleId").value("2"))
                .andExpect(jsonPath("$.content[0].plateNo").value(plateNo))
                .andExpect(jsonPath("$.content[0].make").value(make))
                .andExpect(jsonPath("$.content[0].model").value(model))
                .andExpect(jsonPath("$.content[0].year").value(year))
                .andExpect(jsonPath("$.content[0].ownerName").value(ownerName))
                .andExpect(jsonPath("$.content[0].ownerSurname").value(ownerSurname))
                .andExpect(jsonPath("$.content[0].ownerLegalName").value(ownerLegalName))
                .andExpect(jsonPath("$.content[0].ownerCode").value(ownerCode))
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.pageSize").value(1))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.totalElements").value(2));

        Page<VehicleRegistration> vehicleRegistrationPage = repository.findAll(PageRequest.of(1, 1));

        assertEquals(1, vehicleRegistrationPage.getPageable().getPageNumber());
        assertEquals(1, vehicleRegistrationPage.getPageable().getPageSize());
        assertEquals(2, vehicleRegistrationPage.getTotalPages());
        assertEquals(2, vehicleRegistrationPage.getTotalElements());
        VehicleRegistration vehicleRegistration = vehicleRegistrationPage.getContent().get(0);
        assertEquals(plateNo, vehicleRegistration.getPlateNo());
        assertEquals(make, vehicleRegistration.getMake());
        assertEquals(model, vehicleRegistration.getModel());
        assertEquals(year, vehicleRegistration.getYear());
        assertEquals(ownerName, vehicleRegistration.getOwnerName());
        assertEquals(ownerSurname, vehicleRegistration.getOwnerSurname());
        assertEquals(ownerLegalName, vehicleRegistration.getOwnerLegalName());
        assertEquals(ownerCode, vehicleRegistration.getOwnerCode());
    }

    @Test
    @Transactional
    void testTransferOwnerOfVehicleRegistration() throws Exception {
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
        vehicleRegistration.setId(null);
        TransferOwnerRequest transferOwnerRequest = TestDataUtil.getNewOwner();
        VehicleRegistration savedVehicle = repository.save(vehicleRegistration);

        mockMvc.perform(post("/vehicles/{vehicleId}/transfer-owner", savedVehicle.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(transferOwnerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(VEHICLE_OWNER_TRANSFERRED))
                .andExpect(jsonPath("$.vehicleId").value(savedVehicle.getId().intValue()));

        Optional<VehicleRegistration> vehicleRegistrationOptional = repository.findById(savedVehicle.getId());

        assertTrue(vehicleRegistrationOptional.isPresent());
        VehicleRegistration vehicle = vehicleRegistrationOptional.get();
        List<VehicleOwnershipHistory> histories = vehicle.getOwnershipHistory();
        assertEquals(savedVehicle.getId(), vehicle.getId());
        assertEquals("ABC123", vehicle.getPlateNo());
        assertEquals("Toyota", vehicle.getMake());
        assertEquals("Corolla", vehicle.getModel());
        assertEquals(2020, vehicle.getYear());
        assertEquals("Petras", vehicle.getOwnerName());
        assertEquals("Petraitis", vehicle.getOwnerSurname());
        assertEquals("UAB Petras", vehicle.getOwnerLegalName());
        assertEquals("39601010000", vehicle.getOwnerCode());
        assertEquals(1, histories.size());
        VehicleOwnershipHistory history = histories.get(0);
        assertEquals("Jonas", history.getOwnerName());
        assertEquals("Petrauskas", history.getOwnerSurname());
        assertNull(history.getOwnerLegalName());
        assertEquals("39601010000", history.getOwnerCode());
    }

    @Transactional
    @Test
    void testTransferOwner_withNewOwnerOfVehicleRegistrationNameNull_shouldReturnBadRequest2() throws Exception {
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
        vehicleRegistration.setId(null);
        TransferOwnerRequest transferOwnerRequest = new TransferOwnerRequest(
                null, "Petraitis", "UAB Petras", "39601010000");
        VehicleRegistration savedVehicle = repository.save(vehicleRegistration);

        mockMvc.perform(post("/vehicles/{vehicleId}/transfer-owner", savedVehicle.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(transferOwnerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(OWNER_NAME_IS_REQUIRED))
                .andExpect(jsonPath("$.statusCode").value(400));

        assertEquals(1, repository.findAll().size());
        Optional<VehicleRegistration> vehicleRegistrationOptional = repository.findById(savedVehicle.getId());

        assertTrue(vehicleRegistrationOptional.isPresent());
        VehicleRegistration vehicle = vehicleRegistrationOptional.get();
        List<VehicleOwnershipHistory> histories = vehicle.getOwnershipHistory();
        assertEquals(savedVehicle.getId(), vehicle.getId());
        assertEquals("ABC123", vehicle.getPlateNo());
        assertEquals("Toyota", vehicle.getMake());
        assertEquals("Corolla", vehicle.getModel());
        assertEquals(2020, vehicle.getYear());
        assertEquals("Jonas", vehicle.getOwnerName());
        assertEquals("Petrauskas", vehicle.getOwnerSurname());
        assertNull(vehicle.getOwnerLegalName());
        assertEquals("39601010000", vehicle.getOwnerCode());
        assertEquals(0, histories.size());
    }

    @Transactional
    @Test
    void testTransferOwner_withNewOwnerOfVehicleRegistrationNameEmpty_shouldReturnBadRequest() throws Exception {
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
        vehicleRegistration.setId(null);
        TransferOwnerRequest transferOwnerRequest = new TransferOwnerRequest(
                "", "Petraitis", "UAB Petras", "39601010000");
        VehicleRegistration savedVehicle = repository.save(vehicleRegistration);

        mockMvc.perform(post("/vehicles/{vehicleId}/transfer-owner", savedVehicle.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(transferOwnerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(OWNER_NAME_IS_REQUIRED))
                .andExpect(jsonPath("$.statusCode").value(400));

        assertEquals(1, repository.findAll().size());
        Optional<VehicleRegistration> vehicleRegistrationOptional = repository.findById(savedVehicle.getId());

        assertTrue(vehicleRegistrationOptional.isPresent());
        VehicleRegistration vehicle = vehicleRegistrationOptional.get();
        List<VehicleOwnershipHistory> histories = vehicle.getOwnershipHistory();
        assertEquals(savedVehicle.getId(), vehicle.getId());
        assertEquals("ABC123", vehicle.getPlateNo());
        assertEquals("Toyota", vehicle.getMake());
        assertEquals("Corolla", vehicle.getModel());
        assertEquals(2020, vehicle.getYear());
        assertEquals("Jonas", vehicle.getOwnerName());
        assertEquals("Petrauskas", vehicle.getOwnerSurname());
        assertNull(vehicle.getOwnerLegalName());
        assertEquals("39601010000", vehicle.getOwnerCode());
        assertEquals(0, histories.size());
    }

    @Transactional
    @Test
    void testTransferOwner_withNewOwnerOfVehicleRegistrationSurnameNull_shouldReturnBadRequest() throws Exception {
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
        vehicleRegistration.setId(null);
        TransferOwnerRequest transferOwnerRequest = new TransferOwnerRequest(
                "Petras", null, "UAB Petras", "39601010000");
        VehicleRegistration savedVehicle = repository.save(vehicleRegistration);

        mockMvc.perform(post("/vehicles/{vehicleId}/transfer-owner", savedVehicle.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(transferOwnerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(OWNER_SURNAME_IS_REQUIRED))
                .andExpect(jsonPath("$.statusCode").value(400));

        assertEquals(1, repository.findAll().size());
        Optional<VehicleRegistration> vehicleRegistrationOptional = repository.findById(savedVehicle.getId());

        assertTrue(vehicleRegistrationOptional.isPresent());
        VehicleRegistration vehicle = vehicleRegistrationOptional.get();
        List<VehicleOwnershipHistory> histories = vehicle.getOwnershipHistory();
        assertEquals(savedVehicle.getId(), vehicle.getId());
        assertEquals("ABC123", vehicle.getPlateNo());
        assertEquals("Toyota", vehicle.getMake());
        assertEquals("Corolla", vehicle.getModel());
        assertEquals(2020, vehicle.getYear());
        assertEquals("Jonas", vehicle.getOwnerName());
        assertEquals("Petrauskas", vehicle.getOwnerSurname());
        assertNull(vehicle.getOwnerLegalName());
        assertEquals("39601010000", vehicle.getOwnerCode());
        assertEquals(0, histories.size());
    }

    @Transactional
    @Test
    void testTransferOwner_withNewOwnerOfVehicleRegistrationSurnameEmpty_shouldReturnBadRequest() throws Exception {
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
        vehicleRegistration.setId(null);
        TransferOwnerRequest transferOwnerRequest = new TransferOwnerRequest(
                "Petras", "", "UAB Petras", "39601010000");
        VehicleRegistration savedVehicle = repository.save(vehicleRegistration);

        mockMvc.perform(post("/vehicles/{vehicleId}/transfer-owner", savedVehicle.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(transferOwnerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(OWNER_SURNAME_IS_REQUIRED))
                .andExpect(jsonPath("$.statusCode").value(400));

        assertEquals(1, repository.findAll().size());
        Optional<VehicleRegistration> vehicleRegistrationOptional = repository.findById(savedVehicle.getId());

        assertTrue(vehicleRegistrationOptional.isPresent());
        VehicleRegistration vehicle = vehicleRegistrationOptional.get();
        List<VehicleOwnershipHistory> histories = vehicle.getOwnershipHistory();
        assertEquals(savedVehicle.getId(), vehicle.getId());
        assertEquals("ABC123", vehicle.getPlateNo());
        assertEquals("Toyota", vehicle.getMake());
        assertEquals("Corolla", vehicle.getModel());
        assertEquals(2020, vehicle.getYear());
        assertEquals("Jonas", vehicle.getOwnerName());
        assertEquals("Petrauskas", vehicle.getOwnerSurname());
        assertNull(vehicle.getOwnerLegalName());
        assertEquals("39601010000", vehicle.getOwnerCode());
        assertEquals(0, histories.size());
    }

    @Test
    @Transactional
    void testTransferOwner_withNewOwnerOfVehicleRegistrationLegalNameNull_shouldSuccess() throws Exception {
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
        vehicleRegistration.setId(null);
        TransferOwnerRequest transferOwnerRequest = new TransferOwnerRequest(
                "Petras", "Petraitis", null, "39601010000");
        VehicleRegistration savedVehicle = repository.save(vehicleRegistration);

        mockMvc.perform(post("/vehicles/{vehicleId}/transfer-owner", savedVehicle.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(transferOwnerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(VEHICLE_OWNER_TRANSFERRED))
                .andExpect(jsonPath("$.vehicleId").value(savedVehicle.getId().intValue()));

        Optional<VehicleRegistration> vehicleRegistrationOptional = repository.findById(savedVehicle.getId());

        assertTrue(vehicleRegistrationOptional.isPresent());
        VehicleRegistration vehicle = vehicleRegistrationOptional.get();
        List<VehicleOwnershipHistory> histories = vehicle.getOwnershipHistory();
        assertEquals(savedVehicle.getId(), vehicle.getId());
        assertEquals("ABC123", vehicle.getPlateNo());
        assertEquals("Toyota", vehicle.getMake());
        assertEquals("Corolla", vehicle.getModel());
        assertEquals(2020, vehicle.getYear());
        assertEquals("Petras", vehicle.getOwnerName());
        assertEquals("Petraitis", vehicle.getOwnerSurname());
        assertNull(vehicle.getOwnerLegalName());
        assertEquals("39601010000", vehicle.getOwnerCode());
        assertEquals(1, histories.size());
        VehicleOwnershipHistory history = histories.get(0);
        assertEquals("Jonas", history.getOwnerName());
        assertEquals("Petrauskas", history.getOwnerSurname());
        assertNull(history.getOwnerLegalName());
        assertEquals("39601010000", history.getOwnerCode());
    }


    @Transactional
    @Test
    void testTransferOwner_withNewOwnerOfVehicleRegistrationCodeNull_shouldReturnBadRequest2() throws Exception {
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
        vehicleRegistration.setId(null);
        TransferOwnerRequest transferOwnerRequest = new TransferOwnerRequest(
                "Petras", "Petraitis", "UAB Petras", null);
        VehicleRegistration savedVehicle = repository.save(vehicleRegistration);

        mockMvc.perform(post("/vehicles/{vehicleId}/transfer-owner", savedVehicle.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(transferOwnerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(OWNER_CODE_IS_REQUIRED))
                .andExpect(jsonPath("$.statusCode").value(400));

        assertEquals(1, repository.findAll().size());
        Optional<VehicleRegistration> vehicleRegistrationOptional = repository.findById(savedVehicle.getId());

        assertTrue(vehicleRegistrationOptional.isPresent());
        VehicleRegistration vehicle = vehicleRegistrationOptional.get();
        List<VehicleOwnershipHistory> histories = vehicle.getOwnershipHistory();
        assertEquals(savedVehicle.getId(), vehicle.getId());
        assertEquals("ABC123", vehicle.getPlateNo());
        assertEquals("Toyota", vehicle.getMake());
        assertEquals("Corolla", vehicle.getModel());
        assertEquals(2020, vehicle.getYear());
        assertEquals("Jonas", vehicle.getOwnerName());
        assertEquals("Petrauskas", vehicle.getOwnerSurname());
        assertNull(vehicle.getOwnerLegalName());
        assertEquals("39601010000", vehicle.getOwnerCode());
        assertEquals(0, histories.size());
    }

    @Transactional
    @Test
    void testTransferOwner_withNewOwnerOfVehicleRegistrationCodeEmpty_shouldReturnBadRequest() throws Exception {
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
        vehicleRegistration.setId(null);
        TransferOwnerRequest transferOwnerRequest = new TransferOwnerRequest(
                "Petras", "Petraitis", "UAB Petras", "");
        VehicleRegistration savedVehicle = repository.save(vehicleRegistration);

        mockMvc.perform(post("/vehicles/{vehicleId}/transfer-owner", savedVehicle.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(transferOwnerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(OWNER_CODE_IS_REQUIRED))
                .andExpect(jsonPath("$.statusCode").value(400));

        assertEquals(1, repository.findAll().size());
        Optional<VehicleRegistration> vehicleRegistrationOptional = repository.findById(savedVehicle.getId());

        assertTrue(vehicleRegistrationOptional.isPresent());
        VehicleRegistration vehicle = vehicleRegistrationOptional.get();
        List<VehicleOwnershipHistory> histories = vehicle.getOwnershipHistory();
        assertEquals(savedVehicle.getId(), vehicle.getId());
        assertEquals("ABC123", vehicle.getPlateNo());
        assertEquals("Toyota", vehicle.getMake());
        assertEquals("Corolla", vehicle.getModel());
        assertEquals(2020, vehicle.getYear());
        assertEquals("Jonas", vehicle.getOwnerName());
        assertEquals("Petrauskas", vehicle.getOwnerSurname());
        assertNull(vehicle.getOwnerLegalName());
        assertEquals("39601010000", vehicle.getOwnerCode());
        assertEquals(0, histories.size());
    }

    @Transactional
    @Test
    void testTransferOwner_OfVehicleRegistration_withNoVehicleRegistration_shouldReturnNotFound() throws Exception {
        TransferOwnerRequest transferOwnerRequest = TestDataUtil.getNewOwner();

        mockMvc.perform(post("/vehicles/{vehicleId}/transfer-owner", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(transferOwnerRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(String.format(VEHICLE_NOT_FOUND, 1)))
                .andExpect(jsonPath("$.statusCode").value(404));

        assertEquals(0, repository.findAll().size());
    }

    @Test
    void testDeleteVehicleRegistration() throws Exception {
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
        vehicleRegistration.setId(null);
        VehicleRegistration savedVehicle = repository.save(vehicleRegistration);

        mockMvc.perform(delete("/vehicles/{vehicleId}", savedVehicle.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(VEHICLE_DELETED))
                .andExpect(jsonPath("$.vehicleId").value(savedVehicle.getId()));

        assertEquals(1, repository.findAll().size());
        Optional<VehicleRegistration> deletedVehicleRegistrationOptional = repository.findById(1L);

        assertTrue(deletedVehicleRegistrationOptional.isPresent());
        VehicleRegistration deletedVehicleRegistration = deletedVehicleRegistrationOptional.get();
        assertEquals(savedVehicle.getId(), deletedVehicleRegistration.getId());
        assertEquals("ABC123", deletedVehicleRegistration.getPlateNo());
        assertEquals("Toyota", deletedVehicleRegistration.getMake());
        assertEquals("Corolla", deletedVehicleRegistration.getModel());
        assertEquals(2020, deletedVehicleRegistration.getYear());
        assertEquals("Jonas", deletedVehicleRegistration.getOwnerName());
        assertEquals("Petrauskas", deletedVehicleRegistration.getOwnerSurname());
        assertNull(deletedVehicleRegistration.getOwnerLegalName());
        assertEquals("39601010000", deletedVehicleRegistration.getOwnerCode());
        assertTrue(deletedVehicleRegistration.isDeleted());
    }

    @Test
    void testDelete_VehicleRegistration_withNotSavedVehicleRegistration_shouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/vehicles/{vehicleId}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(String.format(VEHICLE_NOT_FOUND, 1)))
                .andExpect(jsonPath("$.statusCode").value(404));

        assertEquals(0, repository.findAll().size());
    }

    @Test
    void testDeleteVehicleRegistrationAndGetVehicleRegistrationById() throws Exception {
        VehicleRegistration vehicleRegistration = TestDataUtil.getNaturalPersonVehicleRegistration();
        vehicleRegistration.setId(null);
        VehicleRegistration savedVehicle = repository.save(vehicleRegistration);

        mockMvc.perform(delete("/vehicles/{vehicleId}", savedVehicle.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(VEHICLE_DELETED))
                .andExpect(jsonPath("$.vehicleId").value(savedVehicle.getId()));

        mockMvc.perform(get("/vehicles/{vehicleId}", savedVehicle.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(String.format(VEHICLE_NOT_FOUND, 1)))
                .andExpect(jsonPath("$.statusCode").value(404));

        assertEquals(1, repository.findAll().size());
        Optional<VehicleRegistration> deletedVehicleRegistrationOptional = repository.findById(1L);

        assertTrue(deletedVehicleRegistrationOptional.isPresent());
        VehicleRegistration deletedVehicleRegistration = deletedVehicleRegistrationOptional.get();
        assertEquals(savedVehicle.getId(), deletedVehicleRegistration.getId());
        assertEquals("ABC123", deletedVehicleRegistration.getPlateNo());
        assertEquals("Toyota", deletedVehicleRegistration.getMake());
        assertEquals("Corolla", deletedVehicleRegistration.getModel());
        assertEquals(2020, deletedVehicleRegistration.getYear());
        assertEquals("Jonas", deletedVehicleRegistration.getOwnerName());
        assertEquals("Petrauskas", deletedVehicleRegistration.getOwnerSurname());
        assertNull(deletedVehicleRegistration.getOwnerLegalName());
        assertEquals("39601010000", deletedVehicleRegistration.getOwnerCode());
        assertTrue(deletedVehicleRegistration.isDeleted());
    }

    @Test
    void testDeleteVehicleRegistrationAndGetAllVehicleRegistrations() throws Exception {
        VehicleRegistration vehicleRegistration1 = TestDataUtil.getNaturalPersonVehicleRegistration();
        vehicleRegistration1.setId(null);
        VehicleRegistration savedVehicle = repository.save(vehicleRegistration1);

        mockMvc.perform(delete("/vehicles/{vehicleId}", savedVehicle.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(VEHICLE_DELETED))
                .andExpect(jsonPath("$.vehicleId").value(savedVehicle.getId()));

        mockMvc.perform(get("/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "1")
                        .param("size", "1")
                        .param("sortBy", "plateNo")
                        .param("sortDirection", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.pageSize").value(1))
                .andExpect(jsonPath("$.totalPages").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));

        assertEquals(1, repository.findAll().size());
        Optional<VehicleRegistration> deletedVehicleRegistrationOptional = repository.findById(1L);

        assertTrue(deletedVehicleRegistrationOptional.isPresent());
        VehicleRegistration deletedVehicleRegistration = deletedVehicleRegistrationOptional.get();
        assertEquals(savedVehicle.getId(), deletedVehicleRegistration.getId());
        assertEquals("ABC123", deletedVehicleRegistration.getPlateNo());
        assertEquals("Toyota", deletedVehicleRegistration.getMake());
        assertEquals("Corolla", deletedVehicleRegistration.getModel());
        assertEquals(2020, deletedVehicleRegistration.getYear());
        assertEquals("Jonas", deletedVehicleRegistration.getOwnerName());
        assertEquals("Petrauskas", deletedVehicleRegistration.getOwnerSurname());
        assertNull(deletedVehicleRegistration.getOwnerLegalName());
        assertEquals("39601010000", deletedVehicleRegistration.getOwnerCode());
        assertTrue(deletedVehicleRegistration.isDeleted());
    }

    private void resetAutoIncrement() {
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE;");
        jdbcTemplate.execute("TRUNCATE TABLE vehicle_registration;");
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE;");
        jdbcTemplate.execute("ALTER TABLE vehicle_registration ALTER COLUMN id RESTART WITH 1;");
    }
}

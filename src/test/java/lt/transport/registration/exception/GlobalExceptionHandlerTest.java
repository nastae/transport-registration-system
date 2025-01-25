package lt.transport.registration.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lt.transport.registration.controller.VehicleRegistrationController;
import lt.transport.registration.dto.VehicleRegistrationRequest;
import lt.transport.registration.service.VehicleRegistrationService;
import lt.transport.registration.util.TestDataUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebMvcTest(VehicleRegistrationController.class)
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VehicleRegistrationService vehicleRegistrationService;

    private ObjectMapper objectMapper;

    @BeforeAll
    void setUpBeforeAll() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testSaveVehicleRegistration_withRuntimeException_shouldReturnInternalServerError() throws Exception {
        VehicleRegistrationRequest vehicleRegistrationRequest = TestDataUtil.getNaturalPersonVehicleRegistrationRequest();

        when(vehicleRegistrationService.saveVehicleRegistration(any())).thenThrow(new RuntimeException("message"));

        mockMvc.perform(post("/vehicles/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(vehicleRegistrationRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("message"))
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        verify(vehicleRegistrationService).saveVehicleRegistration(any());
    }
}

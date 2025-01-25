package lt.transport.registration.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lt.transport.registration.dto.TransferOwnerRequest;
import lt.transport.registration.dto.VehicleActionResponse;
import lt.transport.registration.dto.VehicleRegistrationDetailsResponse;
import lt.transport.registration.dto.VehicleRegistrationPageResponse;
import lt.transport.registration.dto.VehicleRegistrationRequest;
import lt.transport.registration.mapper.VehicleRegistrationMapper;
import lt.transport.registration.service.VehicleRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static lt.transport.registration.constants.ResponseMessages.VEHICLE_DELETED;
import static lt.transport.registration.constants.ResponseMessages.VEHICLE_OWNER_TRANSFERRED;
import static lt.transport.registration.constants.ResponseMessages.VEHICLE_REGISTERED_SUCCESSFULLY;

@RestController
@RequestMapping("/vehicles")
@Tag(name = "Transporto priemonės", description = "Transporto priemonių valdymo API")
public class VehicleRegistrationController {

    private final VehicleRegistrationService vehicleRegistrationService;

    @Autowired
    public VehicleRegistrationController(VehicleRegistrationService vehicleRegistrationService) {
        this.vehicleRegistrationService = vehicleRegistrationService;
    }

    @PostMapping("/register")
    @Operation(
            summary = "Pridėti naują transporto priemonę",
            description = "Prideda naują transporto priemonės registraciją į sistemą",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transporto priemonė sėkmingai pridėta",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Neteisingi transporto priemonės įvesties duomenys arba nurodytas valstybinis numeris jau egzistuoja sistemoje"),
                    @ApiResponse(responseCode = "500", description = "Vidinė serverio klaida")
            }
    )
    public VehicleActionResponse createVehicleRegistration(@Valid @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Transporto priemonės duomenys",
            required = true,
            content = @Content(schema = @Schema(implementation = VehicleRegistrationRequest.class)))
                                                           VehicleRegistrationRequest vehicleRegistrationRequest) {
        var vehicleRegistration = vehicleRegistrationService.saveVehicleRegistration(vehicleRegistrationRequest);
        return VehicleRegistrationMapper.INSTANCE.toVehicleActionResponse(VEHICLE_REGISTERED_SUCCESSFULLY, vehicleRegistration);
    }

    @GetMapping("/{vehicleId}")
    @Operation(
            summary = "Gauti transporto priemonės informaciją pagal ID",
            description = "Ši funkcija grąžina transporto priemonės registracijos informaciją pagal pateiktą ID. Jei transporto priemonė nerasta, grąžinamas klaidos pranešimas.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Sėkmingas atsakymas su transporto priemonės duomenimis",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = VehicleRegistrationDetailsResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Transporto priemonė nerasta pagal pateiktą ID",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    )
            }
    )
    public VehicleRegistrationDetailsResponse getVehicleRegistrationById(
            @PathVariable @Schema(description = "Transporto priemonės ID", example = "1") Long vehicleId) {
        return vehicleRegistrationService.findVehicleRegistrationById(vehicleId);
    }

    @GetMapping
    @Operation(
            summary = "Gauti visas transporto priemones",
            description = "Gauti visų transporto priemonių registracijų sąrašą su puslapiavimu ir rūšiavimu",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sėkmingai gautas sąrašas",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "500", description = "Vidinė serverio klaida")
            }
    )
    public VehicleRegistrationPageResponse getAllVehicleRegistrations(
            @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "Puslapio numeris (pradedant nuo 0)") int page,
            @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "Elementų skaičius puslapyje") int size,
            @RequestParam(defaultValue = "id") @Parameter(description = "Rūšiavimo laukas") String sortBy,
            @RequestParam(defaultValue = "ASC") @Parameter(description = "Rūšiavimo tvarka (ASC arba DESC)") String sortDirection) {
        return vehicleRegistrationService.findAllVehicleRegistrations(page, size, sortBy, sortDirection);
    }

    @PostMapping("/{vehicleId}/transfer-owner")
    @Operation(
            summary = "Perleisti transporto priemonės savininkystę",
            description = "Ši funkcija leidžia perduoti transporto priemonės savininką naujam asmeniui. Jei transporto priemonė nerandama arba pateikti įvesties duomenys neteisingi, grąžinamas klaidos pranešimas.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Sėkmingas atsakymas su atnaujinta transporto priemonės ID",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = VehicleActionResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Klaida dėl neteisingų įvesties duomenų",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Transporto priemonė nerasta pagal pateiktą ID arba esamas savininkas nerastas",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(responseCode = "500", description = "Vidinė serverio klaida")
            }
    )
    public VehicleActionResponse transferOwnerOfVehicleRegistration(
            @PathVariable("vehicleId") @Schema(description = "Transporto priemonės ID", example = "1") Long vehicleId,
            @Valid @RequestBody @Schema(description = "Naujo savininko duomenys", implementation = TransferOwnerRequest.class) TransferOwnerRequest transferOwnerRequest) {
        var updatedVehicleRegistration = vehicleRegistrationService.transferOwnerOfVehicleRegistration(vehicleId, transferOwnerRequest.newOwnerName(),
                transferOwnerRequest.newOwnerSurname(), transferOwnerRequest.newOwnerLegalName(), transferOwnerRequest.newOwnerCode());
        return VehicleRegistrationMapper.INSTANCE.toVehicleActionResponse(VEHICLE_OWNER_TRANSFERRED, updatedVehicleRegistration);
    }

    @DeleteMapping("/{vehicleId}")
    @Operation(
            summary = "Ištrinti transporto priemonę",
            description = "Ši funkcija leidžia ištrinti transporto priemonę pagal pateiktą ID, tai yra sistema ją duomenų bazėje pažmyi, kaip negaliojančia. Jei transporto priemonė nerandama, grąžinama klaida.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Sėkmingas atsakymas su pranešimu apie ištrintą transporto priemonės ID",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = VehicleActionResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Transporto priemonė nerasta pagal pateiktą ID",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(responseCode = "500", description = "Vidinė serverio klaida")
            }
    )
    public VehicleActionResponse deleteVehicleRegistration(
            @PathVariable("vehicleId") @Schema(description = "Transporto priemonės ID", example = "1") Long vehicleId) {
        var vehicleRegistration = vehicleRegistrationService.deleteVehicleRegistration(vehicleId);
        return VehicleRegistrationMapper.INSTANCE.toVehicleActionResponse(VEHICLE_DELETED, vehicleRegistration);
    }
}

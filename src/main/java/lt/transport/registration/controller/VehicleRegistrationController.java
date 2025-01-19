package lt.transport.registration.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lt.transport.registration.DTO.*;
import lt.transport.registration.entity.VehicleRegistration;
import lt.transport.registration.exception.PlateNoAlreadyExistsException;
import lt.transport.registration.exception.VehicleNotFoundException;
import lt.transport.registration.mapper.VehicleRegistrationMapper;
import lt.transport.registration.service.VehicleRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static lt.transport.registration.constants.ResponseMessages.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/vehicles")
@Tag(name = "Transporto priemonės", description = "Transporto priemonių valdymo API")
public class VehicleRegistrationController {

    private final VehicleRegistrationService service;

    @Autowired
    public VehicleRegistrationController(VehicleRegistrationService service) {
        this.service = service;
    }

    @PostMapping("/register")
    @Operation(
            summary = "Pridėti naują transporto priemonę",
            description = "Prideda naują transporto priemonės registraciją į sistemą",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Transporto priemonė sėkmingai pridėta",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Neteisingi transporto priemonės įvesties duomenys arba nurodytas valstybinis numeris jau egzistuoja sistemoje"),
                    @ApiResponse(responseCode = "500", description = "Vidinė serverio klaida")
            }
    )
    public ResponseEntity<?> create(@RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Transporto priemonės duomenys",
            required = true,
            content = @Content(schema = @Schema(implementation = VehicleRegistrationRequest.class)))
            VehicleRegistrationRequest vehicleRegistrationRequest) {
        try {
            VehicleRegistration vehicleRegistration = service.save(vehicleRegistrationRequest);
            return ResponseEntity.ok(VehicleRegistrationMapper.toVehicleActionResponse(VEHICLE_REGISTERED_SUCCESSFULLY, vehicleRegistration));
        } catch (IllegalArgumentException | PlateNoAlreadyExistsException e) {
            return ResponseEntity.status(BAD_REQUEST).body(e.getMessage());
        }
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
    public ResponseEntity<?> getById(@PathVariable @Schema(description = "Transporto priemonės ID", example = "1") Long vehicleId) {
        try {
            return ResponseEntity.ok(service.findById(vehicleId));
        } catch (VehicleNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        }
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
    public ResponseEntity<VehicleRegistrationPageResponse> getAll(
            @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "Puslapio numeris (pradedant nuo 0)") int page,
            @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "Elementų skaičius puslapyje") int size,
            @RequestParam(defaultValue = "id") @Parameter(description = "Rūšiavimo laukas") String sortBy,
            @RequestParam(defaultValue = "ASC") @Parameter(description = "Rūšiavimo tvarka (ASC arba DESC)") String sortDirection) {
        return ResponseEntity.ok(service.findAll(page, size, sortBy, sortDirection));
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
    public ResponseEntity<?> transferOwner(@PathVariable("vehicleId") @Schema(description = "Transporto priemonės ID", example = "1") Long vehicleId,
                                           @RequestBody @Schema(description = "Naujo savininko duomenys", implementation = TransferOwnerRequest.class) TransferOwnerRequest transferOwnerRequest) {
        try {

            VehicleRegistration updatedVehicleRegistration = service.transferOwner(vehicleId, transferOwnerRequest.getNewOwnerName(),
                    transferOwnerRequest.getNewOwnerSurname(), transferOwnerRequest.getNewOwnerLegalName(), transferOwnerRequest.getNewOwnerCode());
            return ResponseEntity.ok(VehicleRegistrationMapper.toVehicleActionResponse(VEHICLE_OWNER_TRANSFERRED, updatedVehicleRegistration));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(BAD_REQUEST).body(e.getMessage());
        } catch (VehicleNotFoundException | IllegalStateException e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        }
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
    public ResponseEntity<?> delete(@PathVariable("vehicleId") @Schema(description = "Transporto priemonės ID", example = "1") Long vehicleId) {
        try {
            VehicleRegistration vehicleRegistration = service.delete(vehicleId);
            return ResponseEntity.ok(VehicleRegistrationMapper.toVehicleActionResponse(VEHICLE_DELETED, vehicleRegistration));
        } catch (VehicleNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        }
    }
}

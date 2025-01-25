package lt.transport.registration.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import static lt.transport.registration.constants.ResponseMessages.PLATE_NO_ALREADY_EXISTS;

@Schema(description = "Atsakymas su klaidos žinute ir statuso kodu")
public record ErrorResponse(
        @Schema(description = "Klaidos žinutė, kuri paaiškina klaidos priežastį", example = PLATE_NO_ALREADY_EXISTS)
        String message,
        @Schema(description = "HTTP statuso kodas", example = "500")
        int statusCode) {
}

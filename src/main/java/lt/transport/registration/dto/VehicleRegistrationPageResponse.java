package lt.transport.registration.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Transporto priemonių registracijos su puslapiavimu atsakymas")
public record VehicleRegistrationPageResponse(
        @Schema(description = "Transporto priemonių registracijų sąrašas")
        List<VehicleRegistrationDetailsResponse> content,
        @Schema(description = "Dabartinis puslapio numeris")
        int currentPage,
        @Schema(description = "Puslapio dydis (kiek įrašų rodoma viename puslapyje)")
        int pageSize,
        @Schema(description = "Iš viso puslapių skaičius")
        int totalPages,
        @Schema(description = "Iš viso įrašų skaičius")
        long totalElements) {
}

package lt.transport.registration.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Transporto priemonių registracijos su puslapiavimu atsakymas")
public class VehicleRegistrationPageResponse {

    @Schema(description = "Transporto priemonių registracijų sąrašas")
    private List<VehicleRegistrationDetailsResponse> content;
    @Schema(description = "Dabartinis puslapio numeris")
    private int currentPage;
    @Schema(description = "Puslapio dydis (kiek įrašų rodoma viename puslapyje)")
    private int pageSize;
    @Schema(description = "Iš viso puslapių skaičius")
    private int totalPages;
    @Schema(description = "Iš viso įrašų skaičius")
    private long totalElements;

    public VehicleRegistrationPageResponse() {
    }

    public VehicleRegistrationPageResponse(List<VehicleRegistrationDetailsResponse> content, int currentPage, int pageSize,
                                           int totalPages, long totalElements) {
        this.content = content;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }

    public List<VehicleRegistrationDetailsResponse> getContent() {
        return content;
    }

    public void setContent(List<VehicleRegistrationDetailsResponse> content) {
        this.content = content;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
}

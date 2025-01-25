package lt.transport.registration.util;

import lt.transport.registration.dto.TransferOwnerRequest;
import lt.transport.registration.dto.VehicleRegistrationRequest;
import lt.transport.registration.entity.VehicleRegistration;

import java.util.ArrayList;

public class TestDataUtil {

    public static VehicleRegistration getNaturalPersonVehicleRegistration() {
        return VehicleRegistration.builder()
                .id(1L)
                .plateNo("ABC123")
                .make("Toyota")
                .model("Corolla")
                .year(2020)
                .ownerName("Jonas")
                .ownerSurname("Petrauskas")
                .ownerLegalName(null)
                .ownerCode("39601010000")
                .ownershipHistory(new ArrayList<>())
                .build();
    }

    public static VehicleRegistration getLegalEntityVehicleRegistration() {
        return VehicleRegistration.builder()
                .id(1L)
                .plateNo("BCD456")
                .make("Toyota")
                .model("Corolla")
                .year(2020)
                .ownerName("Jonas")
                .ownerSurname("Petrauskas")
                .ownerLegalName("UAB ABC")
                .ownerCode("123456789")
                .build();
    }

    public static VehicleRegistrationRequest getNaturalPersonVehicleRegistrationRequest() {
        return new VehicleRegistrationRequest("ABC123",
                "Toyota", "Corolla", 2020, "Jonas", "Petrauskas",
                null, "39601010000");
    }

    public static VehicleRegistrationRequest getLegalEntityVehicleRegistrationRequest() {
        return new VehicleRegistrationRequest(
                "BCD456", "Toyota", "Corolla", 2020, "Jonas",
                "Petrauskas", "UAB ABC", "123456789");
    }

    public static TransferOwnerRequest getNewOwner() {
        return new TransferOwnerRequest(
                "Petras", "Petraitis", "UAB Petras", "39601010000");
    }
}

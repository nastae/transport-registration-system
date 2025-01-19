package lt.transport.registration.util;

import lt.transport.registration.DTO.TransferOwnerRequest;
import lt.transport.registration.DTO.VehicleRegistrationRequest;
import lt.transport.registration.entity.VehicleRegistration;

public class TestDataUtil {

    public static VehicleRegistration getNaturalPersonVehicleRegistration() {
        VehicleRegistration vehicleRegistration = new VehicleRegistration();
        vehicleRegistration.setId(1L);
        vehicleRegistration.setPlateNo("ABC123");
        vehicleRegistration.setMake("Toyota");
        vehicleRegistration.setModel("Corolla");
        vehicleRegistration.setYear(2020);
        vehicleRegistration.setOwnerName("Jonas");
        vehicleRegistration.setOwnerSurname("Petrauskas");
        vehicleRegistration.setOwnerLegalName(null);
        vehicleRegistration.setOwnerCode("39601010000");
        return vehicleRegistration;
    }

    public static VehicleRegistration getLegalEntityVehicleRegistration() {
        VehicleRegistration vehicleRegistration = new VehicleRegistration();
        vehicleRegistration.setId(1L);
        vehicleRegistration.setPlateNo("BCD456");
        vehicleRegistration.setMake("Toyota");
        vehicleRegistration.setModel("Corolla");
        vehicleRegistration.setYear(2020);
        vehicleRegistration.setOwnerName("Jonas");
        vehicleRegistration.setOwnerSurname("Petrauskas");
        vehicleRegistration.setOwnerLegalName("UAB ABC");
        vehicleRegistration.setOwnerCode("123456789");
        return vehicleRegistration;
    }

    public static VehicleRegistrationRequest getNaturalPersonVehicleRegistrationRequest() {
        VehicleRegistrationRequest vehicleRegistrationRequest = new VehicleRegistrationRequest();
        vehicleRegistrationRequest.setPlateNo("ABC123");
        vehicleRegistrationRequest.setMake("Toyota");
        vehicleRegistrationRequest.setModel("Corolla");
        vehicleRegistrationRequest.setYear(2020);
        vehicleRegistrationRequest.setOwnerName("Jonas");
        vehicleRegistrationRequest.setOwnerSurname("Petrauskas");
        vehicleRegistrationRequest.setOwnerLegalName(null);
        vehicleRegistrationRequest.setOwnerCode("39601010000");
        return vehicleRegistrationRequest;
    }

    public static VehicleRegistrationRequest getLegalEntityVehicleRegistrationRequest() {
        VehicleRegistrationRequest vehicleRegistrationRequest = new VehicleRegistrationRequest();
        vehicleRegistrationRequest.setPlateNo("BCD456");
        vehicleRegistrationRequest.setMake("Toyota");
        vehicleRegistrationRequest.setModel("Corolla");
        vehicleRegistrationRequest.setYear(2020);
        vehicleRegistrationRequest.setOwnerName("Jonas");
        vehicleRegistrationRequest.setOwnerSurname("Petrauskas");
        vehicleRegistrationRequest.setOwnerLegalName("UAB ABC");
        vehicleRegistrationRequest.setOwnerCode("123456789");
        return vehicleRegistrationRequest;
    }

    public static TransferOwnerRequest getNewOwner() {
        TransferOwnerRequest transferOwnerRequest = new TransferOwnerRequest();
        transferOwnerRequest.setNewOwnerName("Petras");
        transferOwnerRequest.setNewOwnerSurname("Petraitis");
        transferOwnerRequest.setNewOwnerLegalName("UAB Petras");
        transferOwnerRequest.setNewOwnerCode("39601010000");
        return transferOwnerRequest;
    }
}

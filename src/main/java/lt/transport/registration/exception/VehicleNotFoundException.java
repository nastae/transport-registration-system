package lt.transport.registration.exception;

import java.util.NoSuchElementException;

public class VehicleNotFoundException extends NoSuchElementException {
    public VehicleNotFoundException(String message) {
        super(message);
    }
}

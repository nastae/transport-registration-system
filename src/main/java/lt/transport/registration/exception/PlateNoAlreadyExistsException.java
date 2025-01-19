package lt.transport.registration.exception;

public class PlateNoAlreadyExistsException extends RuntimeException {
    public PlateNoAlreadyExistsException(String message) {
        super(message);
    }
}
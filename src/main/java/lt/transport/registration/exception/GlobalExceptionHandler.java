package lt.transport.registration.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static lt.transport.registration.constants.ResponseMessages.REQUEST_BODY_CANNOT_BE_NULL;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errorMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .toList();
        String message = String.join(", ", errorMessages);
        logger.error("Validation failed: {}", message, ex);
        return new ErrorResponse(message, BAD_REQUEST.value());
    }

    @ExceptionHandler(PlateNoAlreadyExistsException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handlePlateNoAlreadyExistsException(PlateNoAlreadyExistsException ex) {
        logger.error("Plate number already exists: {}", ex.getMessage(), ex);
        return new ErrorResponse(ex.getMessage(), BAD_REQUEST.value());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleMissingRequestBody(HttpMessageNotReadableException ex) {
        logger.error("Request body is missing or malformed: {}", ex.getMessage(), ex);
        return new ErrorResponse(REQUEST_BODY_CANNOT_BE_NULL, BAD_REQUEST.value());
    }

    @ExceptionHandler(VehicleNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ErrorResponse handleVehicleNotFoundException(VehicleNotFoundException ex) {
        logger.error("Vehicle not found: {}", ex.getMessage(), ex);
        return new ErrorResponse(ex.getMessage(), NOT_FOUND.value());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(NOT_FOUND)
    public ErrorResponse handleIllegalStateException(IllegalStateException ex) {
        logger.error("Illegal state occurred: {}", ex.getMessage(), ex);
        return new ErrorResponse(ex.getMessage(), NOT_FOUND.value());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex) {
        logger.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        return new ErrorResponse(ex.getMessage(), INTERNAL_SERVER_ERROR.value());
    }
}
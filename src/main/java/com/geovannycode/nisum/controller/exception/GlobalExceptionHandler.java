package com.geovannycode.nisum.controller.exception;

import com.geovannycode.nisum.domain.EmailAlreadyExistsException;
import com.geovannycode.nisum.domain.InvalidPasswordFormatException;
import com.geovannycode.nisum.domain.UserRetrievalException;
import com.geovannycode.nisum.domain.model.ApiError;
import com.geovannycode.nisum.domain.model.CreateUserResponse;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {EmailAlreadyExistsException.class})
    public ResponseEntity<CreateUserResponse<Object>> handleEmailAlreadyExistsException(
            EmailAlreadyExistsException ex) {
        return new ResponseEntity<>(buildResponseException(ex), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {InvalidPasswordFormatException.class})
    public ResponseEntity<CreateUserResponse<Object>> handleInvalidPasswordFormatException(
            InvalidPasswordFormatException ex) {
        return new ResponseEntity<>(buildResponseValidException(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<CreateUserResponse<Object>> handleException(Exception ex) {
        return new ResponseEntity<>(buildResponseException(ex, "Algo salió mal"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {UserRetrievalException.class})
    public ResponseEntity<CreateUserResponse<Object>> handleUserRetrieveException(Exception ex) {
        return new ResponseEntity<>(
                buildResponseException(ex, "Error al obtener todos los usuarios"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CreateUserResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return new ResponseEntity<>(buildResponseValidException(ex), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<CreateUserResponse<Object>> handleHttpServerErrorException(HttpServerErrorException ex) {
        return new ResponseEntity<>(buildResponseException(ex, ex.getMessage()), ex.getStatusCode());
    }

    private CreateUserResponse<Object> buildResponseException(Throwable throwable, String message) {
        CreateUserResponse<Object> response = new CreateUserResponse<>();
        ApiError error = new ApiError();
        error.setCauseMessage(throwable.getMessage());
        error.setMessage(message);
        response.setError(error);
        response.setMessage(message);
        return response;
    }

    private CreateUserResponse<Object> buildResponseValidException(MethodArgumentNotValidException ex) {
        CreateUserResponse<Object> response = new CreateUserResponse<>();
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        response.setMessage(errors);
        return response;
    }

    private CreateUserResponse<Object> buildResponseValidException(String message) {
        CreateUserResponse<Object> response = new CreateUserResponse<>();
        response.setMessage(message);
        return response;
    }

    private CreateUserResponse<Object> buildResponseException(EmailAlreadyExistsException ex) {
        CreateUserResponse<Object> response = new CreateUserResponse<>();
        response.setMessage(ex.getMessage());
        return response;
    }
}

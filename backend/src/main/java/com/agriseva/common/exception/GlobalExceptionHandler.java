package com.agriseva.common.exception;

import com.agriseva.auth.exception.InvalidCredentialsException;
import com.agriseva.equipment.exception.EquipmentAccessDeniedException;
import com.agriseva.equipment.exception.EquipmentNotFoundException;
import com.agriseva.equipment.exception.EquipmentOwnerRoleRequiredException;
import com.agriseva.user.exception.EmailAlreadyExistsException;
import com.agriseva.user.exception.PhoneNumberAlreadyExistsException;
import com.agriseva.user.exception.RoleNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import com.agriseva.rental.exception.EquipmentUnavailableForRentalException;
import com.agriseva.rental.exception.InvalidRentalDatesException;
import com.agriseva.rental.exception.InvalidRentalStatusException;
import com.agriseva.rental.exception.RentalNotFoundException;

import com.agriseva.product.exception.ProductAccessDeniedException;
import com.agriseva.product.exception.ProductNotFoundException;
import com.agriseva.product.exception.ProductSellerRoleRequiredException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            EmailAlreadyExistsException.class,
            PhoneNumberAlreadyExistsException.class
    })
    public ResponseEntity<ApiErrorResponse> handleConflict(
            RuntimeException exception,
            HttpServletRequest request
    ) {
        ApiErrorResponse response = buildErrorResponse(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                request.getRequestURI(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleRoleNotFound(
            RoleNotFoundException exception,
            HttpServletRequest request
    ) {
        ApiErrorResponse response = buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getMessage(),
                request.getRequestURI(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidCredentials(
            InvalidCredentialsException exception,
            HttpServletRequest request
    ) {
        ApiErrorResponse response = buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                exception.getMessage(),
                request.getRequestURI(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    @ExceptionHandler(EquipmentNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleEquipmentNotFound(
            EquipmentNotFoundException exception,
            HttpServletRequest request
    ) {
        ApiErrorResponse response = buildErrorResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request.getRequestURI(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler({
            EquipmentAccessDeniedException.class,
            EquipmentOwnerRoleRequiredException.class
    })
    public ResponseEntity<ApiErrorResponse> handleEquipmentForbidden(
            RuntimeException exception,
            HttpServletRequest request
    ) {
        ApiErrorResponse response = buildErrorResponse(
                HttpStatus.FORBIDDEN,
                exception.getMessage(),
                request.getRequestURI(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }
    @ExceptionHandler(RentalNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleRentalNotFound(
            RentalNotFoundException exception,
            HttpServletRequest request
    ) {
        ApiErrorResponse response = buildErrorResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request.getRequestURI(),
                null
        );
    
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }
    
    @ExceptionHandler({
            InvalidRentalDatesException.class,
            InvalidRentalStatusException.class,
            EquipmentUnavailableForRentalException.class
    })
    public ResponseEntity<ApiErrorResponse> handleRentalBadRequest(
            RuntimeException exception,
            HttpServletRequest request
    ) {
        ApiErrorResponse response = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request.getRequestURI(),
                null
        );
    
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleProductNotFound(
            ProductNotFoundException exception,
            HttpServletRequest request
    ) {
        ApiErrorResponse response = buildErrorResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request.getRequestURI(),
                null
        );
    
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }
    
    @ExceptionHandler({
            ProductAccessDeniedException.class,
            ProductSellerRoleRequiredException.class
    })
    public ResponseEntity<ApiErrorResponse> handleProductForbidden(
            RuntimeException exception,
            HttpServletRequest request
    ) {
        ApiErrorResponse response = buildErrorResponse(
                HttpStatus.FORBIDDEN,
                exception.getMessage(),
                request.getRequestURI(),
                null
        );
    
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();

        for (FieldError fieldError : exception
                .getBindingResult()
                .getFieldErrors()) {

            fieldErrors.putIfAbsent(
                    fieldError.getField(),
                    fieldError.getDefaultMessage()
            );
        }

        ApiErrorResponse response = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Request validation failed",
                request.getRequestURI(),
                fieldErrors
        );

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    private ApiErrorResponse buildErrorResponse(
            HttpStatus status,
            String message,
            String path,
            Map<String, String> fieldErrors
    ) {
        return ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .fieldErrors(fieldErrors)
                .build();
    }
}
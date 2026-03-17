package com.ahmedhassan.getthebook.exceptions;

import com.ahmedhassan.getthebook.dtos.responses.ErrorResponse;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintDeclarationException;
import jakarta.validation.ConstraintDefinitionException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	private @NonNull ResponseEntity<ErrorResponse> buildErrorResponseEntity(
			@NonNull HttpStatus status,
			@NonNull Exception ex,
			@NonNull HttpServletRequest request) {
		var error = ErrorResponse
				.builder()
				.timeStamp(Instant.now())
				.status(status.value())
				.error(status.getReasonPhrase())
				.message(ex.getMessage())
				.path(request.getRequestURI())
				.build();
		log.error("An exception occurred while processing the request", ex);
		return ResponseEntity.status(status).body(error);
	}

	// Entity not found in database
	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleEntityNotFoundException(
			@NonNull EntityNotFoundException ex,
			@NonNull HttpServletRequest request) {
		return buildErrorResponseEntity(HttpStatus.NOT_FOUND, ex, request);
	}

	// Invalid username or password during authentication
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleBadCredentialsException(
			@NonNull BadCredentialsException ex,
			@NonNull HttpServletRequest request) {
		return buildErrorResponseEntity(HttpStatus.UNAUTHORIZED, ex, request);
	}

	// Request body validation failed (@Valid DTO)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handlerMethodArgumentNotValidException(
			@NonNull MethodArgumentNotValidException ex,
			@NonNull HttpServletRequest request) {

		Map<String, String> errors = new HashMap<>();

		ex.getBindingResult()
				.getAllErrors()
				.forEach(error -> {
					var fieldName = (error instanceof FieldError fe) ? fe.getField() : error.getObjectName();
					var message = error.getDefaultMessage();
					errors.put(fieldName, message);
				});

		var status = HttpStatus.BAD_REQUEST;

		var error = ErrorResponse
				.builder()
				.timeStamp(Instant.now())
				.status(status.value())
				.error(status.getReasonPhrase())
				.message("Validation failed")
				.path(request.getRequestURI())
				.details(errors)
				.build();
		log.error("An exception occurred while processing the request", ex);
		return ResponseEntity.status(status).body(error);
	}

	// Constraint validation failed on request parameters / path variables
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolationExceptionException(
			@NonNull ConstraintViolationException ex,
			@NonNull HttpServletRequest request) {
		return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, ex, request);
	}

	// Incorrect constraint declaration in validation annotations
	@ExceptionHandler(ConstraintDeclarationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintDeclarationException(
			@NonNull ConstraintDeclarationException ex,
			@NonNull HttpServletRequest request) {
		return buildErrorResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR,
				new RuntimeException("An unexpected error occurred on our side. Please try again later"),
				request);
	}

	// Invalid constraint definition configuration
	@ExceptionHandler(ConstraintDefinitionException.class)
	public ResponseEntity<ErrorResponse> handleConstraintDefinitionException(
			@NonNull ConstraintDefinitionException ex,
			@NonNull HttpServletRequest request) {
		return buildErrorResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR,
				new RuntimeException("An unexpected error occurred on our side. Please try again later"),
				request);
	}

	// Binding error when request parameters cannot be mapped to object fields
	@ExceptionHandler(BindException.class)
	public ResponseEntity<ErrorResponse> handleBindException(
			@NonNull BindException ex,
			@NonNull HttpServletRequest request) {
		return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, ex, request);
	}

	// JSON parsing error or malformed request body
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
			@NonNull HttpMessageNotReadableException ex,
			@NonNull HttpServletRequest request) {
		var status = HttpStatus.BAD_REQUEST;
		var error = ErrorResponse
				.builder()
				.timeStamp(Instant.now())
				.status(status.value())
				.error(status.getReasonPhrase())
				.message("Unable to process request. Kindly provide a valid request")
				.path(request.getRequestURI())
				.build();
		log.error("An exception occurred while processing the request", ex);
		return ResponseEntity.status(status).body(error);
	}

	// Error while serializing response body
	@ExceptionHandler(HttpMessageNotWritableException.class)
	public ResponseEntity<ErrorResponse> handleHttpMessageNotWritableException(
			@NonNull HttpMessageNotWritableException ex,
			@NonNull HttpServletRequest request) {
		var status = HttpStatus.INTERNAL_SERVER_ERROR;
		var error = ErrorResponse
				.builder()
				.timeStamp(Instant.now())
				.status(status.value())
				.error(status.getReasonPhrase())
				.message("An unexpected error occurred on our side. Please try again later")
				.path(request.getRequestURI())
				.build();
		log.error("An exception occurred while processing the request", ex);
		return ResponseEntity.status(status).body(error);
	}

	// Required request parameter missing
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
			@NonNull MissingServletRequestParameterException ex,
			@NonNull HttpServletRequest request) {
		return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, ex, request);
	}

	// Path variable missing in request mapping
	@ExceptionHandler(MissingPathVariableException.class)
	public ResponseEntity<ErrorResponse> handleMissingPathVariableException(
			@NonNull MissingPathVariableException ex,
			@NonNull HttpServletRequest request) {
		return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, ex, request);
	}

	// Type mismatch (e.g. String passed where Long expected)
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
			@NonNull MethodArgumentTypeMismatchException ex,
			@NonNull HttpServletRequest request) {
		return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, ex, request);
	}

	// HTTP method not supported for endpoint
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
			@NonNull HttpRequestMethodNotSupportedException ex,
			@NonNull HttpServletRequest request) {
		return buildErrorResponseEntity(HttpStatus.METHOD_NOT_ALLOWED, ex, request);
	}

	// Unsupported content type (e.g. XML sent when JSON expected)
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupportedException(
			@NonNull HttpMediaTypeNotSupportedException ex,
			@NonNull HttpServletRequest request) {
		return buildErrorResponseEntity(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex, request);
	}

	// Requested response media type not acceptable
	@ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
	public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotAcceptableException(
			@NonNull HttpMediaTypeNotAcceptableException ex,
			@NonNull HttpServletRequest request) {
		return buildErrorResponseEntity(HttpStatus.NOT_ACCEPTABLE, ex, request);
	}

	// Database constraint violation (unique key, foreign key, etc.)
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
			@NonNull DataIntegrityViolationException ex,
			@NonNull HttpServletRequest request) {
		return buildErrorResponseEntity(HttpStatus.CONFLICT,
				new RuntimeException("A data conflict occurred. Please check your input"), request);
	}

	// Access denied due to insufficient permissions
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDeniedException(
			@NonNull AccessDeniedException ex,
			@NonNull HttpServletRequest request) {
		return buildErrorResponseEntity(HttpStatus.FORBIDDEN, ex, request);
	}

	@ExceptionHandler(JwtException.class)
	public ResponseEntity<ErrorResponse> handleJwtGenericException(
			@NonNull JwtException ex,
			@NonNull HttpServletRequest request) {
		return buildErrorResponseEntity(HttpStatus.UNAUTHORIZED, ex, request);
	}

	// Generic authentication failure
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ErrorResponse> handleAuthenticationException(
			@NonNull AuthenticationException ex,
			@NonNull HttpServletRequest request) {
		return buildErrorResponseEntity(HttpStatus.UNAUTHORIZED, ex, request);
	}

	// Illegal argument passed to method
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
			@NonNull IllegalArgumentException ex,
			@NonNull HttpServletRequest request) {
		return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, ex, request);
	}

	// Illegal application state encountered
	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<ErrorResponse> handleIllegalStateException(
			@NonNull IllegalStateException ex,
			@NonNull HttpServletRequest request) {
		return buildErrorResponseEntity(HttpStatus.CONFLICT, ex, request);
	}

	// File upload exceeded configured size limit
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(
			@NonNull MaxUploadSizeExceededException ex,
			@NonNull HttpServletRequest request) {
		return buildErrorResponseEntity(HttpStatus.CONTENT_TOO_LARGE, ex, request);
	}

	// IO failure during file operations
	@ExceptionHandler(IOException.class)
	public ResponseEntity<ErrorResponse> handleIOException(
			@NonNull IOException ex,
			@NonNull HttpServletRequest request) {
		return buildErrorResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR,
				new RuntimeException("An unexpected error occurred on our side. Please try again later"),
				request);
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(
			@NonNull NoHandlerFoundException ex,
			@NonNull HttpServletRequest request) {
		return buildErrorResponseEntity(HttpStatus.NOT_FOUND, ex, request);
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ErrorResponse> handleNoResourceFoundException(
			@NonNull NoResourceFoundException ex,
			@NonNull HttpServletRequest request) {
		return buildErrorResponseEntity(HttpStatus.NOT_FOUND, ex, request);
	}

	@ExceptionHandler(RoleNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleRoleNotFoundException(
			@NonNull RoleNotFoundException ex,
			@NonNull HttpServletRequest request) {
		return buildErrorResponseEntity(HttpStatus.NOT_FOUND, ex, request);
	}

	@ExceptionHandler(BookNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleBookNotFoundException(
			@NonNull BookNotFoundException ex,
			@NonNull HttpServletRequest request) {
		return buildErrorResponseEntity(HttpStatus.NOT_FOUND, ex, request);
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleUserNotFoundException(
			@NonNull UserNotFoundException ex,
			@NonNull HttpServletRequest request) {
		return buildErrorResponseEntity(HttpStatus.NOT_FOUND, ex, request);
	}

	@ExceptionHandler(BookArchivedException.class)
	public ResponseEntity<ErrorResponse> handleBookArchivedException(
			@NonNull BookArchivedException ex,
			@NonNull HttpServletRequest request) {
		return buildErrorResponseEntity(HttpStatus.CONFLICT, ex, request);
	}

	@ExceptionHandler(BookNotShareableException.class)
	public ResponseEntity<ErrorResponse> handleBookNotShareableException(
			@NonNull BookNotShareableException ex,
			@NonNull HttpServletRequest request) {
		return buildErrorResponseEntity(HttpStatus.CONFLICT, ex, request);
	}

	@ExceptionHandler(BookAlreadyBorrowedException.class)
	public ResponseEntity<ErrorResponse> handleBookAlreadyBorrowedException(
			@NonNull BookAlreadyBorrowedException ex,
			@NonNull HttpServletRequest request) {
		return buildErrorResponseEntity(HttpStatus.CONFLICT, ex, request);
	}

	@ExceptionHandler(BookShareAppealAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> handleBookShareAppealAlreadyExistsException(
			@NonNull BookShareAppealAlreadyExistsException ex,
			@NonNull HttpServletRequest request) {
		return buildErrorResponseEntity(HttpStatus.CONFLICT, ex, request);
	}

	@ExceptionHandler(BookShareAppealNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleBookShareAppealNotFoundException(
			@NonNull BookShareAppealNotFoundException ex,
			@NonNull HttpServletRequest request) {
		return buildErrorResponseEntity(HttpStatus.NOT_FOUND, ex, request);
	}

	@ExceptionHandler(AuthorizationDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(
			@NonNull AuthorizationDeniedException ex,
			@NonNull HttpServletRequest request) {
		return buildErrorResponseEntity(HttpStatus.UNAUTHORIZED, ex, request);
	}

	@ExceptionHandler(BookShareAppealAlreadyApproved.class)
	public ResponseEntity<ErrorResponse> handleBookShareAppealAlreadyApprovedException(
			@NonNull BookShareAppealAlreadyApproved ex,
			@NonNull HttpServletRequest request) {
		return buildErrorResponseEntity(HttpStatus.CONFLICT, ex, request);
	}

	@ExceptionHandler(BookShareRecordNotFound.class)
	public ResponseEntity<ErrorResponse> handleBookShareRecordNotFoundException(
			@NonNull BookShareRecordNotFound ex,
			@NonNull HttpServletRequest request) {
		return buildErrorResponseEntity(HttpStatus.NOT_FOUND, ex, request);
	}

	// Catch-all fallback for any unhandled exception
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(
			@NonNull Exception ex,
			@NonNull HttpServletRequest request) {
		return buildErrorResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR,
				new RuntimeException("An unexpected error occurred on our side. Please try again later"),
				request);
	}

}
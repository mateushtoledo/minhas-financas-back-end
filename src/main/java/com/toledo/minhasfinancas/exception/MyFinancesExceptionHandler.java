package com.toledo.minhasfinancas.exception;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.toledo.minhasfinancas.exception.custom.BusinessAuthorizationException;
import com.toledo.minhasfinancas.exception.custom.BusinessRuleException;
import com.toledo.minhasfinancas.exception.custom.UserNotFoundException;

@ControllerAdvice
public class MyFinancesExceptionHandler {
	
	@ExceptionHandler({ HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class })
	public ResponseEntity<ErrorResponse> handleNotReadableMessage(Exception ex, HttpServletRequest request) {
		// Define the error details
		String errorMessage = ex.getMessage();
		if (errorMessage.startsWith("Required request body is missing")) {
			errorMessage = "O corpo da requisição foi omitido!";
		} else if (errorMessage.startsWith("JSON parse error")) {
			errorMessage = "Por favor, verifique a tipagem de todos os campos, e tente novamente!";
		} else if (errorMessage.startsWith("Failed to convert value of type")) {
			errorMessage = "Por favor, verifique a tipagem de todos os parâmetros, e tente novamente!";
		}

		// Build and return error response
		ErrorResponse errorResponse = new ErrorResponse(errorMessage);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}
	
	@ExceptionHandler(BusinessRuleException.class)
	public ResponseEntity<ErrorResponse> handleBusinessRuleException(BusinessRuleException ex, HttpServletRequest request) {
		ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}
	
	@ExceptionHandler(BusinessAuthorizationException.class)
	public ResponseEntity<ErrorResponse> handleBusinessAuthorizationException(BusinessAuthorizationException ex, HttpServletRequest request) {
		ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
	}
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex, HttpServletRequest request) {
		ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}
}

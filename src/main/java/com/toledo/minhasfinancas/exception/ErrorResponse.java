package com.toledo.minhasfinancas.exception;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorResponse implements Serializable {
	private static final long serialVersionUID = 3934337864045707784L;
	
	private String timestamp;
	private List<Error> errors;
	
	public ErrorResponse(String errorMessage) {
		this.errors = new ArrayList<>();
		this.errors.add(new Error(errorMessage));
		this.timestamp = buildTimestamp();
	}
	
	public ErrorResponse(List<Error> errors) {
		this.errors = errors;
		this.timestamp = buildTimestamp();
	}
	
	private String buildTimestamp() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss");
		return formatter.format(LocalDateTime.now());
	}
}
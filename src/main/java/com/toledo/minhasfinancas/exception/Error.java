package com.toledo.minhasfinancas.exception;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Error implements Serializable {
	private static final long serialVersionUID = -557352511534405761L;
	
	private String message;
}

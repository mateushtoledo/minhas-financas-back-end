package com.toledo.minhasfinancas.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthenticationDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String jwt;
	private Long expiresIn;
}

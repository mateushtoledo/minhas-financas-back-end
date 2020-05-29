package com.toledo.minhasfinancas.dto;


import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.toledo.minhasfinancas.domain.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO implements Serializable {
	private static final long serialVersionUID = 1955578837123252557L;
	
	private Long id;
	private String email;
	private String name;
	private LocalDate registerDate;
	private String password;
	
	public User toUser() {
		return User.builder()
				.name(name)
				.email(email)
				.password(password)
				.build();
	}
	
	public UserDTO(User u) {
		this.id = u.getId();
		this.email = u.getEmail();
		this.name = u.getName();
		this.registerDate = u.getRegisterDate();
		this.password = u.getPassword();
	}
}

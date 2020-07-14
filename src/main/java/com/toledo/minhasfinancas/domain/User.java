package com.toledo.minhasfinancas.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users", schema = "financas")
public class User implements Serializable {
	private static final long serialVersionUID = -4405955818282661447L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull(message = "Por favor, informe seu nome!")
	@Size(min=5, message="O nome informado é muito curto!")
	private String name;
	
	@NotNull(message = "Por favor, informe seu e-mail!")
	@Email(message="O e-mail informado é inválido!")
	private String email;
	
	@NotNull(message = "Por favor, informe sua senha!")
	@Size(min=8, message="Por favor, informe uma senha com 8 ou mais dígitos!")
	private String password;
	
	@Column(name = "register_date")
	@Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
	private LocalDate registerDate;
	
	@Builder.Default
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.REMOVE)
	private List<FinancialRecord> financialRecords = new ArrayList<>();
}

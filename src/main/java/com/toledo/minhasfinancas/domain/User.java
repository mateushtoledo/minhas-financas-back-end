package com.toledo.minhasfinancas.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

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
	
	private String name;
	private String email;
	private String password;
	
	@Column(name = "register_date")
	@Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
	private LocalDate registerDate;
	
	@Builder.Default
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	private List<FinancialRecord> financialRecords = new ArrayList<>();
}

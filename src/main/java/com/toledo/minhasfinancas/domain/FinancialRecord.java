package com.toledo.minhasfinancas.domain;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import com.toledo.minhasfinancas.domain.enums.FinancialRecordStatus;
import com.toledo.minhasfinancas.domain.enums.FinancialRecordType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "financial_records", schema = "financas")
public class FinancialRecord implements Serializable {
	private static final long serialVersionUID = 8876045349253142520L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull(message = "A descrição do registro financeiro é obrigatória, e foi omitida!")
	@NotEmpty(message = "A descrição do registro financeiro está vazia!")
	private String description;
	
	@NotNull(message = "O mês de refereência do registro financeiro é obrigatório, e foi omitido!")
	@Min(value = 1, message = "O mês de referência do registro financeiro aceita somente valores de 1 a 12!")
	@Max(value = 12, message = "O mês de referência do registro financeiro aceita somente valores de 1 a 12!")
	private Integer month;
	
	@Min(value = 1000, message = "O ano de referência do registro financeiro é inválido!")
	@Max(value = 9999, message = "O ano de referência do registro financeiro é inválido!")
	@NotNull(message = "O ano de refereência do registro financeiro é obrigatório, e foi omitido!")
	private Integer year;
	
	@NotNull(message = "O valor do registro financeiro é obrigatório, e foi omitido!")
	@Min(value = 1, message = "O valor do registro financeiro deve ser superior a zero!")
	private Float value;
	
	@NotNull(message = "O tipo do registro financeiro é obrigatório, e foi omitido!")
	@Enumerated(EnumType.STRING)
	private FinancialRecordType type;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	private FinancialRecordStatus status = FinancialRecordStatus.PENDANT;
	
	@ManyToOne(optional = false, cascade = CascadeType.REMOVE)
	@JoinColumn(name = "fk_user_id", columnDefinition = "BIGINT")
	private User user;
	
	@Column(name = "register_date")
	@Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
	private LocalDate registerDate;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + month;
		result = prime * result + ((registerDate == null) ? 0 : registerDate.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((user == null) ? 0 : user.getId().hashCode());
		result = prime * result + Float.floatToIntBits(value);
		return prime * result + year;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FinancialRecord other = (FinancialRecord) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (month != other.month)
			return false;
		if (registerDate == null) {
			if (other.registerDate != null)
				return false;
		} else if (!registerDate.equals(other.registerDate))
			return false;
		if (status != other.status)
			return false;
		if (type != other.type)
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.getId().equals(other.user.getId()))
			return false;
		if (Float.floatToIntBits(value) != Float.floatToIntBits(other.value))
			return false;
		return year == other.year;
	}
}

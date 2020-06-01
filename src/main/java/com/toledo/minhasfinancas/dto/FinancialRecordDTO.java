package com.toledo.minhasfinancas.dto;

import java.io.Serializable;

import com.toledo.minhasfinancas.domain.FinancialRecord;
import com.toledo.minhasfinancas.domain.User;
import com.toledo.minhasfinancas.domain.enums.FinancialRecordStatus;
import com.toledo.minhasfinancas.domain.enums.FinancialRecordType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialRecordDTO implements Serializable {
	private static final long serialVersionUID = -4962637323878580507L;
	
	private Long id, userId;
	private Float value;
	private Integer month, year;
	private String description, type, status;
	
	public FinancialRecordDTO(FinancialRecord record) {
		this.id = record.getId();
		this.userId = record.getUser() == null? null : record.getUser().getId();
		this.value = record.getValue();
		this.month = record.getMonth();
		this.year = record.getYear();
		this.description = record.getDescription();
		this.type = record.getType().name();
		this.status = record.getStatus().name();
	}
	
	public FinancialRecord toRecord() {
		FinancialRecord record =  FinancialRecord.builder()
			.id(id)
			.description(description)
			.value(value)
			.month(month)
			.year(year)
			.user(User.builder().id(id).build())
			.build();
		
		if (type != null && !type.trim().isEmpty()) {
			try {
				record.setType(FinancialRecordType.valueOf(type.toUpperCase()));
			} catch (Exception e) {
				
			}
		}
		
		if (status != null && !status.trim().isEmpty()) {
			try {
				record.setStatus(FinancialRecordStatus.valueOf(status.toUpperCase()));
			} catch (Exception e) {
				
			}
		}
		
		return record;
	}
}

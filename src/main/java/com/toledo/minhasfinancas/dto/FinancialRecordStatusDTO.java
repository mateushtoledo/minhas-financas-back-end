package com.toledo.minhasfinancas.dto;

import java.io.Serializable;

import com.toledo.minhasfinancas.domain.enums.FinancialRecordStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialRecordStatusDTO implements Serializable {
	private static final long serialVersionUID = 3990578190935079022L;
	
	private FinancialRecordStatus status;
}

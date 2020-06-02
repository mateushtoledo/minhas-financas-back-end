package com.toledo.minhasfinancas.dto;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PageDTO<T> implements Serializable {
	private static final long serialVersionUID = -5576889948091721807L;
	
	private List<T> items;
	private int pageIndex, pageSize, pageItems, totalPages;
	private long totalItems;
	
	public PageDTO(Page<T> page) {
		this.items = page.getContent();
		this.pageIndex = page.getNumber() + 1;
		this.pageSize = page.getSize();
		this.pageItems = page.getNumberOfElements();
		this.totalItems = page.getTotalElements();
		this.totalPages = page.getTotalPages();
	}
}

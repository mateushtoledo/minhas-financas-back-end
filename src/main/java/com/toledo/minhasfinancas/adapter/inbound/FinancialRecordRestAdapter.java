package com.toledo.minhasfinancas.adapter.inbound;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.toledo.minhasfinancas.domain.FinancialRecord;
import com.toledo.minhasfinancas.domain.User;
import com.toledo.minhasfinancas.domain.enums.FinancialRecordType;
import com.toledo.minhasfinancas.dto.FinancialRecordDTO;
import com.toledo.minhasfinancas.dto.FinancialRecordStatusDTO;
import com.toledo.minhasfinancas.port.inbound.FinancialRecordServicePort;
import com.toledo.minhasfinancas.port.inbound.UserServicePort;
import com.toledo.minhasfinancas.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/financial-records")
public class FinancialRecordRestAdapter {
	
	private final FinancialRecordServicePort service;
	private final UserServicePort userService;
	private final JwtUtil jwtUtil;
	
	@PostMapping
	public ResponseEntity<Void> saveFinancialRecord(
		@RequestBody FinancialRecordDTO recordData,
		@RequestHeader("authorization") String authorization
	) {
		FinancialRecord saved = service.save(getUser(authorization), recordData.toRecord());
		
		URI recordLocation = ServletUriComponentsBuilder.fromCurrentRequest()
			.path("/{id}")
			.buildAndExpand(saved.getId())
			.toUri();
		
		return ResponseEntity.created(recordLocation).build();
	}
	
	@GetMapping
	public ResponseEntity<List<FinancialRecordDTO>> findFinancialRecords(
		@RequestParam(name = "description", required = false) String description,
		@RequestParam(name = "month", required = false) Integer month,
		@RequestParam(name = "year", required = false) Integer year,
		@RequestParam(name = "type", required = false) FinancialRecordType type,
		@RequestHeader("authorization") String authorization
	) {
		List<FinancialRecord> records = service.find(getUser(authorization), description, year, month, type);
		List<FinancialRecordDTO> dtos = records.stream().map(record ->  new FinancialRecordDTO(record)).collect(Collectors.toList());
		return ResponseEntity.ok(dtos);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Void> updateFinancialRecord(
		@PathVariable("id") Long recordId,
		@RequestBody FinancialRecordDTO recordData,
		@RequestHeader("authorization") String authorization
	) {
		service.update(getUser(authorization), recordId, recordData.toRecord());
		return ResponseEntity.noContent().build();
	}
	
	@PatchMapping("/{id}/status")
	public ResponseEntity<Void> updateFinancialRecordStatus(
		@PathVariable("id") Long recordId,
		@RequestBody FinancialRecordStatusDTO statusDto,
		@RequestHeader("authorization") String authorization
	) {
		service.updateStatus(getUser(authorization), recordId, statusDto.getStatus());
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteFinancialRecord(
		@PathVariable("id") Long recordId,
		@RequestHeader("authorization") String authorization
	) {
		service.delete(getUser(authorization), recordId);
		return ResponseEntity.noContent().build();
	}
	
	private User getUser(String authorizationHeader) {
		// Get user requester email from Authorization header
		String actualJwt = authorizationHeader.substring(7);
		String userEmail = jwtUtil.getUserEmail(actualJwt);
		
		// Load user
		return userService.findByEmail(userEmail);
	}
}

package com.toledo.minhasfinancas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.toledo.minhasfinancas.domain.FinancialRecord;
import com.toledo.minhasfinancas.domain.enums.FinancialRecordType;

@Repository
@Transactional(readOnly = true)
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {
	
	@Query("SELECT SUM(fr.value) FROM FinancialRecord fr RIGHT JOIN User u ON fr.user.id = u.id WHERE u.id = :userId AND fr.type = :type AND fr.status = 'ACCEPTED'")
	public Float getSumByUserAndStatus(@Param("userId") Long userId, @Param("type") FinancialRecordType type);
	
}

package com.campa.mis.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.campa.mis.entity.BankDetailsEntity;

public interface BankDetailRepository extends JpaRepository<BankDetailsEntity, Integer> {

	Optional<BankDetailsEntity> findByAccountNumberAndIfscCode(String accountNumber, String ifscCode);

	Optional<BankDetailsEntity> findBySchemeId(Long schemeId);

	boolean existsBySchemeId(Long schemeId);

}

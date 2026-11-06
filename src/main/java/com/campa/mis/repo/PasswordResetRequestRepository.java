package com.campa.mis.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.campa.mis.entity.PasswordResetRequestEntity;

import jakarta.persistence.LockModeType;

@Repository
public interface PasswordResetRequestRepository extends JpaRepository<PasswordResetRequestEntity, Long> {

	// Invalidate all previous active reset requests of a user
	@Modifying
	@Query("""
	    UPDATE PasswordResetRequestEntity p
	    SET p.consumedAt = CURRENT_TIMESTAMP
	    WHERE p.userId = :userId
	      AND p.consumedAt IS NULL
	""")
	int invalidatePreviousRequests(@Param("userId") Long userId);

	// Fetch the latest active password reset request
	@Query(value = """
			SELECT p.*
			FROM mis_scheme.password_reset_requests p
			INNER JOIN mis_scheme.user_accounts u
			    ON u.user_id = p.user_id
			WHERE LOWER(u.email) = LOWER(:email)
			  AND p.consumed_at IS NULL
			ORDER BY p.created_at DESC
			LIMIT 1
			""", nativeQuery = true)
	Optional<PasswordResetRequestEntity> findLatestActiveRequest(@Param("email") String email);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<PasswordResetRequestEntity> findByResetTokenHash(String resetTokenHash);

}
package com.campa.mis.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.campa.mis.dto.response.UserAccountProjection;
import com.campa.mis.entity.UserAccountEntity;

import jakarta.transaction.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccountEntity, Long> {

	Optional<UserAccountEntity> findByEmailIgnoreCase(String email);
	
	boolean existsByEmailIgnoreCase(String email);

	@Query(value = """
			SELECT
			    ua.user_id AS "userId",
			    ua.email AS "email",
			    ua.password_hash AS "passwordHash",
			    ua.account_status AS "accountStatus",
			    ur.role_code AS "roleCode",
			    ua.active_token_id AS "activeTokenId",
			    ua.session_expires_at AS "sessionExpiresAt"
			FROM mis_scheme.user_accounts ua
			JOIN mis_scheme.user_roles ur
			    ON ua.role_id = ur.role_id
			WHERE LOWER(ua.email) = LOWER(:email)
			  AND ur.is_active = true
			""", nativeQuery = true)
	Optional<UserAccountProjection> findUserForAuthentication(@Param("email") String email);

	@Modifying
	@Query(value = """
			UPDATE mis_scheme.user_accounts
			SET
			    active_token_id = :tokenId,
			    session_expires_at = :expiresAt,
			    last_login_at = :lastLoginAt
			WHERE user_id = :userId
			""", nativeQuery = true)
	int acquireSession(@Param("userId") Long userId, @Param("tokenId") String tokenId,
			@Param("expiresAt") OffsetDateTime expiresAt, @Param("lastLoginAt") OffsetDateTime lastLoginAt);

	@Query(value = """
			SELECT active_token_id
			FROM mis_scheme.user_accounts
			WHERE LOWER(email) = LOWER(:email)
			""", nativeQuery = true)
	Optional<String> findActiveTokenIdByEmail(@Param("email") String email);

	@Modifying
	@Query(value = """
			UPDATE mis_scheme.user_accounts
			SET active_token_id = :tokenId,
			    session_expires_at = :expiresAt,
			    last_login_at = CURRENT_TIMESTAMP
			WHERE user_id = :userId
			""", nativeQuery = true)
	int updateActiveSession(@Param("userId") Long userId, @Param("tokenId") String tokenId,
			@Param("expiresAt") OffsetDateTime expiresAt);

	@Modifying
	@Query(value = """
			UPDATE mis_scheme.user_accounts
			SET active_token_id = NULL,
			    session_expires_at = NULL
			WHERE user_id = :userId
			AND active_token_id = :tokenId
			""", nativeQuery = true)
	int clearActiveSession(@Param("userId") Long userId, @Param("tokenId") String tokenId);

}
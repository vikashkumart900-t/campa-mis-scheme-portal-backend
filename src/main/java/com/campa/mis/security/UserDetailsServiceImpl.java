package com.campa.mis.security;

import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.campa.mis.dto.response.UserAccountProjection;
import com.campa.mis.entity.UserAccountEntity;
import com.campa.mis.entity.UserRoleEntity;
import com.campa.mis.repo.UserAccountRepository;
import com.campa.mis.repo.UserRoleRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserAccountRepository userAccountRepository;
	private final UserRoleRepository userRoleRepository;

	public UserDetailsServiceImpl(UserAccountRepository userAccountRepository, UserRoleRepository userRoleRepository) {
		this.userAccountRepository = userAccountRepository;
		this.userRoleRepository = userRoleRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) {

		UserAccountProjection userDetail = userAccountRepository.findUserForAuthentication(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

		boolean enabled = "ACTIVE".equalsIgnoreCase(userDetail.getAccountStatus());

		String roleCode = userDetail.getRoleCode();

		return User.builder().username(userDetail.getEmail()).password(userDetail.getPasswordHash())
				.authorities(List.of(new SimpleGrantedAuthority("ROLE_" + roleCode))).disabled(!enabled).build();
	}
}
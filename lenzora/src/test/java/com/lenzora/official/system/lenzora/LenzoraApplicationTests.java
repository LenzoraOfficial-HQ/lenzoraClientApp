package com.lenzora.official.system.lenzora;

import com.lenzora.official.system.lenzora.entity.AccountStatus;
import com.lenzora.official.system.lenzora.model.User;
import com.lenzora.official.system.lenzora.repository.UserRepository;
import com.lenzora.official.system.lenzora.service.OrthoptistApprovalService;
import com.lenzora.official.system.lenzora.service.OrthoptistProfileService;
import com.lenzora.official.system.lenzora.service.UserRegistrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class LenzoraApplicationTests {

	@Autowired
	private UserRegistrationService registrationService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OrthoptistProfileService profileService;

	@Autowired
	private OrthoptistApprovalService approvalService;

	@Autowired
	private DaoAuthenticationProvider authenticationProvider;

	@Test
	void contextLoads() {
	}

	@Test
	@Transactional
	void registrationPersistsPendingOrthoptistInDatabase() {
		String email = "orthoptist-" + UUID.randomUUID() + "@example.com";

		String error = registrationService.register(
				"Test", "Orthoptist", email, "securepass", "securepass");

		assertEquals("", error);
		var registeredUser = userRepository.findByEmail(email).orElseThrow();
		assertEquals(AccountStatus.PENDING, registeredUser.getAccountStatus());
		assertFalse(registeredUser.isProfileCompleted());
		var authentication = authenticationProvider.authenticate(
				UsernamePasswordAuthenticationToken.unauthenticated(email, "securepass"));
		assertEquals(true, authentication.isAuthenticated());
		Long originalUserId = registeredUser.getId();

		profileService.completeProfile(email, "ORTH-123", "0771234567", "Colombo");

		var completedUser = userRepository.findByEmail(email).orElseThrow();
		assertEquals(originalUserId, completedUser.getId());
		assertEquals("ORTH-123", completedUser.getLicenseNumber());
		assertEquals("0771234567", completedUser.getPhone());
		assertEquals("Colombo", completedUser.getBranch());
		assertEquals(true, completedUser.isProfileCompleted());
	}

	@Test
	@Transactional
	void adminApprovalPersistsAsActiveInExistingDatabaseSchema() {
		String suffix = UUID.randomUUID().toString();
		String adminEmail = "admin-" + suffix + "@example.com";
		String orthoptistEmail = "orthoptist-" + suffix + "@example.com";
		LocalDateTime now = LocalDateTime.now();

		User admin = new User();
		admin.setFirstName("Test");
		admin.setLastName("Admin");
		admin.setEmail(adminEmail);
		admin.setPasswordHash("test-password-hash");
		admin.setRole("ADMIN");
		admin.setAccountStatus(AccountStatus.ACTIVE);
		admin.setCreatedAt(now);
		admin.setUpdatedAt(now);
		userRepository.save(admin);

		assertEquals("", registrationService.register(
				"Test", "Orthoptist", orthoptistEmail, "securepass", "securepass"));
		profileService.completeProfile(orthoptistEmail, "ORTH-456", "0779876543", "Kandy");
		var orthoptist = userRepository.findByEmail(orthoptistEmail).orElseThrow();

		approvalService.approve(orthoptist.getId(), adminEmail);

		var approvedUser = userRepository.findByEmail(orthoptistEmail).orElseThrow();
		assertEquals(AccountStatus.APPROVED, approvedUser.getAccountStatus());
	}

}

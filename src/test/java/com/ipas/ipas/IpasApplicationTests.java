package com.ipas.ipas;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import com.ipas.ipas.model.service.UserService;
import com.ipas.ipas.model.entity.User;

@SpringBootTest
class IpasApplicationTests {
	@Autowired
	private com.ipas.ipas.model.service.PolicyService policyService;
	@Autowired
	private com.ipas.ipas.model.service.ClientService clientService;

	// Prueba: creación de una póliza
	@Test
	void testCreatePolicy() {
		com.ipas.ipas.model.entity.Policy policy = new com.ipas.ipas.model.entity.Policy();
		policy.setPolicyType("Vida");
		policy.setCoverage("Cobertura total");
		policy.setPremiumAmount(java.math.BigDecimal.valueOf(1000));
		policy.setCoverageAmount(java.math.BigDecimal.valueOf(50000));
		policy.setStartDate(java.time.LocalDate.now());
		policy.setEndDate(java.time.LocalDate.now().plusYears(1));
		com.ipas.ipas.model.entity.Policy saved = policyService.save(policy);
		org.junit.jupiter.api.Assertions.assertNotNull(saved.getId());
	}

	// Prueba: asignación de un cliente a una póliza
	@Test
	void testAssignClientToPolicy() {
		com.ipas.ipas.model.entity.Client client = new com.ipas.ipas.model.entity.Client();
		client.setDocumentNumber("123456789");
		client.setFirstName("Cliente");
		client.setLastName("Prueba");
		client.setEmail("clienteprueba@ipas.com");
		client.setStatus(com.ipas.ipas.model.entity.Client.ClientStatus.ACTIVE);
		com.ipas.ipas.model.entity.Client savedClient = clientService.save(client);

		com.ipas.ipas.model.entity.Policy policy = new com.ipas.ipas.model.entity.Policy();
		policy.setPolicyType("Auto");
		policy.setCoverage("Cobertura básica");
		policy.setPremiumAmount(java.math.BigDecimal.valueOf(500));
		policy.setCoverageAmount(java.math.BigDecimal.valueOf(20000));
		policy.setStartDate(java.time.LocalDate.now());
		policy.setEndDate(java.time.LocalDate.now().plusYears(1));
		policy.setClient(savedClient);
		com.ipas.ipas.model.entity.Policy savedPolicy = policyService.save(policy);
		org.junit.jupiter.api.Assertions.assertEquals(savedClient.getId(), savedPolicy.getClient().getId());
	}

	// Prueba: edición de póliza
	@Test
	void testUpdatePolicy() {
		com.ipas.ipas.model.entity.Policy policy = new com.ipas.ipas.model.entity.Policy();
		policy.setPolicyType("Hogar");
		policy.setCoverage("Cobertura contra incendios");
		policy.setPremiumAmount(java.math.BigDecimal.valueOf(800));
		policy.setCoverageAmount(java.math.BigDecimal.valueOf(30000));
		policy.setStartDate(java.time.LocalDate.now());
		policy.setEndDate(java.time.LocalDate.now().plusYears(1));
		com.ipas.ipas.model.entity.Policy saved = policyService.save(policy);
		saved.setCoverage("Cobertura contra incendios y robos");
		com.ipas.ipas.model.entity.Policy updated = policyService.update(saved);
		org.junit.jupiter.api.Assertions.assertEquals("Cobertura contra incendios y robos", updated.getCoverage());
	}

	// Prueba: eliminar póliza
	@Test
	void testDeletePolicy() {
		com.ipas.ipas.model.entity.Policy policy = new com.ipas.ipas.model.entity.Policy();
		policy.setPolicyType("Salud");
		policy.setCoverage("Cobertura médica");
		policy.setPremiumAmount(java.math.BigDecimal.valueOf(1200));
		policy.setCoverageAmount(java.math.BigDecimal.valueOf(60000));
		policy.setStartDate(java.time.LocalDate.now());
		policy.setEndDate(java.time.LocalDate.now().plusYears(1));
		com.ipas.ipas.model.entity.Policy saved = policyService.save(policy);
		Long id = saved.getId();
		policyService.deletePolicy(id);
		org.junit.jupiter.api.Assertions.assertTrue(policyService.findById(id).isEmpty());
	}
	// Prueba: findAll debe retornar una lista (puede estar vacía)
	@Test
	void testFindAllUsers() {
		java.util.List<com.ipas.ipas.model.entity.User> users = userService.findAll();
		org.junit.jupiter.api.Assertions.assertNotNull(users);
	}

	// Prueba: findById debe retornar vacío si el usuario no existe
	@Test
	void testFindByIdNotFound() {
		java.util.Optional<com.ipas.ipas.model.entity.User> userOpt = userService.findById(-1L);
		org.junit.jupiter.api.Assertions.assertTrue(userOpt.isEmpty());
	}

	// Prueba: creación de usuario y verificación de persistencia
	@Test
	void testCreateUser() {
		com.ipas.ipas.model.entity.User user = new com.ipas.ipas.model.entity.User();
		user.setEmail("testuser@ipas.com");
		user.setPassword("testpass");
		user.setFirstName("Test");
		user.setLastName("User");
		user.setRole(com.ipas.ipas.model.entity.User.UserRole.REGISTRADO);
		user.setStatus(com.ipas.ipas.model.entity.User.UserStatus.ACTIVE);
		com.ipas.ipas.model.entity.User saved = userService.save(user);
		org.junit.jupiter.api.Assertions.assertNotNull(saved.getId());
		org.junit.jupiter.api.Assertions.assertEquals("testuser@ipas.com", saved.getEmail());
	}

	// Prueba: actualización de datos de usuario
	@Test
	void testUpdateUser() {
		com.ipas.ipas.model.entity.User user = new com.ipas.ipas.model.entity.User();
		user.setEmail("updateuser@ipas.com");
		user.setPassword("updatepass");
		user.setFirstName("Update");
		user.setLastName("User");
		user.setRole(com.ipas.ipas.model.entity.User.UserRole.REGISTRADO);
		user.setStatus(com.ipas.ipas.model.entity.User.UserStatus.ACTIVE);
		com.ipas.ipas.model.entity.User saved = userService.save(user);
		saved.setFirstName("UpdatedName");
		com.ipas.ipas.model.entity.User updated = userService.update(saved);
		org.junit.jupiter.api.Assertions.assertEquals("UpdatedName", updated.getFirstName());
	}

	// Prueba: eliminar usuario y verificar que no existe
	@Test
	void testDeleteUser() {
		com.ipas.ipas.model.entity.User user = new com.ipas.ipas.model.entity.User();
		user.setEmail("deleteuser@ipas.com");
		user.setPassword("deletepass");
		user.setFirstName("Delete");
		user.setLastName("User");
		user.setRole(com.ipas.ipas.model.entity.User.UserRole.REGISTRADO);
		user.setStatus(com.ipas.ipas.model.entity.User.UserStatus.ACTIVE);
		com.ipas.ipas.model.entity.User saved = userService.save(user);
		Long id = saved.getId();
		userService.deleteUser(id);
		org.junit.jupiter.api.Assertions.assertTrue(userService.findById(id).isEmpty());
	}

	@Test
	void contextLoads() {
	}

	@Autowired
	private UserService userService;

	@Test
	void testAuthenticateWithInvalidUser() {
		UserService.AuthStatus status = userService.authenticate("noexiste@ipas.com", "password");
		org.junit.jupiter.api.Assertions.assertEquals(UserService.AuthStatus.USER_NOT_FOUND, status);
	}

	@Test
	void testAuthenticateWithInvalidPassword() {
		// Suponiendo que existe un usuario con email "admin@ipas.com" y password "admin123"
		UserService.AuthStatus status = userService.authenticate("admin@ipas.com", "wrongpassword");
		org.junit.jupiter.api.Assertions.assertEquals(UserService.AuthStatus.INVALID_CREDENTIALS, status);
	}

}

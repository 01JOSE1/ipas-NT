package com.ipas.ipas;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import com.ipas.ipas.model.service.UserService;
import com.ipas.ipas.model.entity.User;

@SpringBootTest
class IpasApplicationTests {
	

	

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

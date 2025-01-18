package com.trnqngmnh.library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.trnqngmnh.library.entity.User;
import com.trnqngmnh.library.repository.UserRepository;
import com.trnqngmnh.library.service.UserService;

class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserService userService;

	private User testUser;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		testUser = new User();
		testUser.setId(1L);
		testUser.setName("John Doe");
		testUser.setEmail("john.doe@example.com");
		testUser.setPassword("password123");
	}

	@Test
	void testSaveUser() {
		when(userRepository.save(testUser)).thenReturn(testUser);

		User savedUser = userService.saveUser(testUser);

		assertNotNull(savedUser);
		assertEquals(testUser.getId(), savedUser.getId());
		verify(userRepository, times(1)).save(testUser);
	}

	@Test
	void testGetAllUsers() {
		List<User> users = new ArrayList<>();
		users.add(testUser);
		when(userRepository.findAll()).thenReturn(users);

		List<User> retrievedUsers = userService.getAllUsers();

		assertEquals(1, retrievedUsers.size());
		assertEquals(testUser, retrievedUsers.get(0));
		verify(userRepository, times(1)).findAll();
	}

	@Test
	void testCheckLoginSuccess() {
		when(userRepository.findByEmail(testUser.getEmail())).thenReturn(testUser);
		when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);

		User loggedInUser = userService.checkLogin(testUser.getEmail(), "password123");

		assertNotNull(loggedInUser);
		assertEquals(testUser.getEmail(), loggedInUser.getEmail());
	}

	@Test
	void testCheckLoginFailure() {
		when(userRepository.findByEmail(testUser.getEmail())).thenReturn(testUser);
		when(passwordEncoder.matches("wrongpassword", testUser.getPassword())).thenReturn(false);

		User loggedInUser = userService.checkLogin(testUser.getEmail(), "wrongpassword");

		assertNull(loggedInUser);
	}

	@Test
	void testFindUserById() {
		when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

		User foundUser = userService.findUserById(1L);

		assertNotNull(foundUser);
		assertEquals(testUser.getId(), foundUser.getId());
		verify(userRepository, times(1)).findById(1L);
	}

	@Test
	void testFindUserByEmail() {
		when(userRepository.findByEmail(testUser.getEmail())).thenReturn(testUser);

		User foundUser = userService.findUserByEmail(testUser.getEmail());

		assertNotNull(foundUser);
		assertEquals(testUser.getEmail(), foundUser.getEmail());
		verify(userRepository, times(1)).findByEmail(testUser.getEmail());
	}

	@Test
	void testUpdatePassword() {
		String newPassword = "newPassword123";
		when(passwordEncoder.encode(newPassword)).thenReturn("encodedPassword");
		when(userRepository.save(testUser)).thenReturn(testUser);

		userService.updatePassword(testUser, newPassword);

		assertEquals("encodedPassword", testUser.getPassword());
		verify(passwordEncoder, times(1)).encode(newPassword);
		verify(userRepository, times(1)).save(testUser);
	}

	@Test
	void testDeleteUserById() {
		when(userRepository.existsById(1L)).thenReturn(true);

		userService.deleteUserById(1L);

		verify(userRepository, times(1)).deleteById(1L);
	}

	@Test
	void testDeleteUserById_NotFound() {
		when(userRepository.existsById(1L)).thenReturn(false);

		Exception exception = assertThrows(RuntimeException.class, () -> userService.deleteUserById(1L));

		assertEquals("Không tìm thấy người dùng với id 1", exception.getMessage());
		verify(userRepository, never()).deleteById(1L);
	}

	@Test
	void testCheckPassword() {
		when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);

		boolean matches = userService.checkPassword("rawPassword", "encodedPassword");

		assertTrue(matches);
		verify(passwordEncoder, times(1)).matches("rawPassword", "encodedPassword");
	}
}

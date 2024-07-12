package com.trnqngmnh.library;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public User saveUser(User user) {
		return userRepository.save(user);
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@SuppressWarnings("null")
	public User checkLogin(String email, String password) {
		User user = userRepository.findByEmail(email);
		if (user != null && user.getPassword().equals(password)) {
			System.out.println("True: " + user.getEmail() + ": " + user.getPassword());
			return user;
		} else {
			System.out.println("False: Incorrect password for: " + user.getEmail());
			return null;
		}
	}

	public User findUserById(Long id) {
		return userRepository.findById(id).orElse(null);
	}

	public User findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public User findByName(String name) {
		return userRepository.findByName(name);
	}

	public User updateUser(User user) {
		User existingUser = userRepository.findById(user.getId()).orElse(null);
		existingUser.setId(user.getId());
		existingUser.setName(user.getName());
		existingUser.setAddress(user.getAddress());
		existingUser.setDateOfBirth(user.getDateOfBirth());
		existingUser.setGender(user.getGender());
		existingUser.setImagePath(user.getImagePath());
		existingUser.setPhone(user.getPhone());
		return userRepository.save(existingUser);
	}

	public void deleteUserById(Long id) {
		if (userRepository.existsById(id)) {
			userRepository.deleteById(id);
		} else {
			throw new RuntimeException("Không tìm thấy người dùng với id " + id);
		}
	}

	@Transactional
	public void updatePassword(User user, String newPassword) {
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);
	}

}
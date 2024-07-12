package com.trnqngmnh.library;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// Cho phép tất cả các yêu cầu không cần xác thực
		http.authorizeHttpRequests(
				authorizeRequests -> authorizeRequests.requestMatchers("/**").permitAll().anyRequest().authenticated())
				.csrf(csrf -> csrf.disable()) // Vô hiệu hóa CSRF để đơn giản hóa
				.formLogin(formLogin -> formLogin.disable()) // Vô hiệu hóa form login mặc định
				.httpBasic(httpBasic -> httpBasic.disable()); // Vô hiệu hóa xác thực cơ bản

		return http.build();
	}
}

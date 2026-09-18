package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import com.example.demo.filter.JWTFilter;
import com.example.demo.filter.LoginFilter;
import com.example.demo.handler.LoginSuccesHandler;
import com.example.demo.util.JWTUtil;

@Configuration
public class SequrityConfig {
	
	private final AuthenticationConfiguration authenticationConfiguration;
	private final LoginSuccesHandler loginSuccessHandler;
	private final JWTUtil jwtUtil;
	
	public SequrityConfig(AuthenticationConfiguration authenticationConfiguration,LoginSuccesHandler loginSuccesHandler,JWTUtil jwtUtil) {
		
		this.authenticationConfiguration = authenticationConfiguration;
		this.loginSuccessHandler = loginSuccesHandler;
		this.jwtUtil = jwtUtil;		
	}
	
	@Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) {
        return configuration.getAuthenticationManager();
    }
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration config = new CorsConfiguration();

	    config.addAllowedOrigin("http://localhost:3000");
	    config.addAllowedMethod("*");
	    config.addAllowedHeader("*");
	    config.setAllowCredentials(true);

	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", config);

	    return source;
	}
	
	@Bean
	public SecurityFilterChain securityFiletChain(HttpSecurity http) {
		
		
		
		// 수많은 필터중 CSRF 필터를 disavle 시킴
		http.csrf(csrf -> csrf.disable());
		
		http
	    .cors(cors -> cors.configurationSource(corsConfigurationSource()));
		
        // 기본 로그인
		http.formLogin(login -> login.disable());
		
		// 경로별 인가
		http.authorizeHttpRequests(auth -> auth.requestMatchers("/**").permitAll());
		http.authorizeHttpRequests(auth -> auth.requestMatchers("/api/v1/join").permitAll()
                .requestMatchers("/api/v1/").permitAll()
                .requestMatchers("/api/v1/user").hasRole("USER")
                .anyRequest().hasRole("ADMIN")
        );
		
		// 커스텀 필터 추가
		http.addFilterAfter(new JWTFilter(jwtUtil),SecurityContextHolderFilter.class);
        http.addFilterBefore(new LoginFilter(authenticationManager(authenticationConfiguration), loginSuccessHandler), UsernamePasswordAuthenticationFilter.class);
		
		// 세션 설정 STATELESS
		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		
		return http.build();
	}

}

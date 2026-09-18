package com.example.demo.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.user.dto.UserRequestDTO;
import com.example.demo.domain.user.service.UserService;

@RestController
@RequestMapping("/api/v1")
public class JoinController {

	private final UserService userService;
	
	public JoinController(UserService userService) {
		this.userService = userService ;
	}
	
	@PostMapping("/join")
	public String join (@RequestBody UserRequestDTO dto) {
		userService.join(dto);
		return "Success";
	}

	
	
	
	
	
	
	
	
	
	
	
}

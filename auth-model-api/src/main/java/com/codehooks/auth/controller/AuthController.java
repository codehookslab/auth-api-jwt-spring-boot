package com.codehooks.auth.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codehooks.auth.config.security.JwtTokenProvider;
import com.codehooks.auth.model.User;
import com.codehooks.auth.payload.ApiResponse;
import com.codehooks.auth.payload.JwtAuthenticationResponse;
import com.codehooks.auth.payload.LoginRequest;
import com.codehooks.auth.payload.SignUpRequest;
import com.codehooks.auth.repository.UserRepository;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private UserRepository userRepo;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
    AuthenticationManager authenticationManager;
	@Autowired
	JwtTokenProvider tokenProvider;

	@PostMapping(path = "/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signupUser) {
		if (userRepo.existsByEmail(signupUser.getEmail())) {
			return new ResponseEntity(new ApiResponse(false, "Email Address already taken"), HttpStatus.BAD_REQUEST);
		}

		if (userRepo.existsByUsername(signupUser.getUsername())) {
			return new ResponseEntity(new ApiResponse(false, "Username already taken"), HttpStatus.BAD_REQUEST);
		}

		// Creating User
		User user = new User(signupUser.getUsername(), signupUser.getEmail(), passwordEncoder.encode(signupUser.getPassword()),
				signupUser.getFirstName(), signupUser.getMiddleName(), signupUser.getLastName());

		User result = userRepo.save(user);
		return ResponseEntity.ok(result);
	}
	
	@PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
System.out.println("---------------------- authenticateUser " );
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }
}

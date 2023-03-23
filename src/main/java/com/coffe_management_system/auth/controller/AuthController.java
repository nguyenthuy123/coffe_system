package com.coffe_management_system.auth.controller;


import com.coffe_management_system.auth.dto.LoginDto;
import com.coffe_management_system.auth.dto.LoginResponse;
import com.coffe_management_system.auth.dto.RegistrationDto;
import com.coffe_management_system.auth.dto.UserResponse;
import com.coffe_management_system.auth.entity.User;
import com.coffe_management_system.auth.repository.UserRepository;
import com.coffe_management_system.auth.security.JwtTokenUtil;
import com.coffe_management_system.auth.service.UserService;
import com.coffe_management_system.dto.ServerResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "http://localhost:8081")
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    private final JwtTokenUtil jwtTokenUtil;

    private final UserRepository repository;

    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("login")
    public ResponseEntity<ServerResponseDto> login(@RequestBody LoginDto request) {

        User user = userService.getByUsername(request.getUsername());

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.ok(ServerResponseDto.error("Username or password is wrong"));
        }
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(request.username(), request.password());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtTokenUtil.generateAccessToken(user);
        String refreshToken = jwtTokenUtil.generateRefreshToken(user);

        LoginResponse response = new LoginResponse(UserResponse.fromEntity(user),accessToken,refreshToken);

        return ResponseEntity.ok().body(ServerResponseDto.success(response));
    }

    @PostMapping("refresh")
    public ResponseEntity<Map<String, String>> refresh(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String refreshToken = authorizationHeader.substring("Bearer ".length());
            if (jwtTokenUtil.validate(refreshToken)) {
                org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User)
                        userService.loadUserByUsername(jwtTokenUtil.getUserName(refreshToken));
                User user = userService.getByUsername(userDetails.getUsername());

                String accessToken = jwtTokenUtil.generateAccessToken(user);

                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", accessToken);

                return ResponseEntity.ok().body(tokens);
            }
        }

        return ResponseEntity.badRequest().body(null);
    }

    @PostMapping("register")
    public ResponseEntity<ServerResponseDto> register(@RequestBody RegistrationDto request) {
        return ResponseEntity.ok(userService.create(request));
    }

}

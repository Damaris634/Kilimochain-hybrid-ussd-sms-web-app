package com.kilimochain.service;
import com.kilimochain.security.JwtService;
import com.kilimochain.model.dto.AuthResponse;
import com.kilimochain.model.dto.LoginRequest;
import com.kilimochain.model.dto.RegisterRequest;
import com.kilimochain.model.entity.User;
import com.kilimochain.model.enums.UserRole;
import com.kilimochain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService; 

    public String register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            return "Email already exists";
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            return "Phone number already exists";
        }
        User user = User.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .location(request.getLocation())
                .role(UserRole.BUYER)
                .build();

        userRepository.save(user);

        return "Registration successful";
    }
public AuthResponse login(LoginRequest request) {
    User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found")); 

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        throw new RuntimeException("Invalid password");

    }

        org.springframework.security.core.userdetails.UserDetails userDetails =
        org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRole().name())
                .build();
                    
                

    String token = jwtService.generateToken(userDetails);

    return AuthResponse.builder()
            .token(token)
            .message("Login successful")
            .build();
}
}
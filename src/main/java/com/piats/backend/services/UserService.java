package com.piats.backend.services;

import com.piats.backend.dto.TokenResponseDto;
import com.piats.backend.exceptions.InvalidCredentialsException;
import com.piats.backend.exceptions.UserNotFoundException;
import com.piats.backend.models.User;
import com.piats.backend.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

   @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
       this.passwordEncoder = passwordEncoder;
       this.jwtService = jwtService;
   }

    public TokenResponseDto authenticateUser(User requestUser) {
        String email = requestUser.getEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Given email or password is wrong."));
        if (passwordEncoder.matches(requestUser.getPassword(), user.getPassword())) {
            TokenResponseDto responseDto = new TokenResponseDto();
            responseDto.setMessage("Authentication successful.");
       //     responseDto.setToken(jwtService.generateToken(user.getId().toString()));
            responseDto.setId(user.getId().toString());
            return responseDto;
        } else {
            throw new InvalidCredentialsException("Given email or password is wrong.");
        }
    }
}

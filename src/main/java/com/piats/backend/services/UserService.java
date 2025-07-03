package com.piats.backend.services;

import com.piats.backend.dto.MessageResponseDto;
import com.piats.backend.dto.RegisterUserRequestDto;
import com.piats.backend.dto.TokenResponseDto;
import com.piats.backend.enums.Role;
import com.piats.backend.exceptions.InvalidCredentialsException;
import com.piats.backend.exceptions.UserAlreadyExistsException;
import com.piats.backend.exceptions.UserNotFoundException;
import com.piats.backend.models.User;
import com.piats.backend.repos.UserRepository;
import com.piats.backend.utils.ValidationUtil;
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
            responseDto.setToken(jwtService.generateToken(String.valueOf(user.getId()), String.valueOf(user.getRole())));
            responseDto.setId(user.getId().toString());
            return responseDto;
        } else {
            throw new InvalidCredentialsException("Given email or password is wrong.");
        }
    }

    public MessageResponseDto saveUser(RegisterUserRequestDto requestUser) {
        String email = requestUser.getEmail();
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("User with given email already exists.");
        }
        ValidationUtil.validateRegister(requestUser);
        Role role = Role.valueOf(requestUser.getRole());
        User user = new User();
        user.setEmail(requestUser.getEmail());
        user.setPassword(passwordEncoder.encode(requestUser.getPassword()));
        user.setFirstName(requestUser.getFirstName());
        user.setLastName(requestUser.getLastName());
        user.setRole(role);
        userRepository.save(user);

        MessageResponseDto responseDto = new MessageResponseDto();
        responseDto.setMessage("Registration successful.");
        return responseDto;
    }
}

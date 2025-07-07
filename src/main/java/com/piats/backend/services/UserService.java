package com.piats.backend.services;

import com.piats.backend.dto.*;
import com.piats.backend.enums.Role;
import com.piats.backend.exceptions.InvalidCredentialsException;
import com.piats.backend.exceptions.UserAlreadyExistsException;
import com.piats.backend.exceptions.UserNotFoundException;
import com.piats.backend.models.User;
import com.piats.backend.repos.UserRepository;
import com.piats.backend.utils.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public TokenResponseDto authenticateUser(LoginUserRequestDto requestUser) {
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

    public Page<UserInfoResponseDto> getAllUsers(Pageable pageable, String role) {
        Page<User> userPage;
        if (!(role == null) && ValidationUtil.doesUserRoleExist(role)) {
            String validatedRole = role.toUpperCase();
            userPage = userRepository.findByRole(Role.valueOf(validatedRole), pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }
        return userPage.map(this::convertUserInfoResponseDto);
    }

    private UserInfoResponseDto convertUserInfoResponseDto(User user) {
        UserInfoResponseDto dto = new UserInfoResponseDto();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole().name());
        return dto;
    }
}

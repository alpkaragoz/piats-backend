package com.piats.backend.configs;

import com.piats.backend.models.User;
import com.piats.backend.repos.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestDataLoader {

  /*  @Bean
    public CommandLineRunner loadTestUser(UserRepository userRepository) {
        return args -> {
            User user = new User();
            user.setEmail("test@gmail.com");
            user.setPassword("123456");
            userRepository.save(user);
        };
    } */
}

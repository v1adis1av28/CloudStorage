package com.storage.services;

import com.storage.repositories.UserRepository;
import com.storage.security.CustomUserDetails;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import com.storage.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isEmpty())
            throw new UsernameNotFoundException(username);
        return new CustomUserDetails(user.get());
    }

    public String createUser(String username, String password)
    {
        User user = User.builder()
                .username(username)
                .password(new BCryptPasswordEncoder().encode(password))
                .role("user")
                .build();
        try {
            userRepository.save(user);
        }catch (ConstraintViolationException e){
            throw new ConstraintViolationException(e.getConstraintViolations());
        }
        return "User created";
    }
}

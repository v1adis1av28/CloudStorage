package com.storage.validation;

import com.storage.model.User;
import com.storage.services.UserService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.executable.ExecutableValidator;
import jakarta.validation.metadata.BeanDescriptor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class UserValidation implements Validator {

    private final UserService userService;

    @Autowired
    public UserValidation(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> aClass)
    {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {

        User user = (User) target;
        try {
            userService.loadUserByUsername(user.getUsername());
        } catch(UsernameNotFoundException e)
        {
            return ; // Значит валидация прошла
        }

        errors.rejectValue("username", "", "User with this email already exists");
    }

}

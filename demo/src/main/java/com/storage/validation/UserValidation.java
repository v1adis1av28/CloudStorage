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
import java.util.regex.Pattern;

@Component
public class UserValidation implements Validator {

    private final UserService userService;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

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
            errors.rejectValue("username", "", "User with this email already exists.");
        } catch (UsernameNotFoundException ignored) {
        }

        if (!validateEmailField(user.getUsername())) {
            errors.rejectValue("username", "", "Invalid email format. Please provide a valid email.");
        }
    }

    public boolean validateEmailField(String email) {
        return VALID_EMAIL_ADDRESS_REGEX.matcher(email).matches();
    }

}

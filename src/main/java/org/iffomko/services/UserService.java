package org.iffomko.services;

import org.iffomko.domain.User;
import org.iffomko.exceptions.LocalizedException;
import org.iffomko.repositories.UserRepository;
import org.iffomko.validators.UserValidator;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private static final String USER_ALREADY_EXISTS_MESSAGE = "validation.user.already-exists";
    private static final String INVALID_USER_MESSAGE = "user.invalid";

    private final UserRepository userRepository;
    private final UserValidator userValidator;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.userValidator = new UserValidator();
    }

    public Optional<User> byId(int id) {
        return userRepository.findById(id);
    }

    public Optional<User> byPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    public User register(User user) {
        userValidator.validate(user);
        if (byPhone(user.getPhone()).isPresent()) {
            throw new LocalizedException(USER_ALREADY_EXISTS_MESSAGE);
        }
        user.setPassword(user.getPassword());
        return userRepository.save(user);
    }

    public User login(User user) {
        return byPhone(user.getPhone())
                .map(realUser -> {
                    if (user.getPassword() != null && !user.getPassword().equals(realUser.getPassword())) {
                        throw new LocalizedException(INVALID_USER_MESSAGE);
                    }
                    return realUser;
                })
                .orElseThrow(() -> new LocalizedException(INVALID_USER_MESSAGE));
    }
}

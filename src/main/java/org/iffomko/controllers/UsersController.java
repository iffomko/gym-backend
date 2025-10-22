package org.iffomko.controllers;

import org.iffomko.domain.User;
import org.iffomko.services.UserService;
import org.springframework.web.bind.annotation.*;

import static org.iffomko.domain.ControllerNames.BASE_URL;
import static org.iffomko.domain.ControllerNames.*;

@RestController
@RequestMapping(BASE_URL + USERS_URI_PART)

public class UsersController {
    private final UserService userService;

    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(LOGIN_URI_PART)
    public User login(@RequestBody User user) {
        return userService.login(user);
    }

    @PostMapping(REGISTRATION_URI_PART)
    public User register(@RequestBody User user) {
        return userService.register(user);
    }
}

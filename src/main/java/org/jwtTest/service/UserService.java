package org.jwtTest.service;

import org.jwtTest.model.User;
import org.jwtTest.model.UserRequest;
import org.jwtTest.model.UserResponse;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {

    UserResponse createUser(UserRequest userRequest);

    Optional<User> findUserByMail(String mail);

    User updateToken(User user);

    User getUser(String tokenHeader);
}

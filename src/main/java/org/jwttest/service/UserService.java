package org.jwttest.service;

import org.jwttest.model.User;
import org.jwttest.model.UserRequest;
import org.jwttest.model.UserResponse;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {

    UserResponse createUser(UserRequest userRequest);

    Optional<User> findUserByMail(String mail);

    User updateToken(User user);

    User getUser(String tokenHeader);

    User getUserByToken(String tokenHeader);
}

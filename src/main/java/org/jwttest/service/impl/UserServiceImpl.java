package org.jwttest.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jwttest.exception.NotFoundException;
import org.jwttest.exception.UnauthorizedException;
import org.jwttest.model.Phone;
import org.jwttest.model.User;
import org.jwttest.model.UserRequest;
import org.jwttest.model.UserResponse;
import org.jwttest.persistence.UserRepository;
import org.jwttest.service.UserService;
import org.jwttest.util.JwtUtil;
import org.jwttest.util.ValidationUtility;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {


    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private UserRepository userRepository;


    public UserServiceImpl(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UserResponse createUser(UserRequest userRequest) {

        Date currentDate = new Date();

        User user = User.builder()
                .id(UUID.randomUUID())
                .created(currentDate)
                .email(validateAndReturnEmail(userRequest.getEmail()))
                .isActive(true)
                .lastLogin(currentDate)
                .name(userRequest.getName())
                .password(validateAndReturnEncriptedPassword(userRequest.getPassword()))
                .token(jwtUtil.generateToken(userRequest.getEmail()))
                .build();

        List<Phone> phoneEntities = userRequest.getPhones().stream()
                .map(phoneRequest -> Phone.builder()
                        .number(phoneRequest.getNumber())
                        .citycode(phoneRequest.getCitycode())
                        .countrycode(phoneRequest.getContrycode())
                        .build())
                .peek(phone -> phone.setUser(user))
                .collect(Collectors.toList());

        user.setPhones(phoneEntities);
        User savedUser = userRepository.save(user);
        return UserResponse.builder()
                .id(savedUser.getId())
                .isActive(savedUser.isActive())
                .lastLogin(savedUser.getLastLogin())
                .created(savedUser.getCreated())
                .token(savedUser.getToken())
                .build();
    }

    @Transactional
    @Override
    public User getUser(String tokenHeader) {
        String token = tokenHeader.replace("Bearer ", "");
        String subject = jwtUtil.extractSubject(token);

        Optional<User> userOpt = findUserByMail(subject);
        if (userOpt.isPresent()) {
            return updateToken(userOpt.get());
        } else {
            log.warn("User not found for token: {}", token);
            return null;
        }
    }


    private String validateAndReturnEmail(String email) {
        if (ValidationUtility.isValidEmail(email)) {
            final Optional<User> byEmail = userRepository.findByEmail(email);
            if (byEmail.isPresent()){
                throw new IllegalArgumentException("User already exists");
            }
            return email;
        } else {
            throw new IllegalArgumentException("Wrong email format");
        }
    }

    private String validateAndReturnEncriptedPassword(String password) {
        if (ValidationUtility.isValidPassword(password)) {
            return passwordEncoder.encode(password);
        } else {
            throw new IllegalArgumentException("Wrong password format");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findUserByMail(String mail) {
        return userRepository.findByEmail(mail);
    }

    @Override
    public User updateToken(User user) {
        String token = jwtUtil.generateToken(user.getEmail());
        user.setToken(token);
        user.setLastLogin(new Date());
        return userRepository.save(user);
    }

    @Override
    public User getUserByToken(String tokenHeader) {
        String token = tokenHeader.replace("Bearer ", "");

        if (!jwtUtil.isTokenValid(token)) {
            throw new UnauthorizedException("Invalid token");
        }

        User user = getUser(tokenHeader);
        if (user == null || !user.isActive()) {
            throw new NotFoundException("User not found or inactive");
        }

        return user;
    }

}


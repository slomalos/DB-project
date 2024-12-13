package com.example.buysell.services;

import com.example.buysell.dao.UserDao;
import com.example.buysell.models.User;
import com.example.buysell.models.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    public boolean createUser(User user) {
        String email = user.getEmail();
        if (userDao.findByEmail(email) != null) return false;
        user.setActive(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.getRoles().add(Role.ROLE_USER);
        log.info("Saving new User with email: {}", email);
        userDao.save(user);
        return true;
    }

    public List<User> list() {
        return userDao.findAll();
    }

    public void banUser(Long id) {
        User user = userDao.findById(id);
        if (user != null) {
            user.setActive(!user.isActive());
            log.info("User with id = {}; email: {} is now {}", user.getId(), user.getEmail(), user.isActive() ? "active" : "banned");
            userDao.update(user);
        }
    }

    public void changeUserRoles(User user, Map<String, String> form) {
        Set<String> roles = Arrays.stream(Role.values())
                .map(Role::name)
                .collect(Collectors.toSet());
        user.getRoles().clear();
        for (String key : form.keySet()) {
            if (roles.contains(key)) {
                user.getRoles().add(Role.valueOf(key));
            }
        }
        userDao.update(user);
    }

    public User getUserByPrincipal(Principal principal) {
        if (principal == null) return new User();
        return userDao.findByEmail(principal.getName());
    }
}
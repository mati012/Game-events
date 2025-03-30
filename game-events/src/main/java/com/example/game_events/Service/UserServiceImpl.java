package com.example.game_events.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.game_events.Model.Role;
import com.example.game_events.Model.User;
import com.example.game_events.Repository.RoleRepository;
import com.example.game_events.Repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, 
                           PasswordEncoder passwordEncoder,
                           RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public User registerUser(User user) {
        validateNewUser(user);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> {
                    Role newRole = new Role("USER");
                    return roleRepository.save(newRole);
                });

        user.getRoles().add(userRole);

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        validateExistingUser(user);

        Optional<User> existingUserOpt = userRepository.findById(user.getId());
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();

            // Codificar la contraseña solo si ha cambiado
            if (isPasswordChanged(user, existingUser)) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }

            if (user.getRoles() == null || user.getRoles().isEmpty()) {
                user.setRoles(existingUser.getRoles());
            }
        }

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private void validateNewUser(User user) {
        if (existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe: " + user.getUsername());
        }
        if (existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("El correo electrónico ya existe: " + user.getEmail());
        }
    }

    private void validateExistingUser(User user) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo para la actualización.");
        }
    }

    private boolean isPasswordChanged(User newUser, User existingUser) {
        return !newUser.getPassword().equals(existingUser.getPassword()) &&
               !newUser.getPassword().startsWith("$2a$");
    }
}
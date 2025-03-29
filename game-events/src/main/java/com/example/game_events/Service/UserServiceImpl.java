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
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
        // Codificar la contraseña
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Inicializar roles si es null
        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }
        
        // Buscar el rol USER o crearlo si no existe
        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> {
                    Role newRole = new Role("USER");
                    return roleRepository.save(newRole);
                });
        
        // Añadir el rol al usuario
        user.getRoles().add(userRole);
        
        // Guardar y devolver el usuario
        return userRepository.save(user);
    }
    
    @Override
    public User updateUser(User user) {
        if (user.getId() != null) {
            Optional<User> existingUser = userRepository.findById(user.getId());
            if (existingUser.isPresent()) {
                User currentUser = existingUser.get();
                
                // Solo codificar la contraseña si ha cambiado
                if (!user.getPassword().equals(currentUser.getPassword()) && 
                    !user.getPassword().startsWith("$2a$")) {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                }
                
                // Mantener los roles existentes si no se proporcionan nuevos
                if (user.getRoles() == null || user.getRoles().isEmpty()) {
                    user.setRoles(currentUser.getRoles());
                }
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
}
package com.springboot.restful_mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class UserController {
    private final UserRepository userRepository;
    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @GetMapping("/")
    public @ResponseBody Iterable<User> getAllUser() {
        return userRepository.findAll();
    }

    @PostMapping("/")
    User newUser(@RequestBody User newUser) {
        return userRepository.save(newUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> one(@PathVariable Integer id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException(id));
            return ResponseEntity.ok(user);
        } catch(UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@RequestBody User updateUser, @PathVariable Integer id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException(id));
            user.setName(updateUser.getName());
            user.setEmail(updateUser.getEmail());
            User updatedUser = userRepository.save(user);
            return ResponseEntity.ok(updatedUser);
        } catch(UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException(id));
            userRepository.deleteById(id);
            return ResponseEntity.ok("User has been deleted.");
        } catch(UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
}

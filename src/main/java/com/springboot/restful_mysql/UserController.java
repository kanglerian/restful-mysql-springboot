package com.springboot.restful_mysql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
    ObjectMapper objectMapper = new ObjectMapper();
    @GetMapping("/")
    public @ResponseBody Iterable<User> getAllUser() {
        return userRepository.findAll();
    }

    @PostMapping("/")
    public ResponseEntity<?> newUser(@RequestBody User newUser) {
        try {
            User savedUser = userRepository.save(newUser);
            return ResponseEntity.ok(savedUser);
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> one(@PathVariable Integer id) {
        try {
            Optional<User> user = userRepository.findById(id);
            if(user.isPresent()){
                return ResponseEntity.ok(user);
            } else {
                String errorMessage = "User with id " + id + " not found.";
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(errorMessage));
            }
        } catch(UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@RequestBody User updateUser, @PathVariable Integer id) {
        try {
            Optional<User> optionalUser = userRepository.findById(id);
            if(optionalUser.isPresent()){
                User user = optionalUser.get();
                user.setName(updateUser.getName());
                user.setEmail(updateUser.getEmail());
                userRepository.save(user);
                String jsonUser = objectMapper.writeValueAsString(user);
                System.out.println(jsonUser);
                return ResponseEntity.ok(user);
            } else {
                String errorMessage = "User with id " + id + " not found.";
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(errorMessage));
            }
        } catch(UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        try {
            Optional<User> user = userRepository.findById(id);
            if(user.isPresent()){
                userRepository.deleteById(id);
                return ResponseEntity.ok().body(new SuccessResponse("User with id " + id + " has been deleted."));
            } else {
                String errorMessage = "User with id " + id + " not found.";
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(errorMessage));
            }
        } catch(UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
        }
    }
}

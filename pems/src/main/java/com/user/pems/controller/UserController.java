package com.user.pems.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.user.pems.dto.Account;
import com.user.pems.dto.Expense;
import com.user.pems.dto.User;
import com.user.pems.service.UserService;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;
    
    @GetMapping("/")
    public String index() {
        return "index"; 
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{username}")
    public User getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody User user) {
        User existingUser = userService.getUserByUsername(user.getUsername());
        if (existingUser != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already exists");
        } else {
            User savedUser = userService.addUser(user);
            return ResponseEntity.ok(savedUser);
        }
    }


    @PutMapping("/{username}")
    public User updateUserByUsername(@PathVariable String username, @RequestBody User user) {
        return userService.updateUserByUsername(username, user);
    }

    @DeleteMapping("/{username}")
    public void deleteUserByUsername(@PathVariable String username) {
        userService.deleteUserByUsername(username);
    }
    
    @GetMapping("/{username}/{accountNo}")
    public ResponseEntity<List<Expense>> getAllExpensesByUsernameAndAccountNo(
            @PathVariable String username, @PathVariable String accountNo) {
        List<Expense> expenses = userService.getAllExpensesByUsernameAndAccountNo(username, accountNo);
        if (expenses != null) {
            return new ResponseEntity<>(expenses, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @PostMapping("/{username}/accounts")
    public ResponseEntity<User> addAccount(@PathVariable String username, @RequestBody Account newAccount) {
        User updatedUser = userService.addAccount(username, newAccount);
        if (updatedUser != null) {
            return new ResponseEntity<>(updatedUser, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @PostMapping("/{username}/expenses/{accountNo}")
    public User addExpense(@PathVariable String username, @PathVariable String accountNo, @RequestBody Expense newExpense) {
        return userService.addExpense(username, accountNo, newExpense);
    }
    
    @PutMapping("/{username}/expenses/{expenseId}")
    public User updateExpenseByExpenseId(@PathVariable String username, @PathVariable int expenseId, @RequestBody Expense expense) {
        return userService.updateExpenseByExpenseId(username, expenseId, expense);
    }
    
    @DeleteMapping("/{username}/expenses/{expenseId}")
    public String deleteExpenseByExpenseId(@PathVariable String username, @PathVariable int expenseId) {
        User updatedUser = userService.deleteExpenseByExpenseId(username, expenseId);
        if (updatedUser != null) {
            return "Expense with ID " + expenseId + " deleted successfully.";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found.");
        }
    }
    
    @GetMapping("/{username}/expenses/search/{accountNo}")
    public ResponseEntity<List<Expense>> searchByDateOrCategory(@PathVariable String username, @PathVariable String accountNo, @RequestParam String searchCriteria) {
        List<Expense> matchedExpenses = userService.searchByDateOrCategory(username, accountNo, searchCriteria);
        if (matchedExpenses != null) {
            return ResponseEntity.ok(matchedExpenses);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}


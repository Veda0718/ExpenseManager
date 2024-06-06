package com.user.pems.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.user.pems.dto.Account;
import com.user.pems.dto.Expense;
import com.user.pems.dto.User;
import com.user.pems.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional.orElse(null);
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }

    public User updateUserByUsername(String username, User user) {
        User existingUser = getUserByUsername(username);
        if (existingUser != null) {
            // Update the fields of existingUser with the fields of the passed user object
            existingUser.setEmail(user.getEmail());
            existingUser.setPassword(user.getPassword());
            existingUser.setDateJoined(user.getDateJoined());
            existingUser.setAccounts(user.getAccounts());
            existingUser.setExpenses(user.getExpenses());
            
            return userRepository.save(existingUser);
        }
        return null;
    }

    public void deleteUserByUsername(String username) {
        User userToDelete = getUserByUsername(username);
        if (userToDelete != null) {
            userRepository.delete(userToDelete);
        }
    }
    
    public List<Expense> getAllExpensesByUsernameAndAccountNo(String username, String accountNo) {
        User user = getUserByUsername(username);
        if (user != null) {
            List<Account> accounts = user.getAccounts();
            for (Account account : accounts) {
                if (account.getAccountNo().equals(accountNo)) {
                    List<Integer> expenseIds = account.getExpenseId();
                    List<Expense> expenses = new ArrayList<>();
                    for (int expenseId : expenseIds) {
                        for (Expense expense : user.getExpenses()) {
                         if (expense.getExpenseId() == expenseId) {
                         expenses.add(expense);
                                break;
                            }
                        }
                    }
                    return expenses;
                }
            }
        }
        return null;
    }
    
    public User addAccount(String username, Account newAccount) {
        User existingUser = userRepository.findByUsername(username).orElse(null);
        if (existingUser != null) {
         if (existingUser.getAccounts() == null) {
                existingUser.setAccounts(new ArrayList<>());
            }
            existingUser.getAccounts().add(newAccount);
            return userRepository.save(existingUser);
        }
        return null; 
    }
    
    public User addExpense(String username, String accountNo, Expense newExpense) {
        User existingUser = getUserByUsername(username);
        if (existingUser != null) {
            // Initialize expenses list if null
            if (existingUser.getExpenses() == null) {
                existingUser.setExpenses(new ArrayList<>());
            }
            // Get existing expenses
            List<Expense> existingExpenses = existingUser.getExpenses();
           
            // Generate the expenseId
            int expenseId = Expense.getNextId(existingExpenses);
            newExpense.setExpenseId(expenseId);

            // Add the new expense to the user's expenses
            existingExpenses.add(newExpense);

            // Update the list of expenseIds in the account
            List<Account> accounts = existingUser.getAccounts();
            for (Account account : accounts) {
                if (account.getAccountNo().equals(accountNo)) {
                    // Initialize expenseId list if null
                    if (account.getExpenseId() == null) {
                        account.setExpenseId(new ArrayList<>());
                    }
                    account.getExpenseId().add(expenseId);
                 // Update balanceAmount
                    double currentBalance = account.getBalance();
                    double expenseAmount = newExpense.getExpenseAmount();
                    double updatedBalance = currentBalance - expenseAmount;
                    account.setBalance(updatedBalance);
                    break;
                }
            }

            // Save the updated user
            return userRepository.save(existingUser);
        }
        return null;
    }
    public User updateExpenseByExpenseId(String username, int expenseId, Expense updatedExpense) {
        User existingUser = getUserByUsername(username);
        if (existingUser != null) {
            List<Expense> expenses = existingUser.getExpenses();
            for (Expense expense : expenses) {
                if (expense.getExpenseId() == expenseId) {
                    // Calculate the net expense difference
                    double oldExpenseAmount = expense.getExpenseAmount();
                    double newExpenseAmount = updatedExpense.getExpenseAmount();
                    double expenseDifference = newExpenseAmount - oldExpenseAmount;

                    // Update the existing expense
                    expense.setCategory(updatedExpense.getCategory());
                    expense.setDate(updatedExpense.getDate());
                    expense.setExpenseAmount(newExpenseAmount);

                    // Update the balanceAmount of the corresponding Account
                    List<Account> accounts = existingUser.getAccounts();
                    for (Account account : accounts) {
                        if (account.getExpenseId() != null && account.getExpenseId().contains(expenseId)) {
                            double currentBalance = account.getBalance();
                            double updatedBalance = currentBalance - expenseDifference;
                            account.setBalance(updatedBalance);
                            break; // Assuming expenseId is unique
                        }
                    }

                    // Save the updated user
                    return userRepository.save(existingUser);
                }
            }
        }
        return null;
    }

    
    public User deleteExpenseByExpenseId(String username, int expenseId) {
        User existingUser = getUserByUsername(username);
        if (existingUser != null) {
            List<Expense> expenses = existingUser.getExpenses();
            for (Expense expense : expenses) {
                if (expense.getExpenseId() == expenseId) {
                    // Remove the expense from the list of expenses
                    expenses.remove(expense);
                   
                    // Remove the expenseId from the associated account
                    List<Account> accounts = existingUser.getAccounts();
                    for (Account account : accounts) {
                        if (account.getExpenseId().contains(expenseId)) {
                            account.getExpenseId().remove(Integer.valueOf(expenseId));
                            break;
                        }
                    }
                   
                    // Save the updated user
                    return userRepository.save(existingUser);
                }
            }
        }
        return null;
    }
    public List<Expense> searchByDateOrCategory(String username, String accountNo, String searchCriteria) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            List<Expense> matchedExpenses = new ArrayList<>();
            // Find the account with the given accountNo
            Account account = user.getAccounts().stream()
                                    .filter(acc -> acc.getAccountNo().equals(accountNo))
                                    .findFirst()
                                    .orElse(null);
            if (account != null) {
                // Get the list of expenseIds associated with the account
                List<Integer> expenseIds = account.getExpenseId();
                // Filter expenses based on expenseId and search criteria
                for (Expense expense : user.getExpenses()) {
                    if (expenseIds.contains(expense.getExpenseId()) &&
                        (expense.getDate().equals(searchCriteria) || expense.getCategory().equalsIgnoreCase(searchCriteria))) {
                        matchedExpenses.add(expense);
                    }
                }
            }
            return matchedExpenses;
        }
        return null;
    }


}

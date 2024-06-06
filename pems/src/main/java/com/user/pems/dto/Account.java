package com.user.pems.dto;

import java.util.Collections;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
	@Id
    private String accountNo;
    private double balance;
    private double income;
    private List<Integer> expenseId;
    private double loanAmount;
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	public double getIncome() {
		return income;
	}
	public void setIncome(double income) {
		this.income = income;
	}
	public List<Integer> getExpenseId() {
		return expenseId;
	}
	public void setExpenseId(List<Integer> expenseId) {
		this.expenseId = expenseId;
	}
	public double getLoanNo() {
		return loanAmount;
	}
	public void setLoanNo(double loanNo) {
		this.loanAmount = loanNo;
	}

}

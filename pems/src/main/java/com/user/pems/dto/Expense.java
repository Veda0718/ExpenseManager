package com.user.pems.dto;

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
public class Expense {
	private static int nextId;
	private int expenseId;
	private String category;
	private String date;
	private double expenseAmount;

	public static int getNextId(List<Expense> existingExpenses) {
		if (existingExpenses.isEmpty()) {
			nextId = 1;
		} else {
			nextId = existingExpenses.stream()
					.mapToInt(Expense::getExpenseId)
					.max()
					.orElse(0) + 1;
		}
		return nextId++;
	}

	public Integer getExpenseId() {
		return expenseId;
	}

	public void setExpenseId(Integer expenseId) {
		this.expenseId = expenseId;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public double getExpenseAmount() {
		return expenseAmount;
	}

	public void setExpenseAmount(double expenseAmount) {
		this.expenseAmount = expenseAmount;
	}

}

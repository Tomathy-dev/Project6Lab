package project;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents an expense.
 * @author Tomas Salgueiro n 57528
 */
public class Expense {

	private final String description;
	private final User userWhoPaid;
	private final Integer value;
	private final Map<String, Integer> userDebt;

	/**
	 * Creates an expense with a description, the user who paid, the value and the people involved.
	 * The expense is split equally between the people involved.
	 * If the value is not divisible by the number of people involved, the rest is split between the first people involved.
	 * @param description descruption of the expense
	 * @param userWhoPaid user who paid
	 * @param value value of the expense
	 * @param peopleInvolved people involved in the expense
	 */
	public Expense (String description, User userWhoPaid, Integer value, List<String> peopleInvolved) {
		this.description = description;
		this.userWhoPaid = userWhoPaid;
		this.value = value;
		this.userDebt = new HashMap<>();
		int rest = value % peopleInvolved.size();
		for(String s : peopleInvolved) {
			userDebt.put(s, value / peopleInvolved.size());
		}
		if (rest != 0){
			for(int i = 0; i < rest; i++) {
				userDebt.put(peopleInvolved.get(i), userDebt.get(peopleInvolved.get(i)) + 1);
			}
		}
	}

	/**
	 * Creates an expense with a description, the user who paid, the value, the people involved and how to split
	 * between the people involved
	 * The expense is split according to the howToSplit list.
	 * If the value is not divisible by the sum of the howToSplit list, the rest is split between the first people involved.
	 * @param description description of the expense
	 * @param userWhoPaid user who paid
	 * @param value value of the expense
	 * @param peopleInvolved people involved in the expense
	 * @param howToSplit how to split the expense between the people involved (from 0 to 1)
	 * @requires howToSplit.size() == peopleInvolved.size() && userWhoPaid != null && peopleInvolved.contains(userWhoPaid.getUsername())
	 */
	public Expense (String description, User userWhoPaid, Integer value, List<String> peopleInvolved, 
			List<Double> howToSplit) {
		this.description = description;
		this.userWhoPaid = userWhoPaid;
		this.value = value;
		this.userDebt = new HashMap<>();
		int totalSplit = 0;
		for(int i = 0; i < peopleInvolved.size(); i++) {
			userDebt.put(peopleInvolved.get(i), (int) (value * howToSplit.get(i)));
			totalSplit += userDebt.get(peopleInvolved.get(i));
		}
		if((value - totalSplit) != 0) {
			int rest = value - totalSplit;
			for(int i = 0; i < rest; i++) {
				userDebt.put(peopleInvolved.get(i), userDebt.get(peopleInvolved.get(i)) + 1);
			}
		}
	}

	/**
	 * Returns the value of the expense.
	 * @return value of the expense
	 */
	public Integer getValue() {
		return value;
	}

	/**
	 * Returns the user who paid.
	 * @return user who paid
	 */
	public User getPayer() {
		return userWhoPaid;
	}

	/**
	 * Returns the description of the expense.
	 * @return description of the expense
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the absolute value that the user has to pay.
	 * @param username username of the user
	 * @return absolute value that the user has to pay
	 * @requires peopleInvolved.contains(username)
	 */
	public Integer getSplit(String username) {
	return userDebt.get(username);
	}

	/**
	 * Returns the balance of the user.
	 * @param username username of the user
	 * @return balance of the user
	 * @requires peopleInvolved.contains(username)
	 */
	public Integer getExpenseBalance(String username) {
		if (username.equals(userWhoPaid.getUsername())) {
			return value - getSplit(username);
		} else {
			return -getSplit(username);
		}
	}
}

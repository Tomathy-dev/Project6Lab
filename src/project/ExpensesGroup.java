package project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a group of expenses.
 * A group of expenses is a group of users that share expenses between them.
 * Each user can have a debt with the other users in the group.
 * Only works if the users are all added at the beginning.
 * @author Tomas Salgueiro n 57528
 */
public class ExpensesGroup {

	private Map<String, Map<String, Integer>> debtGraph; // QuemÉdevido -> QuemDeve, QtDeve
	private final User creator;
	private final String groupName;
	private final List<Expense> expenses;
	private final List<User> users;

	/**
	 * Creates a group of expenses with a name and a creator.
	 * @param groupName name of the group
	 * @param user creator of the group
	 * @requires user != null
	 */
	public ExpensesGroup(String groupName, User user) {
		this.debtGraph = new HashMap<>();
		this.creator = user;
		creator.addGroup(this);
		this.groupName = groupName;
		this.expenses = new ArrayList<>();
		this.users = new ArrayList<>();
		this.users.add(user);
		this.debtGraph.put(user.getUsername(), new HashMap<>());
	}

	/**
	 * Returns the creator of the group.
	 * @return the creator of the group
	 */
	public User getCreator() {
		return creator;
	}

	/**
	 * Returns the name of the group.
	 * @return the name of the group
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * Returns the users in the group.
	 * @return the users in the group
	 */
	public List<String> usersInGroup(){
		List<String> usersS = new ArrayList<>();
		for(User u : users) {
			usersS.add(u.getUsername());
		}
		return usersS;
	}

	/**
	 * Returns the expenses of the group.
	 * @return the expenses of the group
	 */
	public List<Expense> getExpenses() {
		return expenses;
	}

	/**
	 * Adds a new user to the group, and updates the groups that user is in.
	 * @param user user to be added
	 * @requires !users.contains(user)
	 */
	public void addUser (User user) {
		debtGraph.put(user.getUsername(), new HashMap<>());
		users.add(user);
		user.addGroup(this);
	}

	/**
	 * Creates and adds a new expense in the group.
	 * @param description description of the expense
	 * @param userWhoPaid user who paid the expense
	 * @param value value of the expense
	 * @requires userWhoPaid != null && value > 0 && usersInGroup().contains(userWhoPaid.getUsername())
	 */
	public void addExpense(String description, User userWhoPaid, Integer value) {
		Expense expense = new Expense(description, userWhoPaid, value, usersInGroup());
		expenses.add(expense);
		for(User user : users){
			if(!user.equals(userWhoPaid)){
				if(!debtGraph.get(userWhoPaid.getUsername()).containsKey(user.getUsername()))
					debtGraph.get(userWhoPaid.getUsername()).put(user.getUsername(), 0);
				debtGraph.get(userWhoPaid.getUsername()).put(user.getUsername(), debtGraph.get(userWhoPaid.getUsername()).get(user.getUsername()) - expense.getExpenseBalance(user.getUsername()));
				if(debtGraph.get(user.getUsername()).containsKey(userWhoPaid.getUsername()))
					resolver(user.getUsername(), userWhoPaid.getUsername());
			}
		}
	}

	/**
	 * Creates and adds a new expense in the group, with a custom split.
	 * @param description description of the expense
	 * @param userWhoPaid user who paid the expense
	 * @param value value of the expense
	 * @param howToSplit list of doubles that represent the percentage of the expense that each user pays
	 * @requires userWhoPaid != null && value > 0 && usersInGroup().contains(userWhoPaid.getUsername())
	 * && howToSplit.size() == usersInGroup().size() && (howToSplit.get(0) + ... + howToSplit.get(howToSplit.size() - 1)) == 1
	 */
	public void addExpense(String description, User userWhoPaid, Integer value, List<Double> howToSplit) {
		Expense expense = new Expense(description, userWhoPaid, value, usersInGroup(), howToSplit);
		expenses.add(expense);
		for(User user : users){
			if(!user.equals(userWhoPaid)){
				if(!debtGraph.get(userWhoPaid.getUsername()).containsKey(user.getUsername()))
					debtGraph.get(userWhoPaid.getUsername()).put(user.getUsername(), 0);
				debtGraph.get(userWhoPaid.getUsername()).put(user.getUsername(), debtGraph.get(userWhoPaid.getUsername()).get(user.getUsername()) - expense.getExpenseBalance(user.getUsername()));
				if(debtGraph.get(user.getUsername()).containsKey(userWhoPaid.getUsername()))
					resolver(user.getUsername(), userWhoPaid.getUsername());
			}
		}
	}

	/**
	 * Returns the balance of a user in the group, from all the expenses.
	 * @param user user to get the balance from
	 * @return the balance of a user in the group
	 * @requires user != null && usersInGroup().contains(user.getUsername())
	 */
	public Integer getBalance (User user) {
		Integer balance = 0;
		for(Map.Entry<String, Integer> entry : debtGraph.get(user.getUsername()).entrySet()) {
			balance += entry.getValue();
		}
		for(User u : users) {
			if(!u.equals(user)) {
				if(debtGraph.get(u.getUsername()).containsKey(user.getUsername()))
					balance -= debtGraph.get(u.getUsername()).get(user.getUsername());
			}
		}
		return balance;
	}

	/**
	 * Returns a map with the users that owe money to the user and the amount they owe.
	 * @param user user to get the debtors from
	 * @return a map with the users that owe money to the user and the amount they owe
	 * @requires user != null && usersInGroup().contains(user.getUsername())
	 */
	public Map<String, Integer> getUserDebtors(User user){
		Map<String, Integer> userDebtors = new HashMap<>();
		for(Map.Entry<String,Integer> entry : debtGraph.get(user.getUsername()).entrySet()) {
			if(entry.getValue() > 0) {
				userDebtors.put(entry.getKey(), entry.getValue());
			}
		}
		return userDebtors;
	}

	/**
	 * Returns a map with the users that the user owes money to and the amount they owe.
	 * @param user user to get the debts from
	 * @return a map with the users that the user owes money to and the amount they owe
	 * @requires user != null && usersInGroup().contains(user.getUsername())
	 */
	public Map<String, Integer> getUserDebts(User user){
		Map<String, Integer> userDebts = new HashMap<>();
		for(Map.Entry<String,Map<String,Integer>> entry : debtGraph.entrySet()) {
			if(entry.getValue().containsKey(user.getUsername())) {
				userDebts.put(entry.getKey(), entry.getValue().get(user.getUsername()));
			}
		}
		return userDebts;
	}

	/**
	 * Settles up all the debts of a user.
	 * @param userPayer user to settle up
	 * @requires user != null && usersInGroup().contains(user.getUsername())
	 */
	public void settleUp(User userPayer) {
		Map<String,Integer> userDebts = getUserDebts(userPayer);
		for(String s : userDebts.keySet()) {
			settleUp(userPayer, getUser(s));
		}
	}

	/**
	 * Settles up the debt of a user with another user.
	 * @param userPayer user to settle up
	 * @param userReceiver user that receives the money
	 * @requires userPayer != null && userReceiver != null && usersInGroup().contains(userPayer.getUsername()) && usersInGroup().contains(userReceiver.getUsername())
	 */
	public void settleUp(User userPayer, User userReceiver) {
		Map<String,Integer> userDeptors = getUserDebtors(userReceiver);
		if(userDeptors.containsKey(userPayer.getUsername())) {
			debtGraph.get(userReceiver.getUsername()).remove(userPayer.getUsername());
		}
	}

	/**
	 * Returns the corresponding user from the username.
	 * @param username username of the user to get
	 * @return the corresponding user from the username or null if the user doesn't exist
	 * @requires username != null
	 */
	private User getUser(String username) {
		for(User u : users) {
			if(u.getUsername().equals(username)) {
				return u;
			}
		}
		return null;
	}

	/**
	 * Resolves the debt between two users.
	 * Sees which user owes more money and subtracts the amount from the other user, so only one of them owes money.
	 * If the amount is the same, both users don't owe money to each other.
	 * @param user1 user that owes money
	 * @param user2 user that receives the money
	 * @requires user1 != null && user2 != null && usersInGroup().contains(user1) && usersInGroup().contains(user2)
	 */
	private void resolver(String user1, String user2) {
		if(debtGraph.get(user1).get(user2) > debtGraph.get(user2).get(user1)) {
			debtGraph.get(user1).put(user2, debtGraph.get(user1).get(user2) - debtGraph.get(user2).get(user1));
			debtGraph.get(user2).remove(user1);
		}
		else if(debtGraph.get(user1).get(user2) < debtGraph.get(user2).get(user1)) {
			debtGraph.get(user2).put(user1, debtGraph.get(user2).get(user1) - debtGraph.get(user1).get(user2));
			debtGraph.get(user1).remove(user2);
		}
		else {
			debtGraph.get(user1).remove(user2);
			debtGraph.get(user2).remove(user1);
		}
	}
}

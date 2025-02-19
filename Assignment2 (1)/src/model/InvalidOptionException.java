package model;
/**
 * InvalidOptionException is a custom exception that is thrown when the customer
 * selects an invalid option from the menu.
 */
public class InvalidOptionException extends Exception {
	public InvalidOptionException(String message) {
		super(message);
	}
}

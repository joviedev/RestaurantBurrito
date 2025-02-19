package model;
/**
 * InvalidPriceException is a custom exception that is thrown when the price of
 * a food item is less than 1.
 */
public class InvalidPriceException extends Exception {
	public InvalidPriceException(String message) {
		super(message);
	}
}

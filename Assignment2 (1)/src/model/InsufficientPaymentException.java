package model;
/**
 * InsufficientPaymentException is a custom exception that is thrown when the
 * payment entered by the customer is not sufficient to cover the total amount
 * of the order.
 */
public class InsufficientPaymentException extends Exception {
	public InsufficientPaymentException(String message) {
		super(message);
	}
}

package model;
/**
 * Fries extends the Food class and represents a specific type of food item
 * fries. It get special prompt message for fries when user order items by using
 * polymorphism.
 */
public class Fries extends Food {

	public Fries(double price, int timeTaken, int numInBatch, boolean cookInBatch) {
		super("Fries", price, timeTaken, numInBatch, cookInBatch, false);
	}

	/**
	 * Get the prompt string when user order food item.
	 * 
	 * @return
	 */
	public String getSalePrompt() {
		return "How many serves of fries would you like to buy: ";
	}
}

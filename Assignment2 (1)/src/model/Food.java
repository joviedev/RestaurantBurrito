package model;
import java.io.Serializable;

/**
 * Define class for food item sold in this restaurant.
 */
public abstract class Food implements Serializable{
	private String name; // the name of this food
	private double price; // the price of this food
	private int timeTaken; // the number of minutes taken to prepare this food
	private int numInBatch; // the number of items cooked in batches
	private int numRemains; // the number of remain food
	private boolean cookInBatch; // flag for if this food is cooked in batches
	private int soldQuantity; // the sold quantity
	private double soldAmount; // the amount of sales
	private boolean isVirutal; // true for meal, else false

	public Food(String name, double price, int timeTaken, int numInBatch, boolean cookInBatch) {
		this(name, price, timeTaken, numInBatch, cookInBatch, false);
	}

	public Food(String name, double price, int timeTaken, int numInBatch, boolean cookInBatch, boolean isVirutal) {
		this.name = name;
		this.price = price;
		this.timeTaken = timeTaken;
		this.numInBatch = numInBatch;
		this.cookInBatch = cookInBatch;
		this.isVirutal = isVirutal;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getTimeTaken() {
		return timeTaken;
	}

	public void setTimeTaken(int timeTaken) {
		this.timeTaken = timeTaken;
	}

	public int getNumInBatch() {
		return numInBatch;
	}

	public void setNumInBatch(int numInBatch) {
		this.numInBatch = numInBatch;
	}

	public int getNumRemains() {
		return numRemains;
	}

	public void setNumRemains(int numRemains) {
		this.numRemains = numRemains;
	}

	public boolean isCookInBatch() {
		return cookInBatch;
	}

	public void setCookInBatch(boolean cookInBatch) {
		this.cookInBatch = cookInBatch;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof Food)) {
			return false;
		}
		Food other = (Food) obj;
		return other.getName().equals(this.getName());
	}

	public int getSoldQuantity() {
		return soldQuantity;
	}

	public void setSoldQuantity(int soldQuantity) {
		this.soldQuantity = soldQuantity;
	}

	public double getSoldAmount() {
		return soldAmount;
	}

	public void setSoldAmount(double soldAmount) {
		this.soldAmount = soldAmount;
	}

	public String toString() {
		return name;
	}

	public boolean isVirutal() {
		return isVirutal;
	}

	public void setVirutal(boolean isVirutal) {
		this.isVirutal = isVirutal;
	}

	/**
	 * Get the prompt string when user order food item.
	 * 
	 * @return prompt message
	 */
	public String getSalePrompt() {
		return String.format("How many %s%s would you like to buy: ", name.toLowerCase(),
				name.toLowerCase().endsWith("s") ? "" : "s");
	}
}

package model;
import java.io.Serializable;

/**
 * Define class for order item in customer's order.
 */
public class OrderItem implements Serializable{
	private static final long serialVersionUID = 1L;
	private Food food;
	private double price;
	private int quantity;

	public OrderItem(Food food, double price, int quantity) {
		this.food = food;
		this.price = price;
		this.quantity = quantity;
	}

	public Food getFood() {
		return food;
	}

	public void setFood(Food food) {
		this.food = food;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	public String getName() {
		return this.food.getName();
	}

	/**
	 * Check if tow order item is equal.
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof OrderItem)) {
			return false;
		} else {
			OrderItem item = (OrderItem) obj;
			return item.getFood().equals(this.getFood()) && item.getPrice() == this.getPrice();
		}
	}
}

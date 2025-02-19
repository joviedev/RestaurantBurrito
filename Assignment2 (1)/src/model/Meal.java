package model;
import java.util.List;

/*
 * A meal, which is 1 burrito, 1 serve of fries and 1 soda, at a price that discounts each item in a meal by $1
 */
public class Meal extends Food {
	private List<Food> foodList;

	public Meal(double price, int timeTaken, int numInBatch) {
		super("Meal", price, timeTaken, numInBatch, false, true);
	}
	
	public void setFoodList(List<Food> foodList) {
		this.foodList = foodList;
	}

	public List<Food> getFoodList() {
		return foodList;
	}
	
	public double getPrice() {
		double price = 0;
		for(Food f : foodList) {
			price += f.getPrice() - 1;
		}
		return price;
	}
}


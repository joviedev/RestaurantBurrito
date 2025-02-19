package model;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Define class for user's order.
 */
public class Order implements Serializable{
	private List<OrderItem> orderItems = new ArrayList<OrderItem>();
	private List<OrderItem> userOrderedItems = new ArrayList<OrderItem>();
	private String status = "await for collection";
	private Date placedTime;
	private String orderNo;
	private User user;
	private int timeToPrepare;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:MM");
	private CreditCard paymentCreditCard;

	/**
	 * Record user ordered items.
	 * 
	 * @param food     the food user ordered
	 * @param quantity the quantity of the food
	 */
	public void addUserOrderedItems(Food food, double price, int quantity) {
		userOrderedItems.add(new OrderItem(food, price, quantity));
	}

	public void addOrderItem(OrderItem item) {
		this.orderItems.add(item);
	}
	
	/**
	 * Add real order item for one order.
	 * @param food the orderd food item
	 * @param price the price of the item
	 * @param quantity the quantity of the item
	 */
	public void addOrderItem(Food food, double price, int quantity) {
		OrderItem newItem = new OrderItem(food, price, quantity);
		for (OrderItem item : orderItems) {
			if (newItem.equals(item)) {
				item.setQuantity(item.getQuantity() + quantity);
				return;
			}
		}
		orderItems.add(newItem);
	}

	/**
	 * Get order item list.
	 * 
	 * @return order item list
	 */
	public List<OrderItem> getOrderItems() {
		return orderItems;
	}

	/**
	 * Get total amount to pay for this order.
	 * 
	 * @return total amount of this order
	 */
	public double getTotalAmount() {
		double total = 0;
		List<OrderItem> items = getRealOrderItems();
		for (OrderItem item : items) {
			total += item.getPrice() * item.getQuantity();
		}
		return total;
	}

	/**
	 * Get the total order quantity for the given item.
	 * 
	 * @param item ordered item
	 * @return the order quantity
	 */
	public int getQuantity(Food orderItem) {
		int qty = 0;
		if(orderItem.isVirutal()) {
			for (OrderItem item : this.orderItems) {
				if (orderItem.equals(item.getFood())) {
					qty += item.getQuantity();
				}
			}
			return qty;
		}
		List<OrderItem> items = getRealOrderItems();
		for (OrderItem item : items) {
			if (orderItem.equals(item.getFood())) {
				qty += item.getQuantity();
			}
		}
		return qty;
	}

	/**
	 * Get real order items after get real food in meal.
	 */
	public List<OrderItem> getRealOrderItems() {
		List<OrderItem> realOrderItems = new ArrayList<OrderItem>(); // get real food from meal
		for(OrderItem item : orderItems) {
			if(item.getFood().isVirutal()) {
				// get real food in the meal
				for(Food f : ((Meal)item.getFood()).getFoodList()) {
					realOrderItems.add(new OrderItem(f, f.getPrice() - 1, item.getQuantity()));
				}
			} else {
				realOrderItems.add(item);
			}
		}
		return realOrderItems;
	}

	/**
	 * Check out the order and return the number of minutes wait for this order.
	 * 
	 * @return the number of minutes wait for this order
	 */
	public int checkOut() {
		List<Food> visitedList = new ArrayList<Food>();
		int timeTaken = 0;
		List<OrderItem> realOrderItems = getRealOrderItems();
		for (OrderItem item : realOrderItems) {
			// get time taken for this food if it has not be calculated
			if (!visitedList.contains(item.getFood())) {
				visitedList.add(item.getFood());
				Food food = item.getFood();
				// get total quantity for this food in this order
				int quantity = getQuantity(item.getFood());
				int minutes = ((quantity - food.getNumRemains() + food.getNumInBatch() - 1) / food.getNumInBatch())
						* food.getTimeTaken();
				if (minutes > timeTaken) {
					timeTaken = minutes;
				}
				// update the number of remains for this food
				if (food.isCookInBatch()) {
					food.setNumRemains(getNumRemains(food));
				}
			}
		}
		timeToPrepare = timeTaken;
		return timeTaken;
	}

	/**
	 * Get the number of remains when order the given quantity for this food.
	 * @param food ordered food
	 * @param orderQuantity order quantity
	 * @return the number of remains for this food
	 */
	public int getNumRemains(Food food) {
		if(!food.isCookInBatch()) {
			return 0;
		}
		int orderQuantity = getQuantity(food); //get total quantity for this food
		int remainNum = food.getNumRemains()
				+ food.getNumInBatch()
						* ((orderQuantity - food.getNumRemains() + food.getNumInBatch() - 1) / food.getNumInBatch())
				- orderQuantity;
		return remainNum;
	}
	
	public List<OrderItem> getUserOrderedItems() {
		return userOrderedItems;
	}

	public void setUserOrderedItems(List<OrderItem> userOrderedItems) {
		this.userOrderedItems = userOrderedItems;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Date getPlacedTime() {
		return placedTime;
	}

	public String getPlacedTimeFormatted() {
		return dateFormat.format(placedTime);
	}
	
	public void setPlacedTime(Date placedTime) {
		this.placedTime = placedTime;
	}
	
	public String getFoodItems() {
		String items = "";
		List<Food> visitedList = new ArrayList<Food>();
		for (OrderItem item : orderItems) {
			// get time taken for this food if it has not be calculated
			if (!visitedList.contains(item.getFood())) {
				visitedList.add(item.getFood());
				Food food = item.getFood();
				// get total quantity for this food in this order
				int quantity = getQuantity(item.getFood());
				if(items.length() > 0) {
					items += ", ";
				}
				items += quantity + " " + food.getName();
			}
		}
		return items;
	}
	
	public OrderItem getOrderItem(String foodName) {
		for(OrderItem item : orderItems) {
			if(item.getName().equals(foodName)) {
				return item;
			}
		}
		return null;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getTimeToPrepare() {
		return timeToPrepare;
	}
	
	public String getReadyTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.placedTime);
		calendar.add(Calendar.MINUTE, this.timeToPrepare);
		return new SimpleDateFormat("HH:mm").format(calendar.getTime());
	}

	public CreditCard getPaymentCreditCard() {
		return paymentCreditCard;
	}

	public void setPaymentCreditCard(CreditCard paymentCreditCard) {
		this.paymentCreditCard = paymentCreditCard;
	}
}


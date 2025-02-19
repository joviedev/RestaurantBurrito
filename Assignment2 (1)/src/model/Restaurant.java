package model;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * Restaurant contains a list of Food objects representing the menu items
 * available for ordering. It also has methods to display the menu, take orders
 * from customers, process payments, and handle any exceptions that may occur
 * during the ordering process.
 */
public class Restaurant implements Serializable {
	private transient Scanner scanner = new Scanner(System.in);
	private String name;
	private List<User> users = new ArrayList<User>();

	private Food[] foodItems = { new Burrito(7, 9, 2, false), new Fries(4, 8, 5, true),
			new Soda(2.5, 0, 1, false), new Meal(0.0, 0, 0) };
	private List<Order> orderList = new ArrayList<Order>();
	private Order order;
	private Order shoppingBasket;
	private User curLoginUser;
	private static Restaurant instance = null; // use Singleton pattern to create Restaurant
	
	private Restaurant(String name) {
		this.name = name;
		List<Food> foodList = new ArrayList<Food>();
		// A meal, which is 1 burrito, 1 serve of fries and 1 soda
		foodList.add(foodItems[0]);
		foodList.add(foodItems[1]);
		foodList.add(foodItems[2]);
		((Meal)foodItems[3]).setFoodList(foodList);
	}
	
	public static Restaurant getInstance() {
		if(instance == null) {
			instance = new Restaurant("Burrito King");
		}
		return instance;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Display menu and return user selected item.
	 * 
	 * @return user selected menu item
	 */
	public char getOption() {
		char option = '0';
		System.out.println("    ===============================================================");
		System.out.printf("    %s\n", this.getName());
		System.out.println("    ===============================================================");
		System.out.println("a) Order");
		System.out.println("b) Show sales report");
		System.out.println("c) Update prices");
		System.out.println("d) Exit");
		System.out.print("Please select: ");
		do {
			try {
				option = scanner.nextLine().toLowerCase().charAt(0);
				if (!(option >= 'a' && option <= 'd')) {
					throw new InvalidOptionException("Invalid option");
				}
			} catch (InvalidOptionException e1) {
				System.out.printf("%s, please enter again: ", e1.getMessage());
			} catch (StringIndexOutOfBoundsException e2) {
				System.out.printf("Cannot be empty, please enter again: ");
			}
		} while (!(option >= 'a' && option <= 'd'));
		return option;
	}

	/**
	 * Create new order.
	 */
	public Order newOrder() {
		order = new Order();
		orderList.add(order);
		return order;
	}

	/**
	 * Get current order.
	 */
	public Order getCurrentOrder() {
		return order;
	}

	/**
	 * Order new item.
	 */
	public void addOrderItem(Food food, int quantity, double discount) {
		if (order == null) {
			newOrder();
		}
		if (discount == 0) {
			order.addUserOrderedItems(food, food.getPrice(), quantity);
		}
		// order a meal, which is 1 burrito, 1 serve of fries and 1 sod,
		// at a price that discounts each item in a meal by $1
		if (food.getName().equalsIgnoreCase("meal")) {
			for (int i = 0; i < (foodItems.length - 1); i++) {
				this.addOrderItem(foodItems[i], quantity, 1);
			}
			return;
		}
		order.addOrderItem(food, food.getPrice() - discount, quantity);
	}

	public Food[] getFoodItems() {
		return foodItems;
	}

	/**
	 * Update sales for food items.
	 */
	public void updateSales() {
		for (Food food : foodItems) {
			food.setSoldAmount(0);
			food.setSoldQuantity(0);
			for (Order order : orderList) {
				// update every food item's sale record
				for (OrderItem item : order.getOrderItems()) {
					if (item.getFood().equals(food)) {
						food.setSoldAmount(food.getSoldAmount() + item.getPrice() * item.getQuantity());
						food.setSoldQuantity(food.getSoldQuantity() + item.getQuantity());
					}
				}
			}
		}
	}

	/**
	 * Checkout the order.
	 */
	private void checkOut(Order order) {
		if (order.getOrderItems().isEmpty()) {
			System.out.println("No order items.");
			return;
		}
		System.out.print("Total for ");
		double totalAmount = order.getTotalAmount();
		int i = 0;
		// display items in this order
		List<OrderItem> items = order.getUserOrderedItems();
		for (OrderItem item : items) {
			if (i > 0 && i < (items.size() - 1)) {
				System.out.print(", ");
			} else if (i > 0 && i == (items.size() - 1)) {
				System.out.print(" and ");
			}
			System.out.printf("%d %s", item.getQuantity(), item.getFood().getName());
			i++;
		}
		System.out.printf(" is $%s.\n", new DecimalFormat("#.##").format(totalAmount));
		if (totalAmount <= 0) {
			return;
		}
		// ask user enter money for this order
		double money;
		do {
			System.out.print("Please enter money: ");
			try {
				money = Double.parseDouble(scanner.nextLine());
				if (money < totalAmount) {
					throw new InsufficientPaymentException("Sorry, that's not enough to pay for order");
				}
			} catch (NumberFormatException e1) {
				money = -1;
				System.out.println("Invalid number!");
			} catch (InsufficientPaymentException e2) {
				money = -1;
				System.out.println(e2.getMessage());
			}
			if (money > totalAmount) {
				System.out.println("Change returned $" + new DecimalFormat("#.##").format(money - totalAmount));
			}
		} while (money < totalAmount);
		System.out.printf("Time taken: %d minutes\n", order.checkOut());
	}

	/**
	 * Ask user select food in an order.
	 */
	public void selectFood() {
		Order order = newOrder();
		Food item;
		int qty;
		do {
			item = selectFoodItem(true);
			if (item == null) {
				checkOut(order);
			} else {
				System.out.println();
				System.out.print(item.getSalePrompt());
				do {
					qty = enterInt();
					if (qty <= 0) {
						System.out.print("Invalid quantity, please enter again: ");
					}
				} while (qty <= 0);
				if (item.isCookInBatch() && qty > item.getNumRemains()) {
					System.out.printf("Cooking %s; please be patient\n", item.getName().toLowerCase());
				}
				addOrderItem(item, qty, 0);
				// get the number of remains for this food
				int numRemains = order.getNumRemains(item);
				if (numRemains > 0) {
					System.out.printf("%d serves of %s left for next order\n", numRemains,
							item.getName().toLowerCase());
				}
			}
		} while (item != null);
	}

	/**
	 * Display sales reports for total sales.
	 */
	public void showSalesReport() {
		System.out.println();
		// display how many servings of fries remain
		for (Food food : getFoodItems()) {
			if (food.isCookInBatch() && food.getNumRemains() > 0) {
				System.out.printf("Unsold Serves of %s: %d\n", food.getName(), food.getNumRemains());
			}
		}
		System.out.println();
		System.out.println("Total Sales:");
		int totalQty = 0;
		double totalAmount = 0;
		updateSales();
		for (Food item : getFoodItems()) {
			if (!item.isVirutal()) {
				System.out.printf("%-9s%-8d$%.2f\n", item.getName() + ":", item.getSoldQuantity(),
						item.getSoldAmount());
				totalQty += item.getSoldQuantity();
				totalAmount += item.getSoldAmount();
			}
		}
		System.out.println("_________________________");
		System.out.printf("%-9s%-8d$%.2f\n", "", totalQty, totalAmount);
	}

	/**
	 * Ask user enter integer number, enter again if it's invalid number.
	 * 
	 * @return user entered number
	 */
	private int enterInt() {
		Integer n;
		do {
			try {
				n = Integer.parseInt(scanner.nextLine());
			} catch (Exception e) {
				n = null;
				System.out.print("Invalid number, please enter again: ");
			}
		} while (n == null);
		return n;
	}

	/**
	 * Display food item list and ask user select one food.
	 * 
	 * @param isDisplayVirtual
	 *            display meal in the menu list if it's true
	 * @return index of one food item
	 */
	public Food selectFoodItem(boolean isDisplayVirtual) {
		System.out.println();
		System.out.println("    > Select the food item");
		int i;
		int itemNum = 0;
		// display all available food item list
		for (i = 1; i <= getFoodItems().length; i++) {
			if (isDisplayVirtual || !getFoodItems()[i - 1].isVirutal()) {
				System.out.printf("%d. %s\n", i, getFoodItems()[i - 1]);
				itemNum++;
			}
		}
		System.out.printf("%d. No more\n", itemNum + 1);
		// ask user select one food item's index
		System.out.print("Please select: ");
		do {
			try {
				i = enterInt();
				if (!(i >= 1 && i <= (itemNum + 1))) {
					throw new InvalidOptionException("Invliad item");
				}
			} catch (InvalidOptionException e) {
				System.out.printf("%s, please select again: ", e.getMessage());
			}
		} while (!(i >= 1 && i <= (itemNum + 1)));
		if (i == (itemNum + 1)) {
			return null;
		} else {
			return getFoodItems()[i - 1];
		}
	}

	/**
	 * Update the selling price of items.
	 */
	public void updatePrices() {
		Food item;
		double price;
		do {
			item = selectFoodItem(false);
			// update the food item's price if the selected item is exist
			if (item != null) {
				System.out.print("Please enter new price: ");
				do {
					try {
						price = Double.parseDouble(scanner.nextLine());
						if (price < 1) {
							throw new InvalidPriceException("Price can not less than 1");
						}
					} catch (NumberFormatException e) {
						System.out.print("Invalid number, please enter again: ");
						price = -1;
					} catch (InvalidPriceException e1) {
						System.out.printf("%s, please enter again: ", e1.getMessage());
						price = -1;
					}
				} while (price < 0);
				item.setPrice(price);
				System.out.printf("Successfully updated the price of %s to $%s\n", item.getName(),
						new DecimalFormat("#.##").format(price));
			}
		} while (item != null);
	}

	/**
	 * Displays the menu for Burrito King Restaurant.
	 */
	public void run() {
		char option = '0';
		// display the menu and ask user select one menu item until user enter d
		// to exit
		while (option != 'd') {
			option = getOption();
			if (option == 'a') {
				selectFood();
			} else if (option == 'b') {
				showSalesReport();
			} else if (option == 'c') {
				updatePrices();
			}
			if (option == 'd') {
				System.out.println("Bye Bye.");
			} else {
				System.out.println();
			}
		}
	}

	private void saveObject(Object obj, String fileName) {
		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileName));
			outputStream.writeObject(obj);
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Save data into file.
	 */
	public void saveData() {
		saveObject(this, "restaurant.data");
	}

	/**
	 * Read data from file.
	 */
	public static Restaurant readData() {
		try {
			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("restaurant.data"));
			return (Restaurant) inputStream.readObject();
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * Add user account.
	 */
	public void addUser(User user) {
		this.users.add(user);
	}

	/**
	 * Get user by username.
	 */
	public User getUserByName(String username) {
		for (User u : users) {
			if (u.getUsername().equals(username)) {
				return u;
			}
		}
		return null;
	}

	public Order getShoppingBasket() {
		if (shoppingBasket == null) {
			shoppingBasket = new Order();
		}
		return shoppingBasket;
	}

	public void setShoppingBasket(Order shoppingBasket) {
		this.shoppingBasket = shoppingBasket;
	}

	public Order placeOrder() {
		Order order = shoppingBasket;
		shoppingBasket.setStatus("await for collection"); // await for collection, placed
		shoppingBasket.setUser(curLoginUser);
		Date time = Calendar.getInstance().getTime();
		shoppingBasket.setOrderNo(new SimpleDateFormat("yyyyMMddHHmmss").format(time));
		shoppingBasket.setPlacedTime(time);
		this.orderList.add(shoppingBasket);
		shoppingBasket = null;
		return order;
	}

	public User getCurLoginUser() {
		return curLoginUser;
	}

	public void setCurLoginUser(User curLoginUser) {
		this.curLoginUser = curLoginUser;
	}

	public List<Order> getOrderList(String status) {
		List<Order> list = new ArrayList<Order>();
		for (Order order : orderList) {
			if (order.getUser().equals(curLoginUser) && (status == null || order.getStatus().equals(status))) {
				list.add(order);
			}
		}
		return list;
	}
	
	public void updateOrderStatus(Order order, String status) {
		for(Order d : orderList) {
			if(d.getOrderNo().equals(order.getOrderNo())) {
				d.setStatus(status);
			}
		}
	}
	
	public void resetOrderList() {
		this.orderList = new ArrayList<Order>();
		this.order = null;
	}
}

package test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import model.Burrito;
import model.Food;
import model.Fries;
import model.Order;
import model.Restaurant;
import model.Soda;

/**
 * JUnit test cases for PartB by using JUnit4.
 */
public class UnitTest {

	@Test
	public void testGetTotalAmount() {
		Order order = new Order();
		order.addOrderItem(new Burrito(7, 9, 2, false), 7, 2);
		order.addOrderItem(new Fries(4, 8, 5, true), 3, 4);
		double expectedResult = 26;
		assertEquals(expectedResult, order.getTotalAmount(), 0.01);
	}

	@Test
	public void testGetQuantity() {
		Order order = new Order();
		Food food = new Burrito(7, 9, 2, false);
		order.addOrderItem(food, 7, 2);
		order.addOrderItem(new Fries(4, 8, 5, true), 3, 4);
		order.addOrderItem(food, 6, 3);
		int expectedResult = 5;
		assertEquals(expectedResult, order.getQuantity(food));
	}

	@Test
	public void testGetTimeTakenWaitBurrito1() {
		Order order = new Order();
		Food food1 = new Burrito(7, 9, 2, false);
		Food food2 = new Fries(4, 8, 5, true);
		Food food3 = new Soda(2.5, 0, 1, false);
		order.addOrderItem(food1, 7, 2);
		order.addOrderItem(food2, 6, 3);
		order.addOrderItem(food3, 2.5, 4);
		int expectedResult = 9;
		assertEquals(expectedResult, order.checkOut());
	}

	@Test
	public void testGetTimeTakenWaitBurrito2() {
		Order order = new Order();
		Food food1 = new Burrito(7, 9, 2, false);
		Food food2 = new Fries(4, 8, 5, true);
		Food food3 = new Soda(2.5, 0, 1, false);
		order.addOrderItem(food1, 7, 3);
		order.addOrderItem(food2, 6, 3);
		order.addOrderItem(food3, 2.5, 4);
		int expectedResult = 18;
		assertEquals(expectedResult, order.checkOut());
	}

	@Test
	public void testGetTimeTakenWaitBurrito3() {
		Order order = new Order();
		Food food1 = new Burrito(7, 9, 2, false);
		Food food2 = new Fries(4, 8, 5, true);
		Food food3 = new Soda(2.5, 0, 1, false);
		order.addOrderItem(food1, 7, 3);
		order.addOrderItem(food2, 6, 10);
		order.addOrderItem(food3, 2.5, 4);
		int expectedResult = 18;
		assertEquals(expectedResult, order.checkOut());
	}

	@Test
	public void testGetTimeTakenWaitFries() {
		Order order = new Order();
		Food food1 = new Burrito(7, 9, 2, false);
		Food food2 = new Fries(4, 8, 5, true);
		Food food3 = new Soda(2.5, 0, 1, false);
		order.addOrderItem(food1, 7, 3);
		order.addOrderItem(food2, 6, 11);
		order.addOrderItem(food3, 2.5, 4);
		int expectedResult = 24;
		assertEquals(expectedResult, order.checkOut());
	}

	@Test
	public void testGetTimeTakenInstantly() {
		Order order = new Order();
		Food food1 = new Burrito(7, 9, 2, false);
		Food food2 = new Fries(4, 8, 5, true);
		Food food3 = new Soda(2.5, 0, 1, false);
		order.addOrderItem(food3, 2.5, 5);
		int expectedResult = 0;
		assertEquals(expectedResult, order.checkOut());
	}

	@Test
	public void testAddOrderItemNoRemains() {
		Restaurant restaurant = Restaurant.getInstance();
		Food food1 = new Burrito(7, 9, 2, false);
		Food food2 = new Fries(4, 8, 5, true);
		Food food3 = new Soda(2.5, 0, 1, false);
		restaurant.addOrderItem(food1, 3, 0);
		int expectedResult = 0;
		assertEquals(expectedResult, food1.getNumRemains());
	}

	@Test
	public void testAddOrderItemRemainFries1() {
		Restaurant restaurant = Restaurant.getInstance();
		Food food1 = new Burrito(7, 9, 2, false);
		Food food2 = new Fries(4, 8, 5, true);
		Food food3 = new Soda(2.5, 0, 1, false);
		restaurant.addOrderItem(food2, 3, 0);
		int expectedResult = 2;
		restaurant.getCurrentOrder().checkOut();
		assertEquals(expectedResult, food2.getNumRemains());
	}

	@Test
	public void testAddOrderItemNoRemains1() {
		Restaurant restaurant = Restaurant.getInstance();
		Food food1 = new Burrito(7, 9, 2, false);
		Food food2 = new Fries(4, 8, 5, true);
		Food food3 = new Soda(2.5, 0, 1, false);
		restaurant.addOrderItem(food3, 3, 0);
		int expectedResult = 0;
		assertEquals(expectedResult, food3.getNumRemains());
	}

	@Test
	public void testAddOrderItemRemainFries2() {
		Restaurant restaurant = Restaurant.getInstance();
		restaurant.resetOrderList();
		Food food2 = new Fries(4, 8, 5, true);
		restaurant.addOrderItem(food2, 8, 0);
		int expectedResult = 2;
		restaurant.getCurrentOrder().checkOut();
		assertEquals(expectedResult, food2.getNumRemains());
	}

	@Test
	public void testAddOrderItemRemainFries3() {
		Restaurant restaurant = Restaurant.getInstance();
		restaurant.resetOrderList();
		Food food2 = new Fries(4, 8, 5, true);
		restaurant.addOrderItem(food2, 2, 0);
		int expectedResult = 3;
		restaurant.getCurrentOrder().checkOut();
		assertEquals(expectedResult, food2.getNumRemains());
	}

	@Test
	public void testGetSalePrompt() {
		Restaurant restaurant = Restaurant.getInstance();
		Food food1 = new Burrito(7, 9, 2, false);
		Food food2 = new Fries(4, 8, 5, true);
		Food food3 = new Soda(2.5, 0, 1, false);
		assertEquals("How many burritos would you like to buy: ", food1.getSalePrompt());
		assertEquals("How many serves of fries would you like to buy: ", food2.getSalePrompt());
	}

}

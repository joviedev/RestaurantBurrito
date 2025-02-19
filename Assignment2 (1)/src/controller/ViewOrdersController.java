package controller;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.stage.Stage;
import model.Order;
import model.Restaurant;
import view.ViewOrdersView;

/**
 * Controller for view all orders displayed in the reverse order of time they were placed.  
 */
public class ViewOrdersController {
	private Restaurant restaurant;
	private Stage parent;
	private ViewOrdersView viewOrdersView;

	public ViewOrdersController(Restaurant rest, Stage parent, ViewOrdersView viewOrdersView) {
		this.restaurant = rest;
		this.viewOrdersView = viewOrdersView;
		viewOrdersView.getCloseButton().setOnAction(e->{
			viewOrdersView.getPrimaryStage().close();
		});
		viewOrdersView.getPrimaryStage().setOnCloseRequest(e -> {
			restaurant.saveData();
		});
		updateOrders();
		viewOrdersView.getPrimaryStage().showAndWait();
	}

	private void updateOrders() {
		List<Order> orders = restaurant.getOrderList(null);
		Collections.sort(orders, new Comparator<Order>() {
			public int compare(Order arg0, Order arg1) {
				return arg1.getPlacedTime().compareTo(arg0.getPlacedTime());
			}
		});
		viewOrdersView.getOrderTable().setItems(FXCollections.observableArrayList(orders));
		viewOrdersView.getOrderTable().refresh();
	}
}

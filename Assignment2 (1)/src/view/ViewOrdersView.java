package view;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Order;
import model.Restaurant;

/**
 * View all orders displayed in the reverse order of time they were placed.  
 */
public class ViewOrdersView {
	private TableView<Order> orderTable = new TableView<Order>();
	private Restaurant restaurant;
	private Stage primaryStage;
	private Stage parent;
	private Button closeButton;

	public ViewOrdersView(Restaurant rest, Stage parent) {
		primaryStage = new Stage();
		this.restaurant = rest;
		primaryStage = new Stage();
		primaryStage.initModality(Modality.APPLICATION_MODAL);
		primaryStage.initOwner(parent);
		primaryStage.setTitle("View all orders");
		FlowPane buttonPane = new FlowPane();
		buttonPane.setHgap(20);

		VBox contentBox = new VBox();

		contentBox.setPadding(new Insets(5, 5, 5, 5));
		contentBox.setSpacing(10);

		// create table for display active order
		orderTable.setPrefHeight(270);
		TableColumn<Order, String> orderNoCol = new TableColumn<>("Order Number");
		orderNoCol.prefWidthProperty().bind(orderTable.widthProperty().multiply(0.17));
		orderNoCol.setCellValueFactory(new PropertyValueFactory<Order, String>("orderNo"));

		TableColumn<Order, String> placeTimeCol = new TableColumn<>("Placed Time");
		placeTimeCol.prefWidthProperty().bind(orderTable.widthProperty().multiply(0.2));
		placeTimeCol.setCellValueFactory(new PropertyValueFactory<Order, String>("placedTimeFormatted"));
		
		TableColumn<Order, String> itemsCol = new TableColumn<>("Ordered Food Items");
		itemsCol.prefWidthProperty().bind(orderTable.widthProperty().multiply(0.3));
		itemsCol.setCellValueFactory(new PropertyValueFactory<Order, String>("foodItems"));

		TableColumn<Order, String> priceCol = new TableColumn<>("Total Price");
		priceCol.prefWidthProperty().bind(orderTable.widthProperty().multiply(0.1));
		priceCol.setCellValueFactory(new PropertyValueFactory<Order, String>("totalAmount"));

		TableColumn<Order, String> statusCol = new TableColumn<>("Order Status");
		statusCol.prefWidthProperty().bind(orderTable.widthProperty().multiply(0.25));
		statusCol.setCellValueFactory(new PropertyValueFactory<Order, String>("Status"));
		orderTable.getColumns().addAll(orderNoCol, placeTimeCol, itemsCol, priceCol, statusCol);

		closeButton = new Button("Close");
		closeButton.setTranslateX(300);
		closeButton.setOnAction(e->{
			primaryStage.close();
		});
		contentBox.getChildren().addAll(orderTable, closeButton);
		primaryStage.setOnCloseRequest(e -> {
			restaurant.saveData();
		});
		orderTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		primaryStage.setScene(new Scene(contentBox, 730, 400));
	}

	public TableView<Order> getOrderTable() {
		return orderTable;
	}

	public void setOrderTable(TableView<Order> orderTable) {
		this.orderTable = orderTable;
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public Button getCloseButton() {
		return closeButton;
	}

	public void setCloseButton(Button closeButton) {
		this.closeButton = closeButton;
	}
}


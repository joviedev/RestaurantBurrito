package view;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Order;
import model.Restaurant;

/**
 * Export the historical orders to a file, the user can select which orders and columns to export.
 */
public class ExportOrdersView {
	private TableView<Order> orderTable = new TableView<Order>();
	private Restaurant restaurant;
	private Stage primaryStage;
	private Stage parent;
	private CheckBox orderItemsCheckBox;
	private CheckBox priceCheckBox;
	private CheckBox statusCheckBox;
	private Button buttonExport;

	public ExportOrdersView(Restaurant rest, Stage parent) {
		primaryStage = new Stage();
		this.restaurant = rest;
		primaryStage = new Stage();
		primaryStage.initModality(Modality.APPLICATION_MODAL);
		primaryStage.initOwner(parent);
		primaryStage.setTitle("Export all orders");
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

		TableColumn<Order, String> itemsCol = new TableColumn<>("Ordered Food Items");
		itemsCol.prefWidthProperty().bind(orderTable.widthProperty().multiply(0.5));
		itemsCol.setCellValueFactory(new PropertyValueFactory<Order, String>("foodItems"));

		TableColumn<Order, String> priceCol = new TableColumn<>("Total Price");
		priceCol.prefWidthProperty().bind(orderTable.widthProperty().multiply(0.1));
		priceCol.setCellValueFactory(new PropertyValueFactory<Order, String>("totalAmount"));

		TableColumn<Order, String> statusCol = new TableColumn<>("Order Status");
		statusCol.prefWidthProperty().bind(orderTable.widthProperty().multiply(0.25));
		statusCol.setCellValueFactory(new PropertyValueFactory<Order, String>("Status"));
		orderTable.getColumns().addAll(orderNoCol, itemsCol, priceCol, statusCol);

		HBox hbox1 = new HBox();
		buttonExport = new Button("Export");
		hbox1.setSpacing(10);
		Label labelColumns = new Label("Export columns: ");
		orderItemsCheckBox = new CheckBox("Order Items");
		priceCheckBox = new CheckBox("Price");
		statusCheckBox = new CheckBox("Status");
		orderItemsCheckBox.setSelected(true);
		priceCheckBox.setSelected(true);
		statusCheckBox.setSelected(true);
		hbox1.getChildren().addAll(labelColumns, orderItemsCheckBox, priceCheckBox, statusCheckBox, buttonExport);
		contentBox.getChildren().addAll(orderTable, hbox1);
		primaryStage.setOnCloseRequest(e -> {
			restaurant.saveData();
		});
		orderTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		primaryStage.setScene(new Scene(contentBox, 700, 400));
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

	public CheckBox getOrderItemsCheckBox() {
		return orderItemsCheckBox;
	}

	public void setOrderItemsCheckBox(CheckBox orderItemsCheckBox) {
		this.orderItemsCheckBox = orderItemsCheckBox;
	}

	public CheckBox getPriceCheckBox() {
		return priceCheckBox;
	}

	public void setPriceCheckBox(CheckBox priceCheckBox) {
		this.priceCheckBox = priceCheckBox;
	}

	public CheckBox getStatusCheckBox() {
		return statusCheckBox;
	}

	public void setStatusCheckBox(CheckBox statusCheckBox) {
		this.statusCheckBox = statusCheckBox;
	}

	public Button getButtonExport() {
		return buttonExport;
	}

	public void setButtonExport(Button buttonExport) {
		this.buttonExport = buttonExport;
	}
}


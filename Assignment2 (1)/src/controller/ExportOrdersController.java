package controller;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.SelectionMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Order;
import model.Restaurant;
import view.ExportOrdersView;

/**
 * Controller for export the historical orders to a file, the user can select which orders and columns to export.
 */
public class ExportOrdersController {
	private Restaurant restaurant;
	private Stage parent;
	private ExportOrdersView exportOrdersView;

	public ExportOrdersController(Restaurant rest, Stage parent, ExportOrdersView exportOrdersView) {
		this.exportOrdersView = exportOrdersView;
		this.restaurant = rest;

		exportOrdersView.getButtonExport().setOnAction(e->{
			exportOrders();
		});
		exportOrdersView.getPrimaryStage().setOnCloseRequest(e -> {
			restaurant.saveData();
		});
		exportOrdersView.getOrderTable().getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		updateOrders();
		exportOrdersView.getPrimaryStage().showAndWait();
	}

	public void exportOrders() {
		ObservableList<Order> selectOrders = exportOrdersView.getOrderTable().getSelectionModel().getSelectedItems();
		if(selectOrders.size() == 0) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setHeaderText("Warning");
			alert.setContentText("Please select orders in the table to export!");
			alert.show();
		} else {
			FileChooser chooser = new FileChooser();
			File file = chooser.showSaveDialog(parent);
			if(file == null) {
				return;
			} else {
				try {
					PrintWriter writer = new PrintWriter(file);
					//write header
					writer.print("OrderNumber");
					if(exportOrdersView.getOrderItemsCheckBox().isSelected()) {
						writer.print("," + "OrderItems");	
					}
					if(exportOrdersView.getPriceCheckBox().isSelected()) {
						writer.print("," + "Price");	
					}
					if(exportOrdersView.getStatusCheckBox().isSelected()) {
						writer.print("," + "Status");	
					}
					writer.println();
					// save data
					for(Order d : selectOrders) {
						writer.print(d.getOrderNo());
						if(exportOrdersView.getOrderItemsCheckBox().isSelected()) {
							writer.print("," + d.getFoodItems().replace(',', ';'));	
						}
						if(exportOrdersView.getPriceCheckBox().isSelected()) {
							writer.print("," + d.getTotalAmount());	
						}
						if(exportOrdersView.getStatusCheckBox().isSelected()) {
							writer.print("," + d.getStatus());	
						}
						writer.println();
					}
					writer.close();
					Alert alert = new Alert(Alert.AlertType.INFORMATION);
					alert.setHeaderText("Warning");
					alert.setContentText("Success export orders!");
					alert.show();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void updateOrders() {
		exportOrdersView.getOrderTable().setItems(FXCollections.observableArrayList(restaurant.getOrderList(null)));
		exportOrdersView.getOrderTable().refresh();
	}
}

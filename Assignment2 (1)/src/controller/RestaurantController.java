package controller;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Order;
import model.Restaurant;
import model.User;
import view.ExportOrdersView;
import view.RestaurantView;
import view.ShoppingBasketView;
import view.ViewOrdersView;

/**
 * Controller for RestaurantView
 */
public class RestaurantController {
	private User user;
	private Restaurant restaurant;
	private RestaurantView view;

	public RestaurantController(RestaurantView view) {
		this.view = view;
		Restaurant savedRestaurant = Restaurant.readData();
		if (savedRestaurant != null) {
			restaurant = savedRestaurant;
		} else {
			restaurant = Restaurant.getInstance();
		}

		view.getRegisterButton().setOnAction(event -> showRegisterWindow());
		view.getPrimaryStage().setOnCloseRequest(e -> {
			restaurant.saveData();
		});
		view.getCancelOrderButton().setOnAction(e -> {
			cancelOrder();
		});
		view.getCollectOrderButton().setOnAction(e -> {
			collectOrder();
		});

		// login the system
		view.getLoginButton().setOnAction(event -> {
			String username = view.getUsernameField().getText();
			String password = view.getPasswordField().getText();
			user = restaurant.getUserByName(username);
			if (user == null || !user.getPassword().equals(password)) {
				view.getErrorLabel().setText("Wrong username or password");
			} else {
				// display main window after success login
				view.getPrimaryStage().setScene(view.getMainScene());
				restaurant.setCurLoginUser(user);
				updateWelcomeLabel();
				updateActiveOrders();
				view.getErrorLabel().setText("");
				view.getPrimaryStage().setTitle(restaurant.getName() + " Restaurant");
			}
		});

		// display confirm dialog when log out
		view.getExitItem().setOnAction(e -> {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Confirm");
			alert.setHeaderText("Are you confirm to log out?");

			alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
			alert.showAndWait().ifPresent(response -> {
				if (response == ButtonType.YES) {
					restaurant.saveData();
					view.getPrimaryStage().close();
				}
			});
		});
		// display shopping basket window and update data
		view.getShoppingItem().setOnAction(e -> {
			// ShoppingBasketController
			ShoppingBasketView shoppingBasketView = new ShoppingBasketView(restaurant, view.getPrimaryStage());
			ShoppingBasketController shoppingBasketController = new ShoppingBasketController(restaurant, view.getPrimaryStage(),
					shoppingBasketView);
			updateActiveOrders();
			updateWelcomeLabel();
		});
		view.getEditProfileItem().setOnAction(e -> {
			showEditProfileWindow();
			updateWelcomeLabel();
		});
		view.getViewOrdersItem().setOnAction(e -> {
			ViewOrdersView viewOrdersView = new ViewOrdersView(restaurant, view.getPrimaryStage());
			new ViewOrdersController(restaurant, view.getPrimaryStage(), viewOrdersView);
		});
		view.getExportOrdersItem().setOnAction(e -> {
			ExportOrdersView exportOrdersView = new ExportOrdersView(restaurant, view.getPrimaryStage());
			new ExportOrdersController(restaurant, view.getPrimaryStage(), exportOrdersView);
		});
		view.getUpgradeVipItem().setOnAction(e -> {
			upgradeVip();
		});
	}

	/**
	 * Cancel selected order, set the order status to cancelled.
	 */
	private void cancelOrder() {
		Order order = view.getActiveTable().getSelectionModel().getSelectedItem();
		if (order == null) {
			showMessage("Error", "Please select order to cancel!");
			return;
		}
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Confirm");
		alert.setHeaderText("Are you confirm to cancel this order?");

		alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
		alert.showAndWait().ifPresent(response -> {
			if (response == ButtonType.YES) {
				order.setStatus("cancelled");
				showMessage("Success", "Success cancel the order!");
				updateActiveOrders();
			}
		});
	}

	/**
	 * Check if a valid time HH:MM
	 */
	private boolean isValidTime(String time) {
		if (!time.matches("[0-9]{2}:[0-9]{2}")) {
			return false;
		}
		String values[] = time.split(":");
		int hour = Integer.parseInt(values[0]);
		int minute = Integer.parseInt(values[1]);
		return hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59;
	}

	/**
	 * Collect the selected order, set the order status to collected.
	 */
	private void collectOrder() {
		Order order = view.getActiveTable().getSelectionModel().getSelectedItem();
		if (order == null) {
			showMessage("Error", "Please select order to collect!");
			return;
		}
		// ask user enter collect time
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Input");
		String inputTimeMessage = "Please enter collect time(HH:MM): ";
		dialog.setHeaderText(inputTimeMessage);
		String collectTime = dialog.showAndWait().orElse(null);
		while (collectTime != null && !isValidTime(collectTime)) {
			dialog.setHeaderText("Invalid time, " + inputTimeMessage);
			collectTime = dialog.showAndWait().orElse(null);
		}
		// user abort collect order
		if (collectTime == null) {
			return;
		}
		// do collect the order
		if (collectTime.compareTo(order.getReadyTime()) < 0) {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setHeaderText("Error");
			alert.setContentText("Food is not ready to collect.");
			alert.show();
			return;
		}
		order.setStatus("collected");
		updateActiveOrders();
	}

	private void updateWelcomeLabel() {
		String text = "Welcome " + user.getFirstName() + " " + user.getLastName();
		if (user.isVip()) {
			text += String.format(", VIP user, credit: %.2f ", restaurant.getCurLoginUser().getCredit());
		}
		text += ", active order:";
		view.getWelcomeLabel().setText(text);
	}

	private void updateActiveOrders() {
		view.getActiveTable().setItems(FXCollections.observableArrayList(restaurant.getOrderList("await for collection")));
		view.getActiveTable().refresh();
	}

	/**
	 * Asks if the user agrees to receive promotions: Would you like to receive
	 * promotion information via email? The user will become a VIP once they agree
	 * and provide a valid email address.
	 */
	private void upgradeVip() {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Confirm");
		alert.setHeaderText("Would you like to receive promotion information via email?");

		alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
		alert.showAndWait().ifPresent(response -> {
			if (response == ButtonType.YES) {
				// ask user enter email
				String email = "";
				boolean isValidEmail = false;
				String message = "Please enter email: ";
				do {
					TextInputDialog dialog = new TextInputDialog();
					dialog.setTitle("Input");
					dialog.setHeaderText(message);
					email = dialog.showAndWait().orElse(null);
					if (email == null) {
						return;
					}
					int index = email.indexOf("@");
					if (index > 0 && (index + 1) < email.length()) {
						isValidEmail = true;
						user.setVip(true);
					} else {
						message = "Invalid email, please enter again: ";
					}
				} while (!isValidEmail);
				updateWelcomeLabel();
			}
		});
	}

	private void showEditProfileWindow() {
		Stage editProfileStage = new Stage();
		editProfileStage.initModality(Modality.APPLICATION_MODAL);
		editProfileStage.initOwner(view.getPrimaryStage());
		editProfileStage.setTitle("Edit profile");

		GridPane registerLayout = new GridPane();
		registerLayout.setPadding(new Insets(10, 10, 10, 10));
		registerLayout.setVgap(5);
		registerLayout.setHgap(5);

		Label usernameLabel = new Label("Username: ");
		TextField usernameField = new TextField();
		usernameField.setText(user.getUsername());
		usernameField.setEditable(false);

		Label passwordLabel = new Label("Password: ");
		PasswordField passwordField = new PasswordField();
		passwordField.setText(user.getPassword());

		Label firstNameLabel = new Label("Firstname");
		TextField firstNameField = new TextField();
		firstNameField.setText(user.getFirstName());

		Label lastNameLabel = new Label("Lastname");
		TextField lastNameField = new TextField();
		lastNameField.setText(user.getLastName());

		HBox buttonBox = new HBox();
		buttonBox.setSpacing(20);
		Button updateButton = new Button("Update");
		Button cancelButton = new Button("Cancel");
		cancelButton.setOnAction(e -> {
			editProfileStage.close();
		});
		Label errorLabel = new Label();
		errorLabel.setTextFill(Color.RED);

		buttonBox.getChildren().addAll(updateButton, cancelButton);
		registerLayout.add(usernameLabel, 0, 0);
		registerLayout.add(usernameField, 1, 0);
		registerLayout.add(passwordLabel, 0, 1);
		registerLayout.add(passwordField, 1, 1);
		registerLayout.add(firstNameLabel, 0, 2);
		registerLayout.add(firstNameField, 1, 2);
		registerLayout.add(lastNameLabel, 0, 3);
		registerLayout.add(lastNameField, 1, 3);
		registerLayout.add(buttonBox, 1, 4);
		registerLayout.add(errorLabel, 1, 5);

		Scene registerScene = new Scene(registerLayout, 300, 200);
		editProfileStage.setScene(registerScene);

		updateButton.setOnAction(event -> {
			String username = usernameField.getText();
			String password = passwordField.getText();
			String firstName = firstNameField.getText();
			String lastName = lastNameField.getText();
			if (!username.isEmpty() && !password.isEmpty() && !firstName.isEmpty() && !lastName.isEmpty()) {
				user.setPassword(password);
				user.setFirstName(firstName);
				user.setLastName(lastName);
				restaurant.saveData();
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Success");
				alert.setHeaderText("Success update profile!");
				alert.showAndWait();
				editProfileStage.close();
			} else {
				errorLabel.setText("Please fill in all fields!");
			}
		});
		editProfileStage.showAndWait();
	}

	private void showMessage(String title, String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(message);
		alert.showAndWait();
	}

	private void showRegisterWindow() {
		Stage registerStage = new Stage();
		registerStage.initModality(Modality.APPLICATION_MODAL);
		registerStage.initOwner(view.getPrimaryStage());
		registerStage.setTitle("Register");

		GridPane registerLayout = new GridPane();
		registerLayout.setPadding(new Insets(10, 10, 10, 10));
		registerLayout.setVgap(5);
		registerLayout.setHgap(5);

		Label usernameLabel = new Label("Username: ");
		TextField usernameField = new TextField();
		Label passwordLabel = new Label("Password: ");
		PasswordField passwordField = new PasswordField();
		Label firstNameLabel = new Label("Firstname");
		TextField firstNameField = new TextField();
		Label lastNameLabel = new Label("Lastname");
		TextField lastNameField = new TextField();

		HBox hbox = new HBox();
		hbox.setSpacing(20);
		Button registerButton = new Button("Register");
		Button cancelButton = new Button("Cancel");
		cancelButton.setOnAction(e -> {
			registerStage.close();
		});
		hbox.getChildren().addAll(registerButton, cancelButton);
		Label errorLabel = new Label();
		errorLabel.setTextFill(Color.RED);

		registerLayout.add(usernameLabel, 0, 0);
		registerLayout.add(usernameField, 1, 0);
		registerLayout.add(passwordLabel, 0, 1);
		registerLayout.add(passwordField, 1, 1);
		registerLayout.add(firstNameLabel, 0, 2);
		registerLayout.add(firstNameField, 1, 2);
		registerLayout.add(lastNameLabel, 0, 3);
		registerLayout.add(lastNameField, 1, 3);
		registerLayout.add(hbox, 1, 4);
		registerLayout.add(errorLabel, 1, 5);

		Scene registerScene = new Scene(registerLayout, 300, 200);
		registerStage.setScene(registerScene);

		registerButton.setOnAction(event -> {
			String username = usernameField.getText();
			String password = passwordField.getText();
			String firstName = firstNameField.getText();
			String lastName = lastNameField.getText();
			if (!username.isEmpty() && !password.isEmpty() && !firstName.isEmpty() && !lastName.isEmpty()) {
				if (restaurant.getUserByName(username) != null) {
					errorLabel.setText("Username exists!");
				} else {
					User user = new User(username, password, firstName, lastName);
					restaurant.addUser(user);
					restaurant.saveData();
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Success");
					alert.setHeaderText("Success");
					alert.setContentText("Success register a user!");
					alert.showAndWait();
					registerStage.close();
				}
			} else {
				errorLabel.setText("Please fill in all fields!");
			}
		});
		registerStage.showAndWait();
	}
}

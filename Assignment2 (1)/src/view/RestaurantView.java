package view;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Order;
import model.User;

/**
 * The main program, ask user login first,
 * then display active orders that has not been picked up by the user.  
 */
public class RestaurantView {
	private TableView<Order> activeTable = new TableView<Order>();
	private User user;
	private Stage primaryStage;
	private Label welcomeLabel;
	private Button loginButton;
	private Button registerButton;
	private Button cancelOrderButton;
	private Button collectOrderButton;
	private TextField usernameField;
	private PasswordField passwordField;
	private Label errorLabel;
	private Scene mainScene;
	private Scene loginScene;
	private MenuItem exitItem;
	private MenuItem shoppingItem;
	private MenuItem viewOrdersItem;
	private MenuItem exportOrdersItem;
	private MenuItem upgradeVipItem;
	private MenuItem editProfileItem;
	private Button confirmRegisterButton;

	public RestaurantView() {
		Stage primaryStage = new Stage();
		this.primaryStage = primaryStage;
		primaryStage.setTitle("Login");
		
		GridPane loginPane = new GridPane();
		loginPane.setPadding(new Insets(20, 10, 10, 20));
		loginPane.setVgap(5);
		loginPane.setHgap(5);
		FlowPane buttonPane = new FlowPane();
		buttonPane.setHgap(20);

		Label usernameLabel = new Label("Username: ");
		usernameField = new TextField();
		Label passwordLabel = new Label("Password: ");
		passwordField = new PasswordField();
		loginButton = new Button("Login");
		errorLabel = new Label();
		errorLabel.setTextFill(Color.RED);

		registerButton = new Button("Register");
		registerButton.setOnAction(event -> showRegisterWindow());

		buttonPane.getChildren().addAll(loginButton, registerButton);
		loginPane.add(usernameLabel, 0, 0);
		loginPane.add(usernameField, 1, 0);
		loginPane.add(passwordLabel, 0, 1);
		loginPane.add(passwordField, 1, 1);
		loginPane.add(buttonPane, 1, 2, 2, 1);
		loginPane.add(errorLabel, 1, 3);

		VBox contentBox = new VBox();

		contentBox.setPadding(new Insets(5, 5, 5, 5));
		contentBox.setSpacing(10);

		welcomeLabel = new Label();

		// create table for display active order
		TableColumn<Order, String> orderNoCol = new TableColumn<>("Order Number");
		orderNoCol.prefWidthProperty().bind(activeTable.widthProperty().multiply(0.17));
		orderNoCol.setCellValueFactory(new PropertyValueFactory<Order, String>("orderNo"));

		TableColumn<Order, String> itemsCol = new TableColumn<>("Ordered Food Items");
		itemsCol.prefWidthProperty().bind(activeTable.widthProperty().multiply(0.5));
		itemsCol.setCellValueFactory(new PropertyValueFactory<Order, String>("foodItems"));

		TableColumn<Order, String> priceCol = new TableColumn<>("Total Price");
		priceCol.prefWidthProperty().bind(activeTable.widthProperty().multiply(0.1));
		priceCol.setCellValueFactory(new PropertyValueFactory<Order, String>("totalAmount"));

		TableColumn<Order, String> statusCol = new TableColumn<>("Order Status");
		statusCol.prefWidthProperty().bind(activeTable.widthProperty().multiply(0.25));
		statusCol.setCellValueFactory(new PropertyValueFactory<Order, String>("Status"));
		activeTable.getColumns().addAll(orderNoCol, itemsCol, priceCol, statusCol);

		HBox buttonBar = new HBox();
		buttonBar.setSpacing(10);
		MenuBar menuBar = createMenuBar();
		cancelOrderButton = new Button("Cancel Order");
		collectOrderButton = new Button("Collect Order");
		buttonBar.getChildren().addAll(welcomeLabel, cancelOrderButton, collectOrderButton);
		contentBox.getChildren().addAll(menuBar, buttonBar, activeTable);
		cancelOrderButton.setTranslateX(100);
		collectOrderButton.setTranslateX(100);
		loginScene = new Scene(loginPane, 280, 190);
		mainScene = new Scene(contentBox, 700, 400);
		primaryStage.setScene(loginScene);
		primaryStage.show();
	}
	
	/**
	 * Check if a valid time HH:MM
	 */
	private boolean isValidTime(String time) {
		if(!time.matches("[0-9]{2}:[0-9]{2}")) {
			return false;
		}
		String values[] = time.split(":");
		int hour = Integer.parseInt(values[0]);
		int minute = Integer.parseInt(values[1]);
		return hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59;
	}
	
	private MenuBar createMenuBar() {
		MenuBar menuBar = new MenuBar();
		Menu fileMenu = new Menu("File");
		exitItem = new MenuItem("Log out");
		shoppingItem = new MenuItem("Shopping basket");
		viewOrdersItem = new MenuItem("View all orders");
		exportOrdersItem = new MenuItem("Export all orders");
		upgradeVipItem = new MenuItem("Upgrade to VIP");
		editProfileItem = new MenuItem("Edit profile");
		fileMenu.getItems().addAll(shoppingItem, viewOrdersItem, exportOrdersItem, editProfileItem, upgradeVipItem, exitItem);
		menuBar.getMenus().add(fileMenu);
		return menuBar;
	}

	private void showEditProfileWindow() {
		Stage editProfileStage = new Stage();
		editProfileStage.initModality(Modality.APPLICATION_MODAL);
		editProfileStage.initOwner(this.primaryStage);
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
		Button updateProfileButton = new Button("Update");
		Button cancelUpdateProfileButton = new Button("Cancel");
		cancelUpdateProfileButton.setOnAction(e -> {
			editProfileStage.close();
		});
		Label errorLabel = new Label();
		errorLabel.setTextFill(Color.RED);

		buttonBox.getChildren().addAll(updateProfileButton, cancelUpdateProfileButton);
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
		registerStage.initOwner(this.primaryStage);
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
		confirmRegisterButton = new Button("Register");
		Button cancelButton = new Button("Cancel");
		cancelButton.setOnAction(e -> {
			registerStage.close();
		});
		hbox.getChildren().addAll(confirmRegisterButton, cancelButton);
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

		registerStage.showAndWait();
	}

	public TableView<Order> getActiveTable() {
		return activeTable;
	}

	public void setActiveTable(TableView<Order> activeTable) {
		this.activeTable = activeTable;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public Label getWelcomeLabel() {
		return welcomeLabel;
	}

	public void setWelcomeLabel(Label welcomeLabel) {
		this.welcomeLabel = welcomeLabel;
	}

	public Button getLoginButton() {
		return loginButton;
	}

	public void setLoginButton(Button loginButton) {
		this.loginButton = loginButton;
	}

	public Button getRegisterButton() {
		return registerButton;
	}

	public void setRegisterButton(Button registerButton) {
		this.registerButton = registerButton;
	}

	public Button getCancelOrderButton() {
		return cancelOrderButton;
	}

	public void setCancelOrderButton(Button cancelOrderButton) {
		this.cancelOrderButton = cancelOrderButton;
	}

	public Button getCollectOrderButton() {
		return collectOrderButton;
	}

	public void setCollectOrderButton(Button collectOrderButton) {
		this.collectOrderButton = collectOrderButton;
	}

	public TextField getUsernameField() {
		return usernameField;
	}

	public void setUsernameField(TextField usernameField) {
		this.usernameField = usernameField;
	}

	public PasswordField getPasswordField() {
		return passwordField;
	}

	public void setPasswordField(PasswordField passwordField) {
		this.passwordField = passwordField;
	}

	public Label getErrorLabel() {
		return errorLabel;
	}

	public void setErrorLabel(Label errorLabel) {
		this.errorLabel = errorLabel;
	}

	public Scene getMainScene() {
		return mainScene;
	}

	public void setMainScene(Scene mainScene) {
		this.mainScene = mainScene;
	}

	public Scene getLoginScene() {
		return loginScene;
	}

	public void setLoginScene(Scene loginScene) {
		this.loginScene = loginScene;
	}

	public MenuItem getExitItem() {
		return exitItem;
	}

	public void setExitItem(MenuItem exitItem) {
		this.exitItem = exitItem;
	}

	public MenuItem getShoppingItem() {
		return shoppingItem;
	}

	public void setShoppingItem(MenuItem shoppingItem) {
		this.shoppingItem = shoppingItem;
	}

	public MenuItem getViewOrdersItem() {
		return viewOrdersItem;
	}

	public void setViewOrdersItem(MenuItem viewOrdersItem) {
		this.viewOrdersItem = viewOrdersItem;
	}

	public MenuItem getExportOrdersItem() {
		return exportOrdersItem;
	}

	public void setExportOrdersItem(MenuItem exportOrdersItem) {
		this.exportOrdersItem = exportOrdersItem;
	}

	public MenuItem getUpgradeVipItem() {
		return upgradeVipItem;
	}

	public void setUpgradeVipItem(MenuItem upgradeVipItem) {
		this.upgradeVipItem = upgradeVipItem;
	}

	public MenuItem getEditProfileItem() {
		return editProfileItem;
	}

	public void setEditProfileItem(MenuItem editProfileItem) {
		this.editProfileItem = editProfileItem;
	}

	public Button getConfirmRegisterButton() {
		return confirmRegisterButton;
	}

	public void setConfirmRegisterButton(Button confirmRegisterButton) {
		this.confirmRegisterButton = confirmRegisterButton;
	}
}


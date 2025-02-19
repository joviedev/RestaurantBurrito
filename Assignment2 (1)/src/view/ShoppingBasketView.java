package view;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Food;
import model.Order;
import model.OrderItem;
import model.Restaurant;

/**
 * GUI for add or remove food items to shopping basket.
 */
public class ShoppingBasketView {
	private Restaurant restaurant;
    private ObservableList<OrderItem> items;
    private Stage primaryStage;
    private boolean isCreditCardInputed = false;
    private Stage parent;
	private ComboBox<Food> foodComboBox;
	private Button checkoutButton;
	private Button addButton;
	private Button deleteButton;
	private Button submitButton;
	private TableView<OrderItem> orderTableView;
	private ImageView foodImageView;
	private Spinner<Integer> foodQtySpinner;
    
    public ShoppingBasketView(Restaurant rest, Stage parent) {
    	Order order = rest.getShoppingBasket();
    	this.restaurant = rest;
    	this.parent = parent;
    	primaryStage = new Stage();
    	primaryStage.initModality(Modality.APPLICATION_MODAL);
    	primaryStage.initOwner(parent);
        primaryStage.setTitle("Shopping Basket");

        orderTableView = new TableView<>();
        items = FXCollections.observableArrayList(order.getOrderItems());
        orderTableView.setItems(items);

        TableColumn<OrderItem, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        orderTableView.getColumns().add(nameColumn);

        TableColumn<OrderItem, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        orderTableView.getColumns().add(priceColumn);

        TableColumn<OrderItem, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityColumn.setCellFactory(tc -> new SpinnerTableCell<>(new Spinner<>(0, 100, 1)));
        nameColumn.setCellFactory(TextFieldTableCell.<OrderItem>forTableColumn());
        orderTableView.getColumns().add(quantityColumn);

        HBox itemQtyBox = new HBox();
        itemQtyBox.setSpacing(10);
        Label labelItem = new Label("Item: ");
        Label labelQuantity = new Label("Quantity: ");
        foodQtySpinner = new Spinner(1, 50, 1);
        foodQtySpinner.setPrefWidth(100);
        foodImageView = new ImageView();
        foodImageView.setFitWidth(150);
        foodImageView.setFitHeight(100);
        foodComboBox = new ComboBox<>();
        
        HBox buttonBox = new HBox();
        buttonBox.setSpacing(10);
        addButton = new Button("Add");
        deleteButton = new Button("Delete");
        checkoutButton = new Button("Check out");
        
        itemQtyBox.getChildren().addAll(labelItem, foodComboBox, labelQuantity, foodQtySpinner, foodImageView);
        buttonBox.getChildren().addAll(addButton, deleteButton, checkoutButton);
        
        VBox vbox = new VBox(10, orderTableView, itemQtyBox, buttonBox);
        vbox.setPadding(new Insets(10));

        primaryStage.setScene(new Scene(vbox, 500, 350));
    }

    /**
     * Define table's cell displayed as spinner
     */
    class SpinnerTableCell<S> extends javafx.scene.control.TableCell<S, Integer> {
        private final Spinner<Integer> spinner;

        public SpinnerTableCell(Spinner<Integer> spinner) {
            this.spinner = spinner;
            this.spinner.setEditable(true);
            this.spinner.setDisable(!isEditable());

            this.spinner.valueProperty().addListener((obs, oldValue, newValue) -> {
                if (!isEditing()) {
                    commitEdit(newValue);
                    if(getTableRow() != null) {
                    	OrderItem order = (OrderItem)getTableRow().getItem();
                    	if(order != null) {
                    		order.setQuantity(newValue);
                        	spinner.getValueFactory().setValue(newValue);                    		
                    	}
                    }
                }
            });
        }

		@Override
		protected void updateItem(Integer item, boolean empty) {
			super.updateItem(item, empty);
			if (empty || item == null) {
				setGraphic(null);
			} else {
				setGraphic(spinner);
				spinner.getValueFactory().setValue(item);
			}
		}
    }
    
	public ComboBox<Food> getFoodComboBox() {
		return foodComboBox;
	}

	public void setFoodComboBox(ComboBox<Food> foodComboBox) {
		this.foodComboBox = foodComboBox;
	}

	public Button getCheckoutButton() {
		return checkoutButton;
	}

	public void setCheckoutButton(Button checkoutButton) {
		this.checkoutButton = checkoutButton;
	}

	public Button getAddButton() {
		return addButton;
	}

	public void setAddButton(Button addButton) {
		this.addButton = addButton;
	}

	public Button getDeleteButton() {
		return deleteButton;
	}

	public void setDeleteButton(Button deleteButton) {
		this.deleteButton = deleteButton;
	}

	public Button getSubmitButton() {
		return submitButton;
	}

	public void setSubmitButton(Button submitButton) {
		this.submitButton = submitButton;
	}

	public TableView<OrderItem> getOrderTableView() {
		return orderTableView;
	}

	public void setOrderTableView(TableView<OrderItem> orderTableView) {
		this.orderTableView = orderTableView;
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public ImageView getFoodImageView() {
		return foodImageView;
	}

	public void setFoodImageView(ImageView foodImageView) {
		this.foodImageView = foodImageView;
	}

	public Spinner<Integer> getFoodQtySpinner() {
		return foodQtySpinner;
	}

	public void setFoodQtySpinner(Spinner<Integer> foodQtySpinner) {
		this.foodQtySpinner = foodQtySpinner;
	}
}


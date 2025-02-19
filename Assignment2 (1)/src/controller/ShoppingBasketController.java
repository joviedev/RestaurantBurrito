package controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.CreditCard;
import model.Food;
import model.Order;
import model.OrderItem;
import model.Restaurant;
import view.ShoppingBasketView;

/**
 * Controller for bind view and model.
 */
public class ShoppingBasketController {
	private Restaurant restaurant;
    private boolean isCreditCardInputed = false;
    private Stage parent;
    private ShoppingBasketView shoppingBasketView;
    
    public ShoppingBasketController(Restaurant rest, Stage parent, ShoppingBasketView shoppingBasketView) {
    	this.restaurant = rest;
    	this.shoppingBasketView = shoppingBasketView;
    	this.parent = parent;
        
        // add food item combobox
        List<Food> foodItems = new ArrayList<Food>();
        for(Food f : rest.getFoodItems()) {
        	if(!(f.isVirutal() && !restaurant.getCurLoginUser().isVip())) {
        		foodItems.add(f);
        	}
        }
        shoppingBasketView.getFoodComboBox().setItems(FXCollections.observableArrayList(foodItems));
        shoppingBasketView.getFoodComboBox().valueProperty().addListener(e->{
        	String imageName = shoppingBasketView.getFoodComboBox().getSelectionModel().getSelectedItem().getName().toLowerCase() + ".png";
        	shoppingBasketView.getFoodImageView().setImage(new Image(this.getClass().getResource("../images/" + imageName).toString()));
        });
        shoppingBasketView.getFoodComboBox().getSelectionModel().selectFirst();
        shoppingBasketView.getAddButton().setOnAction(e -> {
            Food food = shoppingBasketView.getFoodComboBox().getSelectionModel().getSelectedItem();
            OrderItem item = restaurant.getShoppingBasket().getOrderItem(food.getName());
            if(item != null) {
            	item.setQuantity(item.getQuantity() + shoppingBasketView.getFoodQtySpinner().getValue());
            } else {
            	OrderItem newItem = new OrderItem(food, food.getPrice(), shoppingBasketView.getFoodQtySpinner().getValue());
            	restaurant.getShoppingBasket().addOrderItem(newItem);
            	shoppingBasketView.getOrderTableView().getItems().add(newItem);
            }
            shoppingBasketView.getOrderTableView().refresh();
        });

        shoppingBasketView.getDeleteButton().setOnAction(e -> {
        	OrderItem selectedItem = shoppingBasketView.getOrderTableView().getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
            	shoppingBasketView.getOrderTableView().getItems().remove(selectedItem);
                restaurant.getShoppingBasket().getOrderItems().remove(selectedItem);
            } else {
            	showMessage("Warning", "Please select item in the table to delete!");
            }
        });

        shoppingBasketView.getCheckoutButton().setOnAction(e -> {
        	if(shoppingBasketView.getOrderTableView().getItems().size() == 0) {
        		showMessage("Error", "Please first add food to shopping basket!");
        		return;
        	}
        	int waitMinutes = restaurant.getShoppingBasket().checkOut();
        	double totalPrice = restaurant.getShoppingBasket().getTotalAmount();
        	// display the total price and the waiting time before placing the order
        	showConfirm(String.format("Total price: %.2f, wait minutes: %d, are you confirm to place order?", totalPrice, waitMinutes));
        });
        shoppingBasketView.getPrimaryStage().showAndWait();
    }

    /**
     * Display confirm message.
     */
    private void showConfirm(String message) {
    	Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText(message);

        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
            	// collecting credits for all orders. The user can earn one credit for each dollar spent.
            	if(inputCreditCard()) {
            		Order order = restaurant.placeOrder();
            		if(restaurant.getCurLoginUser().isVip()) {
            			restaurant.getCurLoginUser().addCredit(order.getTotalAmount());
            		}
                	showMessage("Success", "Order successfully placed!");
                	shoppingBasketView.getPrimaryStage().close();            		
            	}
            }
        });
    }
    
    /**
     * Display message dialog
     */
    private void showMessage(String title, String message) {
    	Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(message);
		alert.showAndWait();
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
    
    /**
	 * Collect (fake) credit card information (card number, expiry date, and cvv).
	 * @return true if entered data is correct, else return false
	 */
	public boolean inputCreditCard() {
		Stage creditCardStage = new Stage();
		creditCardStage.initModality(Modality.APPLICATION_MODAL);
		creditCardStage.initOwner(parent);
		creditCardStage.setTitle("Collect credit card information");
		GridPane loginPane = new GridPane();
		loginPane.setPadding(new Insets(20, 10, 10, 20));
		loginPane.setVgap(5);
		loginPane.setHgap(5);
		FlowPane buttonPane = new FlowPane();
		buttonPane.setHgap(20);

		Label cardNoLabel = new Label("Card number: ");
		TextField cardNoField = new TextField();
		
		Label expLabel = new Label("Expiry date(yyyy-MM-dd): ");
		TextField expField = new TextField();
		
		Label cvvLabel = new Label("cvv: ");
		TextField cvvField = new TextField();
		
		Button cancelButton = new Button("Cancel");
		Label errorLabel = new Label();
		errorLabel.setTextFill(Color.RED);

		Button submitButton = new Button("Submit");
		// submit the credit card information after enter correct data
		submitButton.setOnAction(e -> {
			if(!cardNoField.getText().trim().matches("[0-9]{16}")) {
				errorLabel.setText("The card number must be 16 digits");
				return;
			}
			Date expDate = null;
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			try {
				expDate = dateFormat.parse(expField.getText().trim());
			} catch (Exception e2) {
			}
			if(expDate == null || !expDate.after(Calendar.getInstance().getTime())) {
				errorLabel.setText("The expiry date must be a future date, for example 2030-05-01");
				return;
			}
			if(!cardNoField.getText().trim().matches("[0-9]{16}")) {
				errorLabel.setText("The card number must be 16 digits");
				return;
			}
			if(!cvvField.getText().trim().matches("[0-9]{3}")) {
				errorLabel.setText("The cvv must be 3 digits");
				return;
			}
			isCreditCardInputed = true;
			CreditCard creditCard = new CreditCard();
			creditCard.setCvv(cvvField.getText().trim());
			creditCard.setCardNumber(cardNoField.getText().trim());
			creditCard.setExpiryDate(expDate);
			restaurant.getShoppingBasket().setPaymentCreditCard(creditCard);
			creditCardStage.close();
		});
		cancelButton.setOnAction(e->{
			isCreditCardInputed = false;
			creditCardStage.close();
		});
		buttonPane.getChildren().addAll(submitButton, cancelButton);
		loginPane.add(cardNoLabel, 0, 0);
		loginPane.add(cardNoField, 1, 0, 2, 1);
		loginPane.add(expLabel, 0, 1);
		loginPane.add(expField, 1, 1, 2, 1);
		loginPane.add(cvvLabel, 0, 2);
		loginPane.add(cvvField, 1, 2, 2, 1);
		loginPane.add(buttonPane, 1, 3, 2, 1);
		loginPane.add(errorLabel, 0, 4, 4, 1);
		Scene registerScene = new Scene(loginPane, 500, 200);
		creditCardStage.setScene(registerScene);
		creditCardStage.showAndWait();
		return isCreditCardInputed;
	}
}

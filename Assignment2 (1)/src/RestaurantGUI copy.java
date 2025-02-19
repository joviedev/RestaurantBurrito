import controller.RestaurantController;
import javafx.application.Application;
import javafx.stage.Stage;
import view.RestaurantView;

/**
 * The main program to start restaurant GUI.
 */
public class RestaurantGUI extends Application{
	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage primaryStage) throws Exception {
		RestaurantView restaurantView = new RestaurantView();
		new RestaurantController(restaurantView);
	}
}


# Restaurant Burrito Booking System
IDE used: Eclipse#Java version: Java SE 17
JavaFX version: JavaFx 17
Directions: RestaurantGUI.java is the main program, put all code into eclipse using Java SE 17 or later, the run the program.
Unit test program: test/UnitTest.java
OO design: 
(1) Use Singleton pattern in model/Restaurant.java to create instance of Restaurant by this method public static Restaurant getInstance().
all data are saved in class Restaurant, gui class like ExportOrdersView and ShoppingBasketView are used as view of these data.
(2) Food is defined as an abstract class, Meal, Fries, Soda, Burrito are defined as subclasses of Food. 

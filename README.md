🍽️ Burrito King Restaurant Management System
A Java-based restaurant ordering system designed for Burrito King, allowing customers to place orders, manage their profiles, and access VIP benefits. Developed using JavaFX, the system integrates object-oriented design principles, MVC architecture, and JDBC for persistent data storage.

✨ Features:
🔹 User Management
User registration with unique usernames and passwords
Login system with profile editing options
VIP membership upgrade with email-based promotions
🔹 Ordering System
Add, update, and remove items (burritos, fries, soda) from the shopping basket
Checkout system with validation for fake credit card transactions
Order tracking: Displays order status (awaiting collection, collected, or canceled)
Order history: View and manage past orders
Dynamic waiting time calculation before order collection
🔹 Advanced Functionalities for VIP Users
Discounted meal bundles (burrito, fries, and soda) with $3 off
Credit-based reward system: 1 credit per dollar spent, redeemable at checkout
Export orders to CSV with customizable fields
⏳ Waiting Time Logic
Each order has a preparation time based on the number of items ordered.
The estimated waiting time is displayed before placing an order.
Users cannot collect an order before the calculated minimum collection time, ensuring food is prepared before pickup.
The waiting time is dynamically updated based on the restaurant's processing speed.
🏗️ Tech Stack & Design Principles
Java SE 17+ (Core logic & GUI with JavaFX)
MVC Architecture for clean separation of concerns
JDBC for data persistence
JUnit for unit testing
Singleton & Interface-based design to enhance maintainability

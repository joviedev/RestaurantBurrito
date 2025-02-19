package model;
public class Burrito extends Food {
	public Burrito(double price, int timeTaken, int numInBatch, boolean cookInBatch) {
		super("Burrito", price, timeTaken, numInBatch, cookInBatch, false);
	}
}


package cg.exceptions;

@SuppressWarnings("serial")
public class InvalidImagePathExeption extends RuntimeException {

	public InvalidImagePathExeption() {
		super("An image path is invalid. Check for existance and permissions.");
	}
}

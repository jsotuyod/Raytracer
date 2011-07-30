package cg.exceptions;

@SuppressWarnings("serial")
public class UnsupportedInterpolationType extends RuntimeException {
	public UnsupportedInterpolationType() {
		super("The given interpolation type is unsuported.");
	}
}

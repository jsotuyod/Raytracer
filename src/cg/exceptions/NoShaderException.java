package cg.exceptions;

@SuppressWarnings("serial")
public class NoShaderException extends RuntimeException {

	public NoShaderException() {
		super("A scene object has a nonexisting-shader assigned.");
	}
}

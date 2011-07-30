package cg.exceptions;

@SuppressWarnings("serial")
public class NoShaderTextureException extends RuntimeException {

	public NoShaderTextureException() {
		super("A scene object has a nonexisting-shader texture assigned.");
	}
}

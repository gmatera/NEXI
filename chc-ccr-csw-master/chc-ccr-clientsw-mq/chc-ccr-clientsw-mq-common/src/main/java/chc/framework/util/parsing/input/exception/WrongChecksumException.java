package chc.framework.util.parsing.input.exception;


public class WrongChecksumException extends Exception{
	private static final long serialVersionUID = 8843944201831548592L;

	public WrongChecksumException(String message) {
		super(message);
	}
	
}

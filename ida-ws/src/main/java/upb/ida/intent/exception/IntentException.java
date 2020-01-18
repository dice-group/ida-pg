package upb.ida.intent.exception;

public class IntentException extends Exception {

	public IntentException(Throwable wrapped) {
		super(wrapped);
	}

	public IntentException(String message) {
		super(message);
	}
}

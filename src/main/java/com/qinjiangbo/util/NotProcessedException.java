package com.qinjiangbo.util;

public class NotProcessedException extends RuntimeException {

	/**
	 * serial version UID
	 */
	private static final long serialVersionUID = 1L;

	public NotProcessedException() {
		super();
	}

	public NotProcessedException(String message) {
		super(message);
	}

	public NotProcessedException(String message, Throwable causes) {
		super(message, causes);
	}

	public NotProcessedException(Throwable causes) {
		super(causes);
	}

}

package com.qinjiangbo.util;

public class NotPreProcessedException extends RuntimeException{

	/**
	 * serial version UID
	 */
	private static final long serialVersionUID = 1L;
	
	public NotPreProcessedException() {
		super();
	}
	
	public NotPreProcessedException(String message) {
		super(message);
	}
	
	public NotPreProcessedException(String message, Throwable causes) {
		super(message, causes);
	}
	
	public NotPreProcessedException(Throwable causes) {
		super(causes);
	}

}

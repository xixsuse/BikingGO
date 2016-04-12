package com.kingwaytek.api.exception;

public class NotInstalledException extends Exception {

	private static final long serialVersionUID = 3L;
	
	public NotInstalledException(String message){
		super(message);
	}
}

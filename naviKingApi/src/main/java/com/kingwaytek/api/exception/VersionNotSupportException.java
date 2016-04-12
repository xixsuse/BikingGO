package com.kingwaytek.api.exception;

public class VersionNotSupportException extends Exception {

	private static final long serialVersionUID = 2L;
	
	public VersionNotSupportException(String message){
		super(message);
	}
}

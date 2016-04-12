package com.kingwaytek.api.exception;

public class ImeiEmptyException extends Exception{

	private static final long serialVersionUID = 8999L;

	public ImeiEmptyException(String msg){
		super(msg);
	}
}
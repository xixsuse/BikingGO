package com.kingwaytek.api.exception;

public class ApiException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public static final String URL_CANT_BE_NULL_OR_EMPTY = "Scheme url can't be null or empty." ;
	public static final String URL_DONESNT_CONTAIN_NAVIGATION_OR_FUNCTIONNAME_FORMAT = "Scheme url doesn't contain navigation or function name." ;
	public static final String URL_DONESNT_CONTAIN_POIINFO_OR_FUNCTIONNAME_FORMAT = "Scheme url doesn't contain poiinfo or function name." ;
	public static final String URL_DONESNT_CONTAIN_LAT_ARGUMENT = "Scheme url doesn't contain lat argument." ;
	public static final String URL_DONESNT_CONTAIN_POIID_ARGUMENT = "Scheme url doesn't contain poiid argument." ;
	public static final String URL_DONESNT_CONTAIN_CPID_ARGUMENT = "Scheme url doesn't contain cpid argument." ;
	public static final String URL_DONESNT_CONTAIN_LON_ARGUMENT = "Scheme url doesn't contain lon argument." ;
	public static final String URL_DONESNT_CONTAIN_SIGN = "Scheme url doesn't contain ? or & sign" ;
	public static final String URL_LAT_LON_FORMAT_CANT_BE_PARSED = "Scheme url lat or lon cant be parsed double." ;	
	public static final String CREATE_SCHEME_LAT_OR_LON_CAN_BE_ZERO = "Lat or lon cant be zero while create scheme." ;
	public static final String ARGUMENT_NOT_EXIST = "Argument not exist." ;
	public static final String CALLER_DATA_CANT_BE_NULL = "CallerData cant be null.";
	public static final String LAT_CANT_LARGET_THAN_LON = "Lat can't be large then lon.";
	public static final String ADDRESS_CAN_NOT_NULL_OR_EMPTY = "Address can not null or empty.";
	public static final String CREATE_SCHEME_POIID_CAN_BE_NULL = "PoiId cant be null while create scheme." ;
	public static final String CREATE_SCHEME_CPID_CAN_BE_NULL = "CpId cant be null while create scheme." ;
	public ApiException(String message){
		super(message);
	}
}
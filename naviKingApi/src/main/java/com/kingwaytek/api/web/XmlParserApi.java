package com.kingwaytek.api.web;

public class XmlParserApi {
	
	public static String getDataFromXml(final String XML_DATA ,final String XML_TAG){		
		String jsonData = null;
		if(XML_DATA != null && XML_TAG.length() > 0 && XML_TAG!= null && XML_TAG.length() >0){
			final String TAG_NAME_START = "<" + XML_TAG + ">" ;  
			final String TAG_NAME_END = "</" + XML_TAG + ">" ;
			final int START_INDEX = XML_DATA.indexOf(TAG_NAME_START) ;
			final int END_INDEX = XML_DATA.indexOf(TAG_NAME_END) ;
			final int START_LEN = TAG_NAME_START.length();			
			try{
				jsonData = XML_DATA.substring(START_INDEX+START_LEN, END_INDEX);
			}catch(IndexOutOfBoundsException e){
				e.printStackTrace();
			}
		}		
		return jsonData;
	}
	
	public static String removeXmlFromJsonString(String src){
		String result = src ;
		if(src != null){
			int startIdx = src.indexOf("<?xml");			
			if(startIdx > 3){
				result = (String) src.subSequence(0, startIdx);
			}
		}
		return result;
	}
}
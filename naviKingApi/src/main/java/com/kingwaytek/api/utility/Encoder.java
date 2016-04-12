package com.kingwaytek.api.utility;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import android.content.Context;
import com.kingwaytek.api.exception.ImeiEmptyException;

public class Encoder {
	
	public static String toMd5String(String encode){		
        return getMd5Encode(encode);    
	}
	
	public static String getStringEncodeByImei(Context context,String presetKey) throws ImeiEmptyException{
		String imei = UtilityApi.getHardwareId(context) ;
		String result = imei + presetKey ;		
		if(imei != null && imei.length() == 0){
			throw new ImeiEmptyException("");
		}		
		String encodeResult = getMd5Encode(result);
		return encodeResult;
	}
	
	public static final String getMd5Encode(String source){
		MessageDigest mdEnc = null;
		
		try {
            mdEnc = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        mdEnc.update(source.getBytes(), 0, source.length());
        String md5 = new BigInteger(1, mdEnc.digest()).toString(16) ;        
        return md5;
	}
}
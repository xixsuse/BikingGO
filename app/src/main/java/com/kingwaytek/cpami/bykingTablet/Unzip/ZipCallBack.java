package com.kingwaytek.cpami.bykingTablet.Unzip;

public interface ZipCallBack {
	public void onZipCB(long finishSize,long maxSize,int fileSize,int totalFileSize);
	public void onZipSuccess(int success);
	public void onZipFail();
}

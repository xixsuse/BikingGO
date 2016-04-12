package com.kingwaytek.cpami.bykingTablet.download;

public interface DownloadProgressListener {  
    public void onDownloadSize(String apkName,int size);

	public void onDownloadFinish(String substring);  
	public void onDownloadFail(String substring);  
}  
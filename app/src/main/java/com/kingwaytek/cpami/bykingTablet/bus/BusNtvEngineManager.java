package com.kingwaytek.cpami.bykingTablet.bus;

import com.kingwaytek.jni.BusNtvEngine;

public class BusNtvEngineManager {

	private static String BUS_ENGINE_PATH = "/sdcard/NaviKingData/BUS.BIN";
	private static int hBus = BusNtvEngine.NewBusEngine(BUS_ENGINE_PATH);
	
	public static void setBus(int hBus) {
		BusNtvEngineManager.hBus = hBus;
	}
	
	public static void setBusEnginePath(String _path){
		BUS_ENGINE_PATH = _path ; 
	}

	public static int getBus() {
		BusNtvEngine.DestroyBusEngine(hBus);
		hBus = BusNtvEngine.NewBusEngine(BUS_ENGINE_PATH);
		return hBus;
	}	
	
}

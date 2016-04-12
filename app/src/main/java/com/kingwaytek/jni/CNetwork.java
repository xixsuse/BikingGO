package com.kingwaytek.jni;

public class CNetwork {	
	public int Index;
	public int[] History;
	public int HCount;
	
	public CNetwork(int _Index, int[] _History, int _HCount)
	{
		Index = _Index;
		History = _History;
		HCount = _HCount;				
	}
	public CNetwork()
	{
		Index = 0;
		HCount = 0;
		History = new int[16];
	}
}


package com.sonavtek.sonav;

import android.os.Message;

public interface OnSonavEventListener {
	
	public abstract void handleSonavEvent(Message msg);
}

package com.diycircuits.gpsfake;

import android.content.Context;
import android.content.SharedPreferences;
import de.robv.android.xposed.XSharedPreferences;

public class Settings {

	private static double lat = 22.2855200; // 22.318344;
	private static double lng = 114.1576900; // 114.168655;
	private static boolean start = false;
	private Context context = null;
	private XSharedPreferences xSharedPreferences = null;
    private SharedPreferences sharedPreferences = null;

	public Settings() {
		xSharedPreferences = new XSharedPreferences("com.diycircuits.gpsfake", "gps");
		// xSharedPreferences.makeWorldReadable();
	}

	public Settings(Context context) {
        sharedPreferences = context.getSharedPreferences("gps", Context.MODE_WORLD_READABLE);
        this.context = context;
    }

	public double getLat() {
		if (sharedPreferences != null)
			return sharedPreferences.getFloat("latitude", (float) 22.2855200);
		else if (xSharedPreferences != null)
			return xSharedPreferences.getFloat("latitude", (float) 22.2855200);
		return lat;
    }

	public double getLng() {
		if (sharedPreferences != null)
			return sharedPreferences.getFloat("longitude", (float) 114.1576900);
		else if (xSharedPreferences != null)
			return xSharedPreferences.getFloat("longitude", (float) 114.1576900);
		return lng;
    }

	public boolean isStarted() {
		if (sharedPreferences != null)
			return sharedPreferences.getBoolean("start", false);
		else if (xSharedPreferences != null)
			return xSharedPreferences.getBoolean("start", false);
		return start;
    }

	public void update(double la, double ln, boolean start) {
		SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        prefEditor.putFloat("latitude",  (float) la);
        prefEditor.putFloat("longitude", (float) ln);
        prefEditor.putBoolean("start",   start);
        prefEditor.apply();		
	}

	public void reload() {
        xSharedPreferences.reload();
    }
}

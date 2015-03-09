package com.spark.app.ocb.util;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.UUID;

public class SysUtils {

    public static Context context;
	
	/**
	 * Get AlarmManager
	 * @return AlarmManager
	 */
	public static AlarmManager getAlaramManager(){
		return (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	}

	/**
	 * Get ConnectivityManager 
	 * @return ConnectivityManager
	 */
	public static ConnectivityManager getConnectivityManager(){
		return (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}
	
	/**
	 * Get LocationManager
	 * @return LocationManager
	 */
	public static LocationManager getLocationManager(){
		return (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
	}
	
	/**
	 * Get PowerManager
	 * @return PowerManager
	 */
	public static PowerManager getPowerManager(){
		return (PowerManager)context.getSystemService(Context.POWER_SERVICE);
	}
	
	/**
	 * Get TelephonyManager
	 * @return TelephonyManager
	 */
	public static TelephonyManager getTelephonyManager(){
		return (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
	}
	
	/**
	 * Get android id
	 * @return String
	 */
	public static String getAndroidId(){
		return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
	}
	
	/**
	 * Get an unique ID for the device
	 * @return String
	 */
	public static String getUniqueId(){
		String deviceId = getTelephonyManager().getDeviceId();
		//rTODO.get deviceId for devices it is not available
		if (deviceId==null || "".equals(deviceId)){
			deviceId = UUID.randomUUID().toString();
		}
		
		return deviceId;
	}
	
	/**
	 * @return
	 */
	public static boolean isWifiConnected(){
		ConnectivityManager conn = getConnectivityManager();
		NetworkInfo mWifi = conn.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return mWifi.isConnected();
	}
	
	/**
	 * @return
	 */
	public static boolean isWifiEnabled(){
		ConnectivityManager conn = getConnectivityManager();
		NetworkInfo mWifi = conn.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return mWifi.isAvailable();
	}
	
	/**
	 * @return
	 */
	public static boolean setWifiEnabled(boolean enabled){
		ConnectivityManager conn = getConnectivityManager();
		NetworkInfo mWifi = conn.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (mWifi.isAvailable()){
			WifiManager mWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
			return mWifiManager.setWifiEnabled(enabled);
		}
		
		return true;
	}
	
	/**
	 * @return
	 */
	public static boolean is3GConnected(){
		ConnectivityManager conn = getConnectivityManager();
		NetworkInfo m3G = conn.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		return m3G.isConnected();
	}
	
	/**
	 * @return
	 */
	public static boolean is3Enabled(){
		ConnectivityManager conn = getConnectivityManager();
		NetworkInfo m3G = conn.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		return m3G.isAvailable();
	}
	
	public static boolean set3GEnabled(boolean enabled){
		ConnectivityManager conn = getConnectivityManager();
		NetworkInfo m3G = conn.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (m3G.isAvailable()) return true;
			
		try {
			if (Build.VERSION_CODES.FROYO == Build.VERSION.SDK_INT){
				Method dataConnSwitchmethod;
				Class<?> telephonyManagerClass;
				Object ITelephonyStub;
				Class<?> ITelephonyClass;

				TelephonyManager telephonyManager = getTelephonyManager();
				telephonyManagerClass = Class.forName(telephonyManager.getClass().getName());
				Method getITelephonyMethod = telephonyManagerClass.getDeclaredMethod("getITelephony");
				getITelephonyMethod.setAccessible(true);
				ITelephonyStub = getITelephonyMethod.invoke(telephonyManager);
				ITelephonyClass = Class.forName(ITelephonyStub.getClass().getName());

				if (enabled) {
					dataConnSwitchmethod = ITelephonyClass.getDeclaredMethod("enableDataConnectivity");
				} else {
					dataConnSwitchmethod = ITelephonyClass.getDeclaredMethod("disableDataConnectivity");
				}

				dataConnSwitchmethod.setAccessible(true);
				dataConnSwitchmethod.invoke(ITelephonyStub);
			} else {
				ConnectivityManager cm = getConnectivityManager();
				final Class<?> conmanClass = Class.forName(cm.getClass().getName());
				final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
				iConnectivityManagerField.setAccessible(true);
				
				final Object iConnectivityManager = iConnectivityManagerField.get(cm);
				final Class<?> iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
				final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
				setMobileDataEnabledMethod.setAccessible(true);
				setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
			}
			
			return true;
	    }catch(Exception e){
	         e.printStackTrace();
	         return false;
	    }
	}
	
	
	/**
	 * @return
	 */
	public static boolean networkConnected(){
		return isWifiEnabled() || is3GConnected();
	}
	
	public static void toast(String text){
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}
	
	public static void alert(String title, String message){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		
		builder.setTitle(title)
			   .setMessage(message)
			   .setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
	}

    /*
    *
    */
    public static int randomNumber(int aStart, int aEnd){
        if ( aStart > aEnd ) {
            throw new IllegalArgumentException("Start cannot exceed End.");
        }
        //get the range, casting to long to avoid overflow problems
        long range = (long)aEnd - (long)aStart + 1;

        Random aRandom = new Random();
        // compute a fraction of the range, 0 <= frac < range
        long fraction = (long)(range * aRandom.nextDouble());
        int randomNumber =  (int)(fraction + aStart);

        return randomNumber;
    }
}

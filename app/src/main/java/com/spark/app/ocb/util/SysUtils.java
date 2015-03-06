package com.spark.app.ocb.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

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

public class SysUtils {
	
	/**
	 * Get AlarmManager
	 * @param context
	 * @return AlarmManager
	 */
	public static AlarmManager getAlaramManager(Context context){
		return (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	}

	/**
	 * Get ConnectivityManager 
	 * @param context
	 * @return ConnectivityManager
	 */
	public static ConnectivityManager getConnectivityManager(Context context){
		return (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}
	
	/**
	 * Get LocationManager
	 * @param context
	 * @return LocationManager
	 */
	public static LocationManager getLocationManager(Context context){
		return (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
	}
	
	/**
	 * Get PowerManager
	 * @param context
	 * @return PowerManager
	 */
	public static PowerManager getPowerManager(Context context){
		return (PowerManager)context.getSystemService(Context.POWER_SERVICE);
	}
	
	/**
	 * Get TelephonyManager
	 * @param context
	 * @return TelephonyManager
	 */
	public static TelephonyManager getTelephonyManager(Context context){
		return (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
	}
	
	/**
	 * Get android id
	 * @param context
	 * @return String
	 */
	public static String getAndroidId(Context context){
		return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
	}
	
	/**
	 * Get an unique ID for the device
	 * @param context
	 * @return String
	 */
	public static String getUniqueId(Context context){
		String deviceId = getTelephonyManager(context).getDeviceId();
		//TODO.get deviceId for devices it is not available
		if (deviceId==null || "".equals(deviceId)){
			deviceId = UUID.randomUUID().toString();
		}
		
		return deviceId;
	}
	
	/**
	 * @param context
	 * @return
	 */
	public static boolean isWifiConnected(Context context){
		ConnectivityManager conn = getConnectivityManager(context);
		NetworkInfo mWifi = conn.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return mWifi.isConnected();
	}
	
	/**
	 * @param context
	 * @return
	 */
	public static boolean isWifiEnabled(Context context){
		ConnectivityManager conn = getConnectivityManager(context);
		NetworkInfo mWifi = conn.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return mWifi.isAvailable();
	}
	
	/**
	 * @param context
	 * @return
	 */
	public static boolean setWifiEnabled(Context context, boolean enabled){
		ConnectivityManager conn = getConnectivityManager(context);
		NetworkInfo mWifi = conn.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (mWifi.isAvailable()){
			WifiManager mWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
			return mWifiManager.setWifiEnabled(enabled);
		}
		
		return true;
	}
	
	/**
	 * @param context
	 * @return
	 */
	public static boolean is3GConnected(Context context){
		ConnectivityManager conn = getConnectivityManager(context);
		NetworkInfo m3G = conn.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		return m3G.isConnected();
	}
	
	/**
	 * @param context
	 * @return
	 */
	public static boolean is3Enabled(Context context){
		ConnectivityManager conn = getConnectivityManager(context);
		NetworkInfo m3G = conn.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		return m3G.isAvailable();
	}
	
	public static boolean set3GEnabled(Context context, boolean enabled){
		ConnectivityManager conn = getConnectivityManager(context);
		NetworkInfo m3G = conn.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (m3G.isAvailable()) return true;
			
		try {
			if (Build.VERSION_CODES.FROYO == Build.VERSION.SDK_INT){
				Method dataConnSwitchmethod;
				Class<?> telephonyManagerClass;
				Object ITelephonyStub;
				Class<?> ITelephonyClass;

				TelephonyManager telephonyManager = getTelephonyManager(context);
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
				ConnectivityManager cm = getConnectivityManager(context);
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
	 * @param context
	 * @return
	 */
	public static boolean networkConnected(Context context){
		return isWifiEnabled(context) || is3GConnected(context);
	}
	
	public static void toast(Context context, String text){
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}
	
	public static void alert(Context context, String title, String message){
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
	
//	public static int doubleToRatio(){
//		public void testBatteryLevelDisplay(){
//			int a = 83, b=100;
//			
//			double d1 = a*1.0;
//			double d2 = b*1.0;
//			
//			int ratio = (int)((d1/d2)*100);
//			System.out.println(ratio);
//		}
//	}
	
}

package com.spark.app.ocb.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

public class DialogUtils {
	
	public static Context context = null;

	public static enum MsgType {
		DEFAULT, INFO, WARINING, ERROR;
	}

	/**
	 * @param id
	 * @return
	 */
	public static String getResStr(int id)
	{
		return context.getResources().getString(id);
	}

	public static Toast toast(String msg){
		Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		toast.show();
		return toast;
	}

	public static Toast toastResStr(int resId){
		Toast toast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
		toast.show();
		return toast;
	}

	/**
	 * @param msg
	 */
	public static void alert(Context context, String title, String msg)
	{
		int iconId = 0;
		AlertDialog.Builder dlg = new AlertDialog.Builder(context);		
		dlg.setMessage(msg);
		dlg.setPositiveButton(getResStr(android.R.string.ok), null);		
		dlg.setTitle(title);		
		dlg.setIcon(iconId);		
		dlg.create();
		dlg.show();
	}

	/**
	 * @param titleId
	 * @param msgId
	 */
	public static void alertResStr(Context context, int titleId, int msgId)
	{
		alert(context, getResStr(titleId), getResStr(msgId));
	}

	/**
	 * @param msg
	 * @param msgType
	 */
	public static void showMsg(Context context, String title, String msg, MsgType msgType)
	{
		//String sTitle = "";//getResStr(R.string.app_name);
		int iconId = 0;
		
		switch (msgType){
			case INFO:
				//sTitle = "Info";
				iconId = android.R.drawable.ic_dialog_info;// R.drawable.msgicon_info;
				break;
			
			case WARINING:
				//sTitle = "Warning";
				iconId = android.R.drawable.ic_dialog_alert; //R.drawable.msgicon_warning;
				break;
				
			case ERROR:
				//sTitle = "Error";
				iconId = android.R.drawable.ic_dialog_alert; //R.drawable.msgicon_error;
				break;
		}
		
		AlertDialog.Builder dlg = new AlertDialog.Builder(context);		
		dlg.setMessage(msg);
		dlg.setPositiveButton(android.R.string.ok, null);		
		dlg.setTitle(title);		
		dlg.setIcon(iconId);		
		dlg.create();
		dlg.show();
	}	

	/**
	 * @param msg
	 */
	public static AlertDialog confirm(Context context, String msg, DialogInterface.OnClickListener yesClickListener)
	{
		String sTitle = "Confirm";
		int iconId = android.R.drawable.ic_menu_help;
		
		AlertDialog.Builder dlg = new AlertDialog.Builder(context);		
		dlg.setMessage(msg);
		dlg.setPositiveButton(android.R.string.yes, yesClickListener);		
		dlg.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dlg.setTitle(sTitle);		
		dlg.setIcon(iconId);		
		dlg.create();
		return dlg.show();
	}
	
	public static AlertDialog confirm(Context context, int msgId, DialogInterface.OnClickListener yesClickListener)
	{
		return confirm(context, getResStr(msgId), yesClickListener);
	}	
	
	/**
	 * @param resId
	 * @param msgType
	 */
	public static void showMsgResStr(Context context, int titleResId, int resId, MsgType msgType)
	{
		showMsg(context, getResStr(titleResId), getResStr(resId), msgType);
	}

}

package com.letv.recorder.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

public class NetworkUtils {
	/**
	 * 网络类型 wifi
	 */
	public static final String Type_WIFI = "wifi";
	/**
	 * 网络类型 4g
	 */
	public static final String Type_4G = "4g";
	/**
	 * 网络类型 3g
	 */
	public static final String Type_3G = "3g";
	/**
	 * 网络类型 2g
	 */
	public static final String Type_2G = "2g";

	public static boolean isWifiNetType(Context context) {
		String type = getNetType(context);
		if (Type_WIFI.equals(type)) {
			return true;
		}
		return false;
	}

	/**
	 * 没有任何网络返回null
	 * 
	 * @return
	 */
	public static String getNetType(Context context) {
		if (context == null) {
			return null;
		}
		String type = null;
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni != null && ni.isConnectedOrConnecting()) {
			switch (ni.getType()) {
			case ConnectivityManager.TYPE_WIFI:
				type = Type_WIFI;
				break;
			case ConnectivityManager.TYPE_MOBILE:
				switch (ni.getSubtype()) {
				case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2g
				case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2g
				case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2g
				case TelephonyManager.NETWORK_TYPE_1xRTT:
				case TelephonyManager.NETWORK_TYPE_IDEN:
					type = Type_2G;
					break;
				case TelephonyManager.NETWORK_TYPE_EVDO_A: //电信3g
				case TelephonyManager.NETWORK_TYPE_UMTS:
				case TelephonyManager.NETWORK_TYPE_EVDO_0:
				case TelephonyManager.NETWORK_TYPE_HSDPA:
				case TelephonyManager.NETWORK_TYPE_HSUPA:
				case TelephonyManager.NETWORK_TYPE_HSPA:
				case TelephonyManager.NETWORK_TYPE_EVDO_B:
				case TelephonyManager.NETWORK_TYPE_EHRPD:
				case TelephonyManager.NETWORK_TYPE_HSPAP:
					type = Type_3G;
					break;
				case TelephonyManager.NETWORK_TYPE_LTE:
					type = Type_4G;
					break;
				default:
					type = null;
				}
				break;
			default:
				type = null;
			}

		}
		return type;
	}
	/**
	 * wifi状态下返回true
	 * @param context
	 * @return
	 */
	  public static boolean getNetWorkStatus(Context context) {
	        ConnectivityManager cm = (ConnectivityManager) context
	                .getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
	        if (null != activeNetwork && activeNetwork.isConnectedOrConnecting()) {
	            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
	                return true;

	            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
	                return false;
	        }
	        return false;
	    }

	public static boolean isNetAvailable(Context paramContext) {
		boolean isAvilable = false;
		try {
			ConnectivityManager connectManger = (ConnectivityManager) paramContext.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo localNetworkInfo = connectManger.getActiveNetworkInfo();
			// connectManger.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (localNetworkInfo != null) {
				isAvilable = localNetworkInfo.isAvailable();
			}
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return isAvilable;
	}

	public static String getMacaddress(Context context) {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}

	public static long getTotalRxBytes() {
		return TrafficStats.getTotalRxBytes() == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024); // 转为KB
	}

	/*
	 * 截取域名
	 */
	public static String getTopDomainWithoutSubdomain(String url) {
		try {
			String host = new URL(url).toString().toLowerCase();
			// 此处获取值转换为小写
			Pattern pattern = Pattern
					.compile("(http://|)((\\w)+\\.)+\\w+");
			Matcher matcher = pattern.matcher(host);
			while (matcher.find()) {
				return matcher.group();
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}

package com.ezmo.sms.util;

import android.util.Log;

public class L {

	public static final boolean flag = true;
	public static final String TAG = "EZMO";

	public static void d(String msg)
	{

		if (flag)
		{
			Log.d(TAG, msg);
		}
	}

	public static void e(String msg)
	{

		if (flag)
		{
			Log.e(TAG, msg);
		}
	}

	public static void e(long msg)
	{

		if (flag)
		{
			Log.e(TAG, msg + "");
		}
	}

	public static void e(int msg)
	{

		if (flag)
		{
			Log.e(TAG, msg + "");
		}
	}
}

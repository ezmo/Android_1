package com.ezmo.sms.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Util {

	public static String getStringDateByDate(Date date)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy년MM월dd일");
		String dateString = sdf.format(date);
		return dateString;
	}

	public static Date stringToDate(String dateStr)
	{
		Date date = null;
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			date = sdf.parse(dateStr);
		} catch (Exception e)
		{
			L.d("stringToDate error");
		}
		return date;
	}

	public static Calendar getCalendarByDate(Date date)
	{
		TimeZone tz = TimeZone.getDefault();
		Calendar c = Calendar.getInstance(tz);
		c.setTime(date);
		return c;
	}

	// 콤마 추가
	public static String convertCommaString(String data)
	{
		int result = Integer.parseInt(data);
		return new java.text.DecimalFormat("#,###").format(result);
	}

	// 콤마 제거
	public static String removeCommaString(String data)
	{
		return data.replaceAll("\\,", "");
	}
}

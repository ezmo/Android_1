package com.ezmo.sms.db;

import android.provider.BaseColumns;

public class SmsTables {

	public class SmsTable implements BaseColumns {

		// public final static String TABLE_NAME = "title";
		public final static String TABLE_NAME = "SMS";
		public static final String DATE = "date";
		public static final String TIME = "time";
		public static final String ACCOUNTNUMBER = "accountnumber";
		public static final String USERNAME = "username";
		public static final String MONEY = "money";
		public static final String TIMESTAMP = "timestamp";
		public static final String SENDSTATUS = "sendstatus";
		public static final String ORIGINAL = "original";
		public static final String PHONENUMBER = "phonenumber";
		//
		// _ID는 baseColunmn이 가지고 있음.

		public final static String TABLE_CREATE_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + _ID + " integer primary key autoincrement," + DATE + " text," + TIME + " text," + ACCOUNTNUMBER + " text,"
				+ USERNAME + " text," + MONEY + " text," + TIMESTAMP + " text," + SENDSTATUS + " text," + ORIGINAL + " text," + PHONENUMBER + ")";

		public final static String TABLE_DROP_SQL = "drop table if exists " + TABLE_NAME;

		// 테이블이 있는 지 확인
		public final static String TABLE_CHECK_BY_NAME = "SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name = '" + TABLE_NAME + "'";
	}
}

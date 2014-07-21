package com.ezmo.sms.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.ezmo.sms.util.L;

public class SmsDBHelper {
	public static final String DB_NAME = "sms.db";
	private static final int DB_VERSION = 1;

	private SQLiteDatabase db;
	private SmsDAO dao;
	MonitorDBOpenHelper openHepler = null;

	public SmsDBHelper(Context context)
	{
		// boolean existDB = DBUtil.existDBFile(context);
		// if (!existDB)
		// {
		// L.d("DB가 존재하지 않습니다. assets에서 복사합니다.");
		// DBUtil.copySQLiteDB(context);
		// }

		openHepler = new MonitorDBOpenHelper(context, DB_NAME, null, DB_VERSION);
		db = openHepler.getWritableDatabase();
		dao = new SmsDAO(db);
	}

	public void dbClose()
	{
		db.close();
		db = null;
	}

	public void helperClose()
	{
		if (openHepler != null)
		{
			openHepler.close();
			openHepler = null;
		}
	}

	public SmsDAO getDAO()
	{

		return this.dao;
	}

	class MonitorDBOpenHelper extends SQLiteOpenHelper {

		Context context;

		public MonitorDBOpenHelper(Context context, String name, CursorFactory factory, int version)
		{
			super(context, name, factory, version);
			this.context = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
			db.execSQL(SmsTables.SmsTable.TABLE_CREATE_SQL);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			// db.execSQL(LottoTables.ContactTable.TABLE_DROP_SQL);
			// onCreate(db);
			L.d("새 DB가 검색되었습니다. Info:Old-" + oldVersion + " info:new-" + newVersion);
			DBUtil.copySQLiteDB(context);
			openHepler = new MonitorDBOpenHelper(context, DB_NAME, null, DB_VERSION);
			db = openHepler.getWritableDatabase();
			dao = new SmsDAO(db);
		}
	}
}

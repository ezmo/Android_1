package com.ezmo.sms.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SmsDAO {
	private SQLiteDatabase db;

	public SmsDAO(SQLiteDatabase db)
	{
		this.db = db;
	}

	public long insert(SmsModel dto)
	{
		long result = 0;

		ContentValues v = new ContentValues();

		v.put(SmsTables.SmsTable.DATE, dto.getDate());
		v.put(SmsTables.SmsTable.TIME, dto.getTime());
		v.put(SmsTables.SmsTable.ACCOUNTNUMBER, dto.getAccountNumber());
		v.put(SmsTables.SmsTable.USERNAME, dto.getUserName());
		v.put(SmsTables.SmsTable.MONEY, dto.getMoney());
		v.put(SmsTables.SmsTable.TIMESTAMP, dto.getTimeStamp());
		v.put(SmsTables.SmsTable.SENDSTATUS, dto.isSendStatus());
		v.put(SmsTables.SmsTable.ORIGINAL, dto.getOriginalMessage());
		v.put(SmsTables.SmsTable.PHONENUMBER, dto.getPhoneNumber());
		result = db.insert(SmsTables.SmsTable.TABLE_NAME, null, v);

		return result;
	}

	public int updateContact(String id)
	{
		ContentValues values = new ContentValues();
		values.put(SmsTables.SmsTable.SENDSTATUS, "true");

		// updating row
		return db.update(SmsTables.SmsTable.TABLE_NAME, values, SmsTables.SmsTable._ID + " = ?", new String[] { id + "" });
	}

	public ArrayList<SmsModel> selectAll()
	{
		ArrayList<SmsModel> list = new ArrayList<SmsModel>();

		Cursor c = db.query(SmsTables.SmsTable.TABLE_NAME, null, null, null, null, null, "_id desc");

		int size = c.getCount();
		c.moveToFirst();
		for (int i = 0; i < size; i++)
		{

			SmsModel m = new SmsModel();

			m.setId(c.getInt(c.getColumnIndex(SmsTables.SmsTable._ID)));

			m.setDate(c.getString(c.getColumnIndex(SmsTables.SmsTable.DATE)));

			m.setTime(c.getString(c.getColumnIndex(SmsTables.SmsTable.TIME)));

			m.setAccountNumber(c.getString(c.getColumnIndex(SmsTables.SmsTable.ACCOUNTNUMBER)));

			m.setUserName(c.getString(c.getColumnIndex(SmsTables.SmsTable.USERNAME)));

			m.setMoney(c.getInt(c.getColumnIndex(SmsTables.SmsTable.MONEY)));

			m.setTimeStamp(c.getLong(c.getColumnIndex(SmsTables.SmsTable.TIMESTAMP)));

			m.setOriginalMessage(c.getString(c.getColumnIndex(SmsTables.SmsTable.ORIGINAL)));

			m.setPhoneNumber(c.getString(c.getColumnIndex(SmsTables.SmsTable.PHONENUMBER)));

			boolean flag = Boolean.parseBoolean(c.getString(c.getColumnIndex(SmsTables.SmsTable.SENDSTATUS)));
			m.setSendStatus(flag);

			list.add(m);
			c.moveToNext();
		}
		c.close();
		c = null;

		return list;
	}

	// public ArrayList<SmsModel> getLottoNumberByMonth()
	// {
	// ArrayList<SmsModel> list = new ArrayList<SmsModel>();
	//
	// Cursor c = db.query(SmsTables.SmsTable.TABLE_NAME, null, null, null,
	// null, null, null);
	//
	// int size = c.getCount();
	// c.moveToFirst();
	// for (int i = 0; i < size; i++)
	// {
	//
	// SmsModel m = new SmsModel();
	// m.setId(c.getInt(c.getColumnIndex(SmsTables.SmsTable.NUM1)));
	// Date date =
	// Util.stringToDate(c.getString(c.getColumnIndex(SmsTables.SmsTable.DATE)));
	// m.setDate(date);
	// m.setNum1(c.getString(c.getColumnIndex(SmsTables.SmsTable.NUM1)));
	// m.setNum2(c.getString(c.getColumnIndex(SmsTables.SmsTable.NUM2)));
	// m.setNum3(c.getString(c.getColumnIndex(SmsTables.SmsTable.NUM3)));
	// m.setNum4(c.getString(c.getColumnIndex(SmsTables.SmsTable.NUM4)));
	// m.setNum5(c.getString(c.getColumnIndex(SmsTables.SmsTable.NUM5)));
	// m.setNum6(c.getString(c.getColumnIndex(SmsTables.SmsTable.NUM6)));
	// m.setNum7(c.getString(c.getColumnIndex(SmsTables.SmsTable.NUM7)));
	// list.add(m);
	// c.moveToNext();
	// }
	// c.close();
	// c = null;
	//
	// return list;
	// }

	public int delete(long id)
	{
		String whereClause = "_id=" + id;
		int result = db.delete(SmsTables.SmsTable.TABLE_NAME, whereClause, null);
		return result;

	}

	public int selectAllSize()
	{

		Cursor c = db.query(SmsTables.SmsTable.TABLE_NAME, null, null, null, null, null, null);
		int size = c.getCount();
		c.close();
		return size;
	}

	public int getRowCount()
	{
		Cursor c = db.query(SmsTables.SmsTable.TABLE_NAME, null, null, null, null, null, null);
		int i = 0;

		if (c != null)
		{
			i = c.getCount();
		}

		return i;
	}

	public boolean isTableExists()
	{
		boolean flag = false;

		Cursor c = db.rawQuery(SmsTables.SmsTable.TABLE_CHECK_BY_NAME, null);

		if (c != null)
		{
			if (c.getCount() > 0)
			{
				c.moveToFirst();
				int idx = c.getColumnIndex("tbl_name");
				String name = c.getString(idx);
				if (name == null || name.equals(""))
				{
					return false;
				}
				return true;
			}
			c.close();
		}

		return flag;
	}

	public void begineTransaction()
	{
		db.beginTransaction();
	}

	public void endTransaction()
	{
		db.setTransactionSuccessful();
		db.endTransaction();
	}

}